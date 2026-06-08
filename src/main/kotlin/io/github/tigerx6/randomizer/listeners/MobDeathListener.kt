package io.github.tigerx6.randomizer.listeners

import io.github.tigerx6.randomizer.Randomizer
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Mob
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack
import kotlin.random.Random


class MobDeathListener(private val plugin: Randomizer) : Listener {

    var randomizerCommand = plugin.randomizerCommand
    var database = plugin.database

    var randomItemMap: MutableMap<Material, Material> = mutableMapOf()

    fun shuffle() {
        randomItemMap.clear()
        Bukkit.getScheduler().runTaskAsynchronously(plugin) { _ ->
            database?.deleteData("mobs")
        }
    }

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        if (randomizerCommand.randomizerStatus == "start") {
            if (!randomizerCommand.randomizerPlayers.contains(event.entity.killer?.name) && plugin.config.getBoolean("use-player-list")) return
            if (event.entity.killer?.gameMode == GameMode.CREATIVE && !plugin.config.getBoolean("creative-drops")) return

            if (event.entity is Mob) {
                if (plugin.config.getBoolean("mob-drops.randomize-mob-drops")) {
                    event.drops.replaceAll {
                        var material = randomItemMap[it.type]
                        if (material == null) {
                            do {
                                material = Material.entries[Random.nextInt(0, Material.entries.size)]
                            } while (!material!!.isItem || plugin.config.getList("item-blacklist", listOf(""))!!
                                    .contains(material.toString())
                            )

                            if (plugin.config.getBoolean("save-random-pairs")) {
                                randomItemMap[it.type] = material
                                val eventDrop = it.type
                                Bukkit.getScheduler().runTaskAsynchronously(plugin) { _ ->
                                    database?.savePair("mobs", eventDrop.toString(), material.toString())
                                }
                            }
                        }
                        ItemStack(
                            material,
                            Random.nextInt(
                                plugin.config.getInt("mob-drops.min-mob-drops"),
                                plugin.config.getInt("mob-drops.max-mob-drops")
                            )
                        )
                    }
                }

                if (plugin.config.getBoolean("mob-drops.randomize-mob-xp-drops")) {
                    event.droppedExp =
                        Random.nextInt(
                            plugin.config.getInt("mob-drops.min-mob-xp"),
                            plugin.config.getInt("mob-drops.max-mob-xp")
                        )
                }
            }
        }
    }
}