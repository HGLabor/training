package de.hglabor.plugins.training.settings.mlg

import de.hglabor.plugins.training.database.DatabaseManager
import kotlinx.serialization.SerialName
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.litote.kmongo.findOneById
import java.util.*
import kotlin.collections.HashMap

enum class MlgSetting (@SerialName("_id") val settingName: String, val icon: Material, var enabled: HashMap<UUID, Boolean> = HashMap()) : java.io.Serializable {

    JUMP_SNEAK_ELEVATOR("Jump/Sneak Elevator", Material.MAGENTA_GLAZED_TERRACOTTA),
    LEVITATOR_SHEEP("Levitator Sheep", Material.WHITE_WOOL),
    TOP_BOTTOM_PHANTOMS("Top/Bottom Phantoms", Material.PHANTOM_MEMBRANE)

    ;

    fun toggle(uuid: UUID) { enabled[uuid] = enabled[uuid]?.not() ?: false }

    // best fun name ever CHANGE MY MIND
    fun setDefaultEnabledIfMissing(uuid: UUID, default: Boolean = false) {
        if (enabled[uuid] != null) return
        enabled[uuid] = default
    }

    fun getEnabled(uuid: UUID) = enabled[uuid]!!
    fun getEnabled(player: Player) = enabled[player.uniqueId]!!

    fun getValuesFrom(mlgSetting: MlgSetting) {
        this.enabled = mlgSetting.enabled
    }

    companion object {
        fun getValuesFromDB() {
            values().forEach { mySetting ->
                val mlgSetting: MlgSetting? = DatabaseManager.mlgSettings.findOneById(mySetting.settingName)
                mlgSetting?.let { dbSetting ->
                    mySetting.getValuesFrom(dbSetting)
                    Bukkit.getLogger().info("retrieved a setting from database")
                }
                Bukkit.getLogger().info("MAYBE retrieved a setting from database")
            }
        }

        fun saveValuesToDB() {
            values().forEach {
                DatabaseManager.mlgSettings.insertOne(it)
            }
        }
    }
}