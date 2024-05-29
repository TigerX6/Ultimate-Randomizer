package me.tigerx4.randomizer

import me.tigerx4.randomizer.commands.ChallengeCommand
import me.tigerx4.randomizer.listeners.BlockBreakListener
import me.tigerx4.randomizer.listeners.MobDeathListener
import org.bukkit.plugin.java.JavaPlugin

class Randomizer : JavaPlugin() {
    override fun onEnable() {
        logger.info("Randomizer has loaded!")

        val challengeCommand = ChallengeCommand(this)
        server.pluginManager.registerEvents(BlockBreakListener(challengeCommand, this), this)
        server.pluginManager.registerEvents(MobDeathListener(challengeCommand, this), this)

        getCommand("randomizer")?.setExecutor(challengeCommand)
        saveDefaultConfig()
        logger.info("Registered listeners, commands & config")
    }

    override fun onDisable() {
        logger.info("Randomizer has unloaded!")
    }
}
