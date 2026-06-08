package io.github.tigerx6.randomizer.commands.subCommands

import io.github.tigerx6.randomizer.Randomizer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class PlayersAdd(private val plugin: Randomizer) : CommandExecutor {

    private val randomizerCommand = plugin.randomizerCommand
    private val randomizerPlayers = randomizerCommand.randomizerPlayers
    private val onlinePlayers = randomizerCommand.onlinePlayers
    private var mm = MiniMessage.miniMessage()
    private val prefix: Component = mm.deserialize("${plugin.config.getString("plugin-messages.prefix")}")

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.size == 3) {
            if (args[2] in onlinePlayers) {
                if (args[2] !in randomizerPlayers) {
                    randomizerCommand.randomizerPlayers.add(args[2])
                    sender.sendMessage(
                        prefix.append(
                            mm.deserialize(
                                "${plugin.config.getString("plugin-messages.added-player")}",
                                Placeholder.component("player", Component.text(args[2], NamedTextColor.GOLD))
                            )
                        )
                    )
                } else {
                    sender.sendMessage(
                        prefix.append(
                            mm.deserialize("${plugin.config.getString("plugin-messages.player-already-added")}")
                        )
                    )
                }
            } else if (args[2] == "@a") {
                randomizerCommand.randomizerPlayers.clear()
                for (player in onlinePlayers) {
                    randomizerCommand.randomizerPlayers.add(player)
                }
                sender.sendMessage(
                    prefix.append(
                        mm.deserialize("${plugin.config.getString("plugin-messages.added-all-players")}")
                    )
                )
            } else {
                sender.sendMessage(
                    prefix.append(
                        mm.deserialize("${plugin.config.getString("plugin-messages.argument-error")}")
                    )
                )
            }
        } else {
            sender.sendMessage(
                prefix.append(
                    mm.deserialize("${plugin.config.getString("plugin-messages.argument-error")}")
                )
            )
        }
        return true
    }
}