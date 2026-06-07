package io.github.tigerx6.randomizer.database

import io.github.tigerx6.randomizer.Randomizer
import org.bukkit.Material
import java.sql.Connection
import java.sql.SQLException

class Database(private val plugin: Randomizer) {

    private val dataSource = plugin.dataSource

    fun initTables() {
        var sql =
            "CREATE TABLE IF NOT EXISTS blockPairs (block TEXT NOT NULL, item TEXT NOT NULL, PRIMARY KEY (block));"
        for (i in 1..2) {
            try {
                getConnection().use { connection ->
                    connection.prepareStatement(sql).use { statement ->
                        statement.executeUpdate()
                    }
                }
            } catch (e: SQLException) {
                plugin.logger.info(e.message)
            }
            sql =
                "CREATE TABLE IF NOT EXISTS mobPairs (mob_drop TEXT NOT NULL, item TEXT NOT NULL, PRIMARY KEY (mob_drop));"
        }
    }

    fun savePair(table: String, key: String, value: String) {
        when (table) {
            "blocks" -> {
                val sql = "INSERT INTO BlockPairs VALUES (?, ?);"
                try {
                    getConnection().use { connection ->
                        connection.prepareStatement(sql).use { statement ->
                            statement.setString(1, key)
                            statement.setString(2, value)
                            statement.executeUpdate()
                        }
                    }
                } catch (e: SQLException) {
                    plugin.logger.info(e.message)
                }
            }

            "mobs" -> {
                val sql = "INSERT INTO MobPairs VALUES (?, ?);"
                try {
                    getConnection().use { connection ->
                        connection.prepareStatement(sql).use { statement ->
                            statement.setString(1, key)
                            statement.setString(2, value)
                            statement.executeUpdate()
                        }
                    }
                } catch (e: SQLException) {
                    plugin.logger.info(e.message)
                }
            }
        }
    }

    fun deleteData(table: String) {
        when (table) {
            "blocks" -> {
                val sql = "DELETE FROM BlockPairs;"
                try {
                    getConnection().use { connection ->
                        connection.prepareStatement(sql).use { statement ->
                            statement.executeUpdate()
                        }
                    }
                } catch (e: SQLException) {
                    plugin.logger.info(e.message)
                }
            }

            "mobs" -> {
                val sql = "DELETE FROM MobPairs;"
                try {
                    getConnection().use { connection ->
                        connection.prepareStatement(sql).use { statement ->
                            statement.executeUpdate()
                        }
                    }
                } catch (e: SQLException) {
                    plugin.logger.info(e.message)
                }
            }
        }
    }

    fun getData(table: String): MutableMap<Material, Material> {
        val map: MutableMap<Material, Material> = mutableMapOf()
        when (table) {
            "blocks" -> {
                val sql = "SELECT * FROM BlockPairs;"
                try {
                    getConnection().use { connection ->
                        connection.prepareStatement(sql).use { statement ->
                            statement.executeQuery().use { rs ->
                                while (rs.next()) {
                                    try {
                                        val block = Material.getMaterial(rs.getString(1))
                                        val drop = Material.getMaterial(rs.getString(2))
                                        if (block != null && drop != null) {
                                            map[block] = drop
                                        }
                                    } catch (e: SQLException) {
                                        plugin.logger.info(e.message)
                                    }
                                }
                            }
                        }
                    }
                } catch (e: SQLException) {
                    plugin.logger.info(e.message)
                }
            }

            "mobs" -> {
                val sql = "SELECT * FROM mobPairs;"
                try {
                    getConnection().use { connection ->
                        connection.prepareStatement(sql).use { statement ->
                            statement.executeQuery().use { rs ->
                                while (rs.next()) {
                                    try {
                                        val item = Material.getMaterial(rs.getString(1))
                                        val drop = Material.getMaterial(rs.getString(2))
                                        if (item != null && drop != null) {
                                            map[item] = drop
                                        }
                                    } catch (e: SQLException) {
                                        plugin.logger.info(e.message)
                                    }
                                }
                            }
                        }
                    }
                } catch (e: SQLException) {
                    plugin.logger.info(e.message)
                }
            }
        }
        return map
    }

    private fun getConnection(): Connection {
        return dataSource!!.connection
    }
}