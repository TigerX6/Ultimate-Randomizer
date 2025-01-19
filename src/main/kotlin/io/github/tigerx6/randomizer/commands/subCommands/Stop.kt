package io.github.tigerx6.randomizer.commands.subCommands

import io.github.tigerx6.randomizer.Randomizer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration

class Stop(plugin: Randomizer) : CommandExecutor {

    private val challengeCommand = plugin.challengeCommand
    private var challengeStatus = challengeCommand.challengeStatus
    private val config: FileConfiguration = plugin.config
    private var mm = MiniMessage.miniMessage()
    private val prefix: Component = mm.deserialize("${config.getString("plugin-messages.prefix")}")
        .append(Component.text(" "))

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (challengeStatus == "end") {
            challengeCommand.messageSender("plugin-messages.already-disabled")
            return true
        }

        challengeCommand.challengeStatus = "end"
        Bukkit.broadcast(
            prefix
                .append(mm.deserialize("${config.getString("plugin-messages.randomizer-off")}"))
        )

        if (config.getBoolean("show_timer")) {
            challengeCommand.stopTimer()
        }
        return true
    }
}