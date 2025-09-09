package io.github.tigerx6.randomizer.listeners

import io.github.tigerx6.randomizer.Randomizer
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Mob
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack
import kotlin.random.Random


class MobDeathListener(plugin: Randomizer) : Listener {

    var challengeCommand = plugin.challengeCommand

    private val randomItemMap: MutableMap<Material, Material> = mutableMapOf()
    private val config: FileConfiguration = plugin.config

    fun shuffle() {
        randomItemMap.clear()
    }

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        if (challengeCommand.challengeStatus == "start") {
            if (!challengeCommand.randomizerPlayers.contains(event.entity.killer?.name) && config.getBoolean("use_player_list")) return

            if (event.entity is Mob) {
                if (config.getBoolean("mob_drops.randomize_mob_drops")) {
                    event.drops.replaceAll {
                        var material = randomItemMap[it.type]
                        if (material == null) {
                            do {
                                material = Material.entries[Random.nextInt(0, Material.entries.size)]
                            } while (!material!!.isItem)

                            if (config.getBoolean("save-random-pairs")) {
                                randomItemMap[it.type] = material
                            }
                        }
                        ItemStack(
                            material,
                            Random.nextInt(
                                config.getInt("mob_drops.min_mob_drops"),
                                config.getInt("mob_drops.max_mob_drops")
                            )
                        )
                    }
                }

                if (config.getBoolean("mob_drops.randomize_mob_xp_drops")) {
                    event.droppedExp =
                        Random.nextInt(config.getInt("mob_drops.min_mob_xp"), config.getInt("mob_drops.max_mob_xp"))
                }
            }
        }
    }
}