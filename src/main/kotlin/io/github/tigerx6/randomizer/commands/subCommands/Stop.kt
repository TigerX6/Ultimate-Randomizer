package io.github.tigerx6.randomizer.commands.subCommands

import io.github.tigerx6.randomizer.Randomizer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class Stop(private val plugin: Randomizer) : CommandExecutor {

    private val randomizerCommand = plugin.randomizerCommand
    private var randomizerStatus = randomizerCommand.randomizerStatus
    private var mm = MiniMessage.miniMessage()
    private val prefix: Component = mm.deserialize("${plugin.config.getString("plugin-messages.prefix")}")

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (randomizerStatus == "end") {
            sender.sendMessage(
                prefix.append(
                    mm.deserialize("${plugin.config.getString("plugin-messages.already-disabled")}")
                )
            )
            return true
        }

        randomizerCommand.randomizerStatus = "end"
        Bukkit.broadcast(
            prefix
                .append(mm.deserialize("${plugin.config.getString("plugin-messages.randomizer-off")}"))
        )

        if (plugin.config.getBoolean("show-timer")) {
            randomizerCommand.stopTimer()
        }
        return true
    }
}