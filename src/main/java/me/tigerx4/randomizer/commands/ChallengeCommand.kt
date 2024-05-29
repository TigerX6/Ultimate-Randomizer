package me.tigerx4.randomizer.commands

import me.tigerx4.randomizer.Randomizer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
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

class ChallengeCommand(plugin: Randomizer) : TabExecutor {
    var challengeStatus = "end"
    private val config: FileConfiguration = plugin.config
    private var mm = MiniMessage.miniMessage()
    private val prefix: Component = Component.text("[")
        .append(mm.deserialize("<rainbow>Randomizer</rainbow>"))
        .append(Component.text("] "))

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (sender !is Player) return false
        if (!sender.hasPermission("Randomizer.randomizer")) {
            sender.sendMessage(prefix.append(
                Component.text("ERROR: ", NamedTextColor.RED)
                    .decorate(TextDecoration.BOLD)
            )
                .append(Component.text("You don't have the required permission to use that", NamedTextColor.RED)))
            return true
        }

        if (args.size != 1) {
            sender.sendMessage(
                prefix.append(
                    Component.text("ERROR: ", NamedTextColor.RED)
                        .decorate(TextDecoration.BOLD)
                )
                    .append(Component.text("Could not run with the arguments supplied", NamedTextColor.RED))
            )
            return false
        }

        if (args[0] == "start") {
            if (challengeStatus == "start") {
                sender.sendMessage(
                    prefix.append(
                        Component.text("ERROR: ", NamedTextColor.RED)
                            .decorate(TextDecoration.BOLD)
                    )
                        .append(Component.text("Randomizer is already ON", NamedTextColor.RED))
                )
                return true
            }

            challengeStatus = "start"
            Bukkit.broadcast(
                prefix
                    .append(Component.text("Randomizer is now "))
                    .append(
                        Component.text("ON", NamedTextColor.GREEN)
                            .decorate(TextDecoration.BOLD)
                    )
            )
            if (config.getBoolean("show_timer")) {
                stopTimer()
                startTimer()
            }
            return true
        }

        if (args[0] == "stop") {
            if (challengeStatus == "end") {
                sender.sendMessage(
                    prefix.append(
                        Component.text("ERROR: ", NamedTextColor.RED)
                            .decorate(TextDecoration.BOLD)
                    )
                        .append(Component.text("Randomizer is already OFF", NamedTextColor.RED))
                )
                return true
            }

            challengeStatus = "end"
            Bukkit.broadcast(
                prefix
                    .append(Component.text("Randomizer is now "))
                    .append(
                        Component.text("OFF", NamedTextColor.RED)
                            .decorate(TextDecoration.BOLD)
                    )
            )
            if (config.getBoolean("show_timer")) {
                stopTimer()
            }
            return true
        }

        sender.sendMessage(
            prefix.append(
                Component.text("ERROR: ", NamedTextColor.RED)
                    .decorate(TextDecoration.BOLD)
            )
                .append(Component.text("Could not run with the arguments supplied. ", NamedTextColor.RED))
        )
        return false
    }

    private val executorService: ScheduledExecutorService = Executors.newScheduledThreadPool(0)
    private var scheduledFuture: ScheduledFuture<*>? = null
    private var elapsedS = 0.seconds

    private fun startTimer() {
        scheduledFuture = executorService.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    elapsedS += 1.seconds
                    val timeText = elapsedS.toComponents { hours, minutes, seconds, _ ->
                        String.format("%02d : %02d : %02d", hours, minutes, seconds)
                    }
                    for (player in Bukkit.getOnlinePlayers()) {
                        player.sendActionBar(
                            mm.deserialize("<gradient:yellow:gold:1>$timeText</gradient>")
                                .decorate(TextDecoration.BOLD)
                        )
                    }
                }
            },
            0, 1, TimeUnit.SECONDS
        )
    }

    private fun stopTimer() {
        scheduledFuture?.cancel(false)
        elapsedS = 0.seconds
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): MutableList<String> {
        return if (args.size == 1) {
            mutableListOf("start", "stop")
        } else {
            mutableListOf("")
        }
    }
}