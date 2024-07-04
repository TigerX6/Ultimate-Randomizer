package me.tigerx4.randomizer.listeners

import me.tigerx4.randomizer.main.Randomizer
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack
import kotlin.random.Random


class BlockBreakListener(plugin: Randomizer) : Listener {

    var challengeCommand = plugin.challengeCommand

    private val randomItemMap: MutableMap<Material, Material> = mutableMapOf()
    private val config = plugin.config

    fun shuffle() {
        randomItemMap.clear()
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        if (challengeCommand.challengeStatus == "start") {
            if (config.getBoolean("block_drops.randomize_block_drops")) {
                event.isDropItems = false
                var material = randomItemMap[event.block.type]

                if (material == null) {
                    material = Material.entries[Random.nextInt(0, Material.entries.size)]
                    randomItemMap[event.block.type] = material
                }

                val itemStack = ItemStack(material, Random.nextInt(1, config.getInt("block_drops.max_block_drops")))
                event.player.world.dropItemNaturally(event.block.location, itemStack)
            }
        }
    }
}