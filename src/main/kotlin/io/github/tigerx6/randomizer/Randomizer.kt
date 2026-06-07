package io.github.tigerx6.randomizer

import com.zaxxer.hikari.HikariDataSource
import io.github.tigerx6.randomizer.commands.RandomizerCommand
import io.github.tigerx6.randomizer.database.Database
import io.github.tigerx6.randomizer.listeners.BlockBreakListener
import io.github.tigerx6.randomizer.listeners.MobDeathListener
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class Randomizer : JavaPlugin() {
    var database: Database? = null
    var dataSource: HikariDataSource? = null

    override fun onEnable() {
        logger.info("Randomizer has loaded!")
        registerEvents()
        registerCommands()
        saveDefaultConfig()
        setupMetrics()
        logger.info("Registered listeners, commands, config")
        setupDatabase()
        logger.info("Connected to database")
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

    private fun setupDatabase() {
        dataSource = HikariDataSource()
        dataSource?.jdbcUrl = "jdbc:sqlite:${File(dataFolder, "data.db")}"
        database = Database(this)
        database?.initTables()

        blockBreakListener.database = database
        mobDeathListener.database = database
        Bukkit.getScheduler().runTaskAsynchronously(this) { _ ->
            blockBreakListener.randomItemMap = database!!.getData("blocks")
            mobDeathListener.randomItemMap = database!!.getData("mobs")
        }
    }
}
