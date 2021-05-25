package de.hglabor.plugins.training.database

import de.hglabor.plugins.training.settings.mlg.MlgSetting
import net.axay.blueutils.database.mongodb.MongoDB

object DatabaseManager {
    private const val PREFIX = "training_"

    private val mongoDB = MongoDB(DatabaseConfig.databaseLoginInformation, spigot = true)
    //private val db = mongoDB.getDatabase("training_database")

    val mlgSettings = mongoDB.getCollectionOrCreate<MlgSetting>(PREFIX + "mlgsettings")

    fun startup() {
        MlgSetting.getValuesFromDB()
    }

    fun shutdown() {
        MlgSetting.saveValuesToDB()

        mongoDB.close()
    }
}