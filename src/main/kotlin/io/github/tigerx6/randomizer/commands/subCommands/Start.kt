package io.github.tigerx6.randomizer.commands.subCommands

import io.github.tigerx6.randomizer.Randomizer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration

class Start(plugin: Randomizer) : CommandExecutor {

    private val challengeCommand = plugin.challengeCommand
    private var challengeStatus = challengeCommand.challengeStatus
    private val config: FileConfiguration = plugin.config
    private var mm = MiniMessage.miniMessage()
    private val prefix: Component = mm.deserialize("${config.getString("plugin-messages.prefix")}")
        .append(Component.text(" "))

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (challengeStatus == "start") {
            sender.sendMessage(
                prefix.append(
                    mm.deserialize("${config.getString("plugin-messages.already-enabled")}")
                )
            )
            return true
        }

        challengeCommand.challengeStatus = "start"
        Bukkit.broadcast(
            prefix
                .append(mm.deserialize("${config.getString("plugin-messages.randomizer-on")}"))
        )

        if (config.getBoolean("show_timer")) {
            challengeCommand.stopTimer()
            challengeCommand.startTimer()
        }
        return true
    }
}