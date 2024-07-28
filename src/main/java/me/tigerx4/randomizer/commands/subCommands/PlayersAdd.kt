package me.tigerx4.randomizer.commands.subCommands

import me.tigerx4.randomizer.main.Randomizer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration

class PlayersAdd(plugin: Randomizer) : CommandExecutor {

    private val challengeCommand = plugin.challengeCommand
    private val randomizerPlayers = challengeCommand.randomizerPlayers
    private val onlinePlayers = challengeCommand.onlinePlayers
    private val config: FileConfiguration = plugin.config
    private var mm = MiniMessage.miniMessage()
    private val prefix: Component = mm.deserialize("${config.getString("plugin-messages.prefix")}")
        .append(Component.text(" "))

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.size == 3) {
            if (args[2] in onlinePlayers) {
                if (args[2] !in randomizerPlayers) {
                    challengeCommand.randomizerPlayers.add(args[2])
                    sender.sendMessage(
                        prefix.append(
                            mm.deserialize(
                                "${config.getString("plugin-messages.added-player")}",
                                Placeholder.component("player", Component.text(args[2], NamedTextColor.GOLD))
                            )
                        )
                    )
                } else {
                    sender.sendMessage(
                        prefix.append(
                            mm.deserialize("${config.getString("plugin-messages.player-already-added")}")
                        )
                    )
                }
            } else if (args[2] == "@a") {
                challengeCommand.randomizerPlayers.clear()
                for (player in onlinePlayers) {
                    challengeCommand.randomizerPlayers.add(player)
                }
                sender.sendMessage(
                    prefix.append(
                        mm.deserialize("${config.getString("plugin-messages.added-all-players")}")
                    )
                )
            } else {
                sender.sendMessage(
                    prefix.append(
                        mm.deserialize("${config.getString("plugin-messages.argument-error")}")
                    )
                )
            }
        } else {
            sender.sendMessage(
                prefix.append(
                    mm.deserialize("${config.getString("plugin-messages.argument-error")}")
                )
            )
        }
        return true
    }
}