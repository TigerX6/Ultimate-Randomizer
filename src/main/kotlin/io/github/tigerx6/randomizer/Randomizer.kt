package io.github.tigerx6.randomizer

import io.github.tigerx6.randomizer.commands.RandomizerCommand
import io.github.tigerx6.randomizer.listeners.BlockBreakListener
import io.github.tigerx6.randomizer.listeners.MobDeathListener
import org.bstats.bukkit.Metrics
import org.bukkit.plugin.java.JavaPlugin

class Randomizer : JavaPlugin() {
    override fun onEnable() {
        logger.info("Randomizer has loaded!")
        registerEvents()
        registerCommands()
        saveDefaultConfig()
        setupMetrics()
        logger.info("Registered listeners, commands, config")
    }

    override fun onDisable() {
        logger.info("Randomizer has unloaded!")
    }

    val mobDeathListener = MobDeathListener(this)
    val blockBreakListener = BlockBreakListener(this)
    var randomizerCommand = RandomizerCommand(this)


    private fun registerEvents() {
        server.pluginManager.registerEvents(blockBreakListener, this)
        server.pluginManager.registerEvents(mobDeathListener, this)
    }

    private fun registerCommands() {
        // fix for circular references
        mobDeathListener.randomizerCommand = randomizerCommand
        blockBreakListener.randomizerCommand = randomizerCommand

        getCommand("randomizer")?.setExecutor(randomizerCommand)
    }

    private fun setupMetrics() {
        Metrics(this, 27261)
    }
}
