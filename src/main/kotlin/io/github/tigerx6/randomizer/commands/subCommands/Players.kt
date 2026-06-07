package io.github.tigerx6.randomizer.commands.subCommands

import io.github.tigerx6.randomizer.Randomizer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class Players(plugin: Randomizer) : CommandExecutor {

    private val randomizerCommand = plugin.randomizerCommand
    private val randomizerPlayers = randomizerCommand.randomizerPlayers
    private val config = plugin.config
    private var mm = MiniMessage.miniMessage()
    private val prefix: Component = mm.deserialize("${config.getString("plugin-messages.prefix")}")

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (randomizerPlayers.isNotEmpty()) {
            sender.sendMessage(
                prefix.append(
                    mm.deserialize(
                        "${config.getString("plugin-messages.current-players")}\n${
                            randomizerPlayers.joinToString(",\n")
                        }"
                    )
                )
            )
        } else {
            sender.sendMessage(
                prefix.append(
                    mm.deserialize("${config.getString("plugin-messages.no-current-players")}")
                )
            )
        }
        return true
    }
}