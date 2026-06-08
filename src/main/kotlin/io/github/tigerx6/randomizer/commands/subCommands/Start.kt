package io.github.tigerx6.randomizer.commands.subCommands

import io.github.tigerx6.randomizer.Randomizer
import io.github.tigerx6.randomizer.commands.RandomizerCommand
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class Start(private val plugin: Randomizer, private val randomizerCommand: RandomizerCommand) : CommandExecutor {

    private var randomizerStatus = randomizerCommand.randomizerStatus
    private val blockBreakListener = randomizerCommand.blockBreakListener
    private val mobDeathListener = randomizerCommand.mobDeathListener
    private var mm = MiniMessage.miniMessage()
    private val prefix: Component = mm.deserialize("${plugin.config.getString("plugin-messages.prefix")}")

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (randomizerStatus == "start") {
            sender.sendMessage(
                prefix.append(
                    mm.deserialize("${plugin.config.getString("plugin-messages.already-enabled")}")
                )
            )
            return true
        }

        randomizerCommand.randomizerStatus = "start"
        if (plugin.config.getBoolean("auto-shuffle")) {
            blockBreakListener.shuffle()
            mobDeathListener.shuffle()
            Bukkit.broadcast(prefix.append(mm.deserialize("${plugin.config.getString("plugin-messages.randomizer-on-shuffle")}")))
        } else {
            Bukkit.broadcast(
                prefix
                    .append(mm.deserialize("${plugin.config.getString("plugin-messages.randomizer-on")}"))
            )
        }

        if (plugin.config.getBoolean("show-timer")) {
            randomizerCommand.stopTimer()
            randomizerCommand.startTimer()
        }
        return true
    }
}