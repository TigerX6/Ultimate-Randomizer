package me.tigerx4.randomizer.listeners

import me.tigerx4.randomizer.Randomizer
import me.tigerx4.randomizer.commands.ChallengeCommand
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Mob
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack
import kotlin.random.Random


class MobDeathListener(private val challengeCommand: ChallengeCommand, plugin: Randomizer) : Listener {

    private val randomItemMap: MutableMap<Material, Material> = mutableMapOf()
    private val config: FileConfiguration = plugin.config

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        if (challengeCommand.challengeStatus == "start") {
            val entity = event.entity
            if (entity is Mob) {
                if (config.getBoolean("mob_drops.randomize_mob_drops")) {
                    event.drops.replaceAll {
                        var material = randomItemMap[it.type]
                        if (material == null) {
                            material = Material.entries[Random.nextInt(0, Material.entries.size)]
                            randomItemMap[it.type] = material
                        }
                        ItemStack(material, Random.nextInt(1, 10))
                    }
                }
                if (config.getBoolean("mob_drops.randomize_mob_xp_drops")) {
                    event.droppedExp = Random.nextInt(1, config.getInt("mob_drops.max_mob_xp"))
                }
            }
        }
    }
}