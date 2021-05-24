package de.hglabor.plugins.training.database

import net.axay.blueutils.database.DatabaseLoginInformation
import net.axay.kspigot.config.PluginFile
import net.axay.kspigot.config.kSpigotJsonConfig

object DatabaseConfig {
    val databaseLoginInformation by kSpigotJsonConfig(PluginFile("databaseLoginInformation.json")) {
        DatabaseLoginInformation.NOTSET_DEFAULT
    }
}