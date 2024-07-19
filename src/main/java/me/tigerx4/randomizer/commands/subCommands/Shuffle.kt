package me.tigerx4.randomizer.commands.subCommands

import me.tigerx4.randomizer.commands.baseCommand.ChallengeCommand
import me.tigerx4.randomizer.main.Randomizer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration

class Shuffle(plugin: Randomizer, challengeCommand: ChallengeCommand) : CommandExecutor {

    private val blockBreakListener = challengeCommand.blockBreakListener
    private val mobDeathListener = challengeCommand.mobDeathListener
    private val config: FileConfiguration = plugin.config
    private var mm = MiniMessage.miniMessage()
    private val prefix: Component = mm.deserialize("${config.getString("plugin-messages.prefix")}")
        .append(Component.text(" "))

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        blockBreakListener.shuffle()
        mobDeathListener.shuffle()
        Bukkit.broadcast(
            prefix
                .append(mm.deserialize("${config.getString("plugin-messages.randomizer-shuffled")}"))
        )
        return true
    }
}

