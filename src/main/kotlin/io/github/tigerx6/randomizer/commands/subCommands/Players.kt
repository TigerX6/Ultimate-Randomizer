package io.github.tigerx6.randomizer.commands.subCommands

import io.github.tigerx6.randomizer.main.Randomizer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration

class Players(plugin: Randomizer) : CommandExecutor {

    private val challengeCommand = plugin.challengeCommand
    private val randomizerPlayers = challengeCommand.randomizerPlayers
    private val config: FileConfiguration = plugin.config
    private var mm = MiniMessage.miniMessage()
    private val prefix: Component = mm.deserialize("${config.getString("plugin-messages.prefix")}")
        .append(Component.text(" "))

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!config.getBoolean("use_player_list")) {
            sender.sendMessage(
                prefix.append(
                    mm.deserialize("${config.getString("plugin-messages.not-using-player-list")}")
                )
            )
        }

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