package io.github.tigerx6.randomizer.commands

import io.github.tigerx6.randomizer.Randomizer
import io.github.tigerx6.randomizer.commands.subCommands.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.scheduler.BukkitTask
import kotlin.time.Duration.Companion.seconds

class RandomizerCommand(private val plugin: Randomizer) : TabExecutor {

    val mobDeathListener = plugin.mobDeathListener
    val blockBreakListener = plugin.blockBreakListener
    var randomizerStatus = "end"
    val randomizerPlayers: MutableList<String> = mutableListOf()
    val onlinePlayers: MutableList<String> = mutableListOf()
    private val config = plugin.config
    private var mm = MiniMessage.miniMessage()
    private val prefix: Component = mm.deserialize("${config.getString("plugin-messages.prefix")}")

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // update onlinePlayers
        onlinePlayers.clear()
        for (player in Bukkit.getOnlinePlayers()) {
            onlinePlayers.add(player.name)
        }

        fun messageSender(configPath: String) {
            sender.sendMessage(
                prefix.append(
                    mm.deserialize("${config.getString(configPath)}")
                )
            )
        }

        fun sendPermissionError(): Boolean {
            messageSender("plugin-messages.permission-error")
            return true
        }

        fun sendArgError(): Boolean {
            messageSender("plugin-messages.argument-error")
            return true
        }

        // subcommands
        if (args.isNotEmpty()) {
            // start
            if (args[0] == "start") {
                if (sender.hasPermission("randomizer.start")) {
                    return if (args.size == 1) {
                        if (randomizerPlayers.isEmpty() && config.getBoolean("use_player_list")) {
                            messageSender("plugin-messages.empty-player-list")
                            return true
                        }
                        return Start(plugin, this).onCommand(sender, command, label, args)
                    } else {
                        sendArgError()
                    }
                } else {
                    return sendPermissionError()
                }
            }

            // stop
            if (args[0] == "stop") {
                if (sender.hasPermission("randomizer.stop")) {
                    return if (args.size == 1) {
                        Stop(plugin).onCommand(sender, command, label, args)
                    } else {
                        sendArgError()
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
                        sendArgError()
                    }
                } else {
                    sendPermissionError()
                }
            }

            // players
            if (args[0] == "players") {
                if (!config.getBoolean("use_player_list")) {
                    messageSender("plugin-messages.not-using-player-list")
                }
                if (args.size == 1) {
                    return if (sender.hasPermission("randomizer.players")) {
                        Players(plugin).onCommand(sender, command, label, args)
                    } else {
                        sendPermissionError()
                    }
                }
                if (args.size <= 3) {
                    when (args[1]) {
                        "add" -> return when (sender.hasPermission("randomizer.players.add")) {
                            true -> PlayersAdd(plugin).onCommand(sender, command, label, args)
                            else -> sendPermissionError()
                        }

                        "remove" -> return when (sender.hasPermission("randomizer.players.remove")) {
                            true -> PlayersRemove(plugin).onCommand(sender, command, label, args)
                            else -> sendPermissionError()
                        }
                    }
                }
                return sendArgError()
            }
            sendArgError()
        } else if (randomizerStatus == "end" && sender.hasPermission("randomizer.randomizer")) {
            messageSender("plugin-messages.status-off")
            return true
        } else {
            if (sender.hasPermission("randomizer.randomizer")) {
                messageSender("plugin-messages.status-on")
            }
            return true
        }
        return true
    }

    // Timer

    private var elapsedS = 0.seconds
    private var timer: BukkitTask? = null
    private var timerString = ""

    fun startTimer() {
        val timerText = plugin.config.getString("timer-style")
        var timerFormat = plugin.config.getInt("timer-format")
        if (timerFormat !in 1..2) {
            plugin.config.set("timer-format", 1)
            plugin.saveConfig()
            timerFormat = 1
        }
        timer =
            plugin.server.scheduler.runTaskTimer(
                plugin, Runnable {
                    elapsedS += 1.seconds
                    timerString = elapsedS.toComponents { hours, minutes, seconds, _ ->
                        when (timerFormat) {
                            1 -> listOfNotNull(
                                if (hours != 0L) "${hours}h" else null,
                                if (minutes != 0) "${minutes}m" else null,
                                if (seconds != 0) "${seconds}s" else null,
                            ).joinToString(" ")

                            2 -> String.format("%02d : %02d : %02d", hours, minutes, seconds)
                            else -> ""
                        }
                    }
                    for (player in Bukkit.getOnlinePlayers()) {
                        if (!randomizerPlayers.contains(player.name) && config.getBoolean("use_player_list")) continue
                        player.sendActionBar(
                            mm.deserialize("$timerText", Placeholder.unparsed("time", timerString))
                        )
                    }
                },
                0, 20
            )
    }

    fun stopTimer() {
        timer?.cancel()
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
