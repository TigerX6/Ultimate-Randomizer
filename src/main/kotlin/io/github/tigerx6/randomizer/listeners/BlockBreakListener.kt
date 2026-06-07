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
    private val config = plugin.config

    fun shuffle() {
        randomItemMap.clear()
        Bukkit.getScheduler().runTaskAsynchronously(plugin) { _ ->
            database?.deleteData("blocks")
        }
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        if (randomizerCommand.randomizerStatus == "start") {
            if (!randomizerCommand.randomizerPlayers.contains(event.player.name) && config.getBoolean("use_player_list")) return
            if (event.player.gameMode == GameMode.CREATIVE && !config.getBoolean("creative-drops")) return

            if (config.getBoolean("block-drops.randomize-block-drops")) {
                event.isDropItems = false
                var material = randomItemMap[event.block.type]

                if (material == null) {
                    do {
                        material = Material.entries[Random.nextInt(0, Material.entries.size)]
                    } while (!material!!.isItem)

                    if (config.getBoolean("save-random-pairs")) {
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
                        config.getInt("block-drops.min-block-drops"),
                        config.getInt("block-drops.max-block-drops")
                    )
                )
                event.player.world.dropItemNaturally(event.block.location, itemStack)
            }
        }
    }
}