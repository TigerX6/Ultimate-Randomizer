package io.github.tigerx6.randomizer.commands.subCommands

import io.github.tigerx6.randomizer.commands.ChallengeCommand
import io.github.tigerx6.randomizer.Randomizer
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

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (args.size == 2) {
            if (args[1] == "mobs") {
                mobDeathListener.shuffle()
                Bukkit.broadcast(
                    prefix.append(
                        mm.deserialize("${config.getString("plugin-messages.randomizer-shuffled-mobs")}")
                    )
                )
                return true
            }

            if (args[1] == "blocks") {
                blockBreakListener.shuffle()
                Bukkit.broadcast(
                    prefix.append(
                        mm.deserialize("${config.getString("plugin-messages.randomizer-shuffled-blocks")}")
                    )
                )
                return true
            }
        }

        if (args.size == 1) {
            mobDeathListener.shuffle()
            blockBreakListener.shuffle()

            Bukkit.broadcast(
                prefix
                    .append(mm.deserialize("${config.getString("plugin-messages.randomizer-shuffled")}"))
            )
            return true
        }
        sender.sendMessage(
            prefix.append(
                mm.deserialize("${config.getString("plugin-messages.argument-error")}")
            )
        )
        return true
    }
}

