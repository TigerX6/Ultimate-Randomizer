package io.github.tigerx6.randomizer.listeners

import io.github.tigerx6.randomizer.Randomizer
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack
import kotlin.random.Random


class BlockBreakListener(private val plugin: Randomizer) : Listener {

    var randomizerCommand = plugin.randomizerCommand
    var database = plugin.database

    var randomItemMap: MutableMap<Material, Material> = mutableMapOf()

    fun shuffle() {
        randomItemMap.clear()
        Bukkit.getScheduler().runTaskAsynchronously(plugin) { _ ->
            database?.deleteData("blocks")
        }
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        if (randomizerCommand.randomizerStatus == "start") {
            if (!randomizerCommand.randomizerPlayers.contains(event.player.name) && plugin.config.getBoolean("use-player-list")) return
            if (event.player.gameMode == GameMode.CREATIVE && !plugin.config.getBoolean("creative-drops")) return

            if (plugin.config.getBoolean("block-drops.randomize-block-drops")) {
                event.isDropItems = false
                var material =
                    if (plugin.config.getBoolean("save-random-pairs")) randomItemMap[event.block.type] else null

                if (material == null) {
                    do {
                        material = Material.entries[Random.nextInt(0, Material.entries.size)]
                    } while (!material!!.isItem)

                    if (plugin.config.getBoolean("save-random-pairs")) {
                        randomItemMap[event.block.type] = material
                        val eventBlock = event.block.type
                        Bukkit.getScheduler().runTaskAsynchronously(plugin) { _ ->
                            database?.savePair("blocks", eventBlock.toString(), material.toString())
                        }
                    }
                }

                val itemStack = ItemStack(
                    material,
                    Random.nextInt(
                        plugin.config.getInt("block-drops.min-block-drops"),
                        plugin.config.getInt("block-drops.max-block-drops")
                    )
                )
                event.player.world.dropItemNaturally(event.block.location, itemStack)
            }
        }
    }
}