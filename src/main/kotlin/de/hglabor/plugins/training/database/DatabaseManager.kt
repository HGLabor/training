package de.hglabor.plugins.training.database

import de.hglabor.plugins.training.gui.setting.MlgSetting
import net.axay.blueutils.database.mongodb.SyncMongoDB

object DatabaseManager {
    private const val PREFIX = "training_"

    private val mongoDB = SyncMongoDB(DatabaseConfig.databaseLoginInformation, spigot = true)

    val mlgSettings = mongoDB.getCollectionOrCreate<MlgSetting>("${PREFIX}mlgsettings")

    fun startup() {
        MlgSetting.getValuesFromDB()
    }

    fun shutdown() {
        MlgSetting.saveValuesToDB()
    }
}