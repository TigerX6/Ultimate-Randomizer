package io.github.tigerx6.randomizer.commands.baseCommand

import io.github.tigerx6.randomizer.commands.subCommands.*
import io.github.tigerx6.randomizer.main.Randomizer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds

class ChallengeCommand(private val plugin: Randomizer) : TabExecutor {

    val mobDeathListener = plugin.mobDeathListener
    val blockBreakListener = plugin.blockBreakListener
    var challengeStatus = "end"
    val randomizerPlayers: MutableList<String> = mutableListOf()
    val onlinePlayers: MutableList<String> = mutableListOf()
    private val config: FileConfiguration = plugin.config
    private var mm = MiniMessage.miniMessage()
    private val prefix: Component = mm.deserialize("${config.getString("plugin-messages.prefix")}")
        .append(Component.text(" "))

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        onlinePlayers.clear()
        for (player in Bukkit.getOnlinePlayers()) {
            onlinePlayers.add(player.name)
        }

        // error handling
        if (sender !is Player) {
            sender.sendMessage(
                prefix.append(
                    mm.deserialize("${config.getString("plugin-messages.not-player-error")}")
                )
            )
            return true
        }

        fun sendPermissionError(): Boolean {
            sender.sendMessage(
                prefix.append(
                    mm.deserialize("${config.getString("plugin-messages.permission-error")}")
                )
            )
            return true
        }

        fun sendArgsError(): Boolean {
            sender.sendMessage(
                prefix.append(
                    mm.deserialize("${config.getString("plugin-messages.argument-error")}")
                )
            )
            return true
        }

        // subcommands
        if (args.isNotEmpty()) {
            // start
            if (args[0] == "start") {
                if (sender.hasPermission("randomizer.start")) {
                    return if (args.size == 1) {
                        if (randomizerPlayers.isEmpty() && config.getBoolean("use_player_list")) {
                            sender.sendMessage(
                                prefix.append(
                                    mm.deserialize("${config.getString("plugin-messages.empty-player-list")}")
                                )
                            )
                            return true
                        }
                        return Start(plugin).onCommand(sender, command, label, args)
                    } else {
                        sendArgsError()
                    }
                } else {
                    return sendPermissionError()
                }
            }

            // stop
            if (args[0] == "stop") {
                return if (sender.hasPermission("randomizer.stop")) {
                    if (args.size == 1) {
                        Stop(plugin).onCommand(sender, command, label, args)
                    } else {
                        sendArgsError()
                    }
                } else {
                    sendPermissionError()
                }
            }

            // shuffle
            if (args[0] == "shuffle") {
                return if (sender.hasPermission("randomizer.shuffle")) {
                    if (args.size <= 2) {
                        Shuffle(plugin, this).onCommand(sender, command, label, args)
                    } else {
                        sendArgsError()
                    }
                } else {
                    sendPermissionError()
                }
            }

            // players
            if (args[0] == "players") {
                if (args.size == 1) {
                    return if (sender.hasPermission("randomizer.players")) {
                        Players(plugin).onCommand(sender, command, label, args)
                    } else {
                        sendPermissionError()
                    }
                }
                if (args.size <= 3) {
                    if (args[1] == "add") {
                        return if (sender.hasPermission("randomizer.players.add")) {
                            PlayersAdd(plugin).onCommand(sender, command, label, args)
                        } else {
                            sendPermissionError()
                        }
                    }
                    if (args[1] == "remove") {
                        return if (sender.hasPermission("randomizer.players.remove")) {
                            PlayersRemove(plugin).onCommand(sender, command, label, args)
                        } else {
                            sendPermissionError()
                        }
                    }
                }
                return sendArgsError()
            }
        } else if (challengeStatus == "end") {
            sender.sendMessage(
                prefix.append(
                    mm.deserialize("${config.getString("plugin-messages.status-off")}")
                )
            )
            return true
        } else {
            sender.sendMessage(
                prefix.append(
                    mm.deserialize("${config.getString("plugin-messages.status-on")}")
                )
            )
            return true
        }
        return sendArgsError()
    }

    // Timer
    private val executorService: ScheduledExecutorService = Executors.newScheduledThreadPool(0)
    private var scheduledFuture: ScheduledFuture<*>? = null
    private var elapsedS = 0.seconds
    var timerText = ""

    fun startTimer() {
        scheduledFuture = executorService.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    elapsedS += 1.seconds
                    timerText = elapsedS.toComponents { hours, minutes, seconds, _ ->
                        String.format("%02d : %02d : %02d", hours, minutes, seconds)
                    }
                    for (player in Bukkit.getOnlinePlayers()) {
                        if (!randomizerPlayers.contains(player.name) && config.getBoolean("use_player_list")) return
                        player.sendActionBar(
                            mm.deserialize("<gradient:yellow:gold:1>$timerText")
                                .decorate(TextDecoration.BOLD)
                        )
                    }
                }
            },
            0, 1, TimeUnit.SECONDS
        )
    }

    fun stopTimer() {
        scheduledFuture?.cancel(false)
        elapsedS = 0.seconds
    }

    // Tab-Completion
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): MutableList<String> {
        onlinePlayers.clear()
        for (player in Bukkit.getOnlinePlayers()) {
            onlinePlayers.add(player.name)
        }

        val addReturnList = onlinePlayers.toMutableList()
        val removeReturnList = randomizerPlayers.toMutableList()
        addReturnList.removeAll(randomizerPlayers)

        if (addReturnList.isNotEmpty() && !addReturnList.contains("@a")) {
            addReturnList.add("@a")
        }
        if (randomizerPlayers.isNotEmpty() && !removeReturnList.contains("@a")) {
            removeReturnList.add("@a")
        }

        return if (args.size == 1) {
            mutableListOf("start", "stop", "shuffle", "players")
        } else if (args.size == 2) {
            if (args[0] == "players") {
                mutableListOf("add", "remove")
            } else if (args[0] == "shuffle") {
                mutableListOf("mobs", "blocks")
            } else {
                mutableListOf()
            }
        } else if (args.size == 3) {
            if (args[1] == "add") {
                addReturnList
            } else if (args[1] == "remove") {
                removeReturnList
            } else {
                mutableListOf()
            }
        } else {
            mutableListOf()
        }
    }
}
