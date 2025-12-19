package io.github.tigerx6.randomizer.commands.subCommands

import io.github.tigerx6.randomizer.Randomizer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration

class PlayersRemove(plugin: Randomizer) : CommandExecutor {

    private val challengeCommand = plugin.challengeCommand
    private val randomizerPlayers = challengeCommand.randomizerPlayers
    private val onlinePlayers = challengeCommand.onlinePlayers
    private val config: FileConfiguration = plugin.config
    private var mm = MiniMessage.miniMessage()
    private val prefix: Component = mm.deserialize("${config.getString("plugin-messages.prefix")}")

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.size == 3) {
            if (args[2] in onlinePlayers) {
                if (args[2] in randomizerPlayers) {
                    challengeCommand.randomizerPlayers.remove(args[2])
                    sender.sendMessage(
                        prefix.append(
                            mm.deserialize(
                                "${config.getString("plugin-messages.removed-player")}",
                                Placeholder.component("player", Component.text(args[2], NamedTextColor.GOLD))
                            )
                        )
                    )
                } else {
                    sender.sendMessage(
                        prefix.append(
                            mm.deserialize("${config.getString("plugin-messages.player-already-removed")}")
                        )
                    )
                }
            } else if (args[2] == "@a") {
                challengeCommand.randomizerPlayers.clear()
                sender.sendMessage(
                    prefix.append(
                        mm.deserialize("${config.getString("plugin-messages.removed-all-players")}")
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