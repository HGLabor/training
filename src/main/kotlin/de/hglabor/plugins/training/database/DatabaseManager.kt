package de.hglabor.plugins.training.database

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import de.hglabor.plugins.training.gui.setting.MlgSetting
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection

object DatabaseManager {
    private val client = KMongo.createClient()
    private val database: MongoDatabase = client.getDatabase("training-plugin")
    val mlgSettings: MongoCollection<MlgSetting> = database.getCollection<MlgSetting>()

    fun startup() {
        MlgSetting.getValuesFromDB()
    }

    fun shutdown() {

    }
}