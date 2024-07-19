package me.tigerx4.randomizer.main

import me.tigerx4.randomizer.commands.baseCommand.ChallengeCommand
import me.tigerx4.randomizer.listeners.BlockBreakListener
import me.tigerx4.randomizer.listeners.MobDeathListener
import org.bukkit.plugin.java.JavaPlugin

class Randomizer : JavaPlugin() {
    override fun onEnable() {
        logger.info("Randomizer has loaded!")
        registerEvents()
        registerCommands()
        saveDefaultConfig()
        logger.info("Registered listeners, commands, config")
    }

    override fun onDisable() {
        logger.info("Randomizer has unloaded!")
    }

    val mobDeathListener = MobDeathListener(this)
    val blockBreakListener = BlockBreakListener(this)
    var challengeCommand = ChallengeCommand(this)


    private fun registerEvents() {
        server.pluginManager.registerEvents(blockBreakListener, this)
        server.pluginManager.registerEvents(mobDeathListener, this)
    }

    private fun registerCommands() {
        // fix for circular references
        mobDeathListener.challengeCommand = challengeCommand
        blockBreakListener.challengeCommand = challengeCommand

        getCommand("randomizer")?.setExecutor(challengeCommand)
    }
}
