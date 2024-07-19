package me.tigerx4.randomizer.commands.baseCommand

import me.tigerx4.randomizer.commands.subCommands.Shuffle
import me.tigerx4.randomizer.commands.subCommands.Start
import me.tigerx4.randomizer.commands.subCommands.Stop
import me.tigerx4.randomizer.main.Randomizer
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
    private val config: FileConfiguration = plugin.config
    private var mm = MiniMessage.miniMessage()
    private val prefix: Component = mm.deserialize("${config.getString("plugin-messages.prefix")}")
        .append(Component.text(" "))

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        // error handling
        if (sender !is Player) {
            sender.sendMessage(
                prefix.append(
                    mm.deserialize("${config.getString("plugin-messages.not-player-error")}")
                )
            )
            return true
        }

        fun sendPermissionError() {
            sender.sendMessage(
                prefix.append(
                    mm.deserialize("${config.getString("plugin-messages.permission-error")}")
                )
            )
        }

        fun sendArgsError() {
            sender.sendMessage(
                prefix.append(
                    mm.deserialize("${config.getString("plugin-messages.argument-error")}")
                )
            )
        }

        // subcommands
        if (args.isNotEmpty()) {
            // start
            if (args[0] == "start") {
                if (sender.hasPermission("randomizer.start")) {
                    return if (args.size == 1) {
                        Start(plugin).onCommand(sender, command, label, args)
                    } else {
                        sendArgsError()
                        return true
                    }
                }
                sendPermissionError()
                return true
            }

            // stop
            if (args[0] == "stop") {
                if (sender.hasPermission("randomizer.stop")) {
                    return if (args.size == 1) {
                        Stop(plugin).onCommand(sender, command, label, args)
                    } else {
                        sendArgsError()
                        return true
                    }
                }
            }

            // shuffle
            if (args[0] == "shuffle") {
                if (sender.hasPermission("randomizer.shuffle")) {
                    return if (args.size == 1) {
                        Shuffle(plugin, this).onCommand(sender, command, label, args)
                    } else {
                        sendArgsError()
                        return true
                    }
                }
            }

            // players
            sendArgsError()
            return false
        }
        sendArgsError()
        return false

        // start subcommand
        /*if (args[0] == "start") {
            if (sender.hasPermission("randomizer.start")) {
                if (challengeStatus == "start") {
                    sender.sendMessage(
                        prefix.append(
                            mm.deserialize("${config.getString("plugin-messages.already-enabled")}")
                        )
                    )
                    return true
                }

                challengeStatus = "start"
                Bukkit.broadcast(
                    prefix
                        .append(mm.deserialize("${config.getString("plugin-messages.randomizer-on")}"))
                )
                if (config.getBoolean("show_timer")) {
                    stopTimer()
                    startTimer()
                }
                return true
            }
            sendPermissionError()
        }*/

        // stop subcommand
        /*if (args[0] == "stop") {
            if (sender.hasPermission("randomizer.stop")) {
                if (challengeStatus == "end") {
                    sender.sendMessage(
                        prefix.append(
                            mm.deserialize("${config.getString("plugin-messages.already-disabled")}")
                        )
                    )
                    return true
                }

                challengeStatus = "end"
                Bukkit.broadcast(
                    prefix
                        .append(mm.deserialize("${config.getString("plugin-messages.randomizer-off")}"))
                )

                if (config.getBoolean("show_timer")) {
                    stopTimer()
                }
                return true
            }
            sendPermissionError()
        }*/

        // shuffle subcommand
        /*if (args[0] == "shuffle") {
            if (sender.hasPermission("randomizer.shuffle")) {
                blockBreakListener.shuffle()
                mobDeathListener.shuffle()
                Bukkit.broadcast(
                    prefix
                        .append(mm.deserialize("${config.getString("plugin-messages.randomizer-shuffled")}"))
                )
                return true
            }
            sendPermissionError()
        }*/
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

        return if (args.size == 1) {
            mutableListOf("start", "stop", "shuffle")
        } else {
            mutableListOf()
        }
    }
}