package de.hglabor.plugins.training.data.objects

import de.hglabor.plugins.training.data.loadObj
import de.hglabor.plugins.training.data.saveObj
import de.hglabor.plugins.training.settings.mlg.MlgSetting
import org.bukkit.Bukkit
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MlgSettingData() : Serializable {
    private var enabledMap: HashMap<String, ArrayList<UUID>> = HashMap()

    companion object {
        const val serialVersionUID: Long = -6867189741698444290

        private const val PATH = "mlg_settings"
        fun load() {
            loadObj(PATH)?.let {
                val casted = MlgSettingData(it as MlgSettingData)
                casted.enabledMap.forEach { entry ->
                    Bukkit.getLogger().info("Entry with name ${entry.key} and enabled arraylist size ${entry.value.size} in load()")
                }
                MlgSetting.setEnabledValues(casted.enabledMap)
            }
        }

        fun save() {
            saveObj(PATH, getFromEnum())
            MlgSetting.valuesMap().forEach {
                Bukkit.getLogger().info("Entry with name ${it.key} and enabled arraylist size ${it.value.enabled.size}")
            }
        }

        private fun getFromEnum(): MlgSettingData {
            val d = MlgSettingData()
            MlgSetting.valuesMap().forEach {
                d.enabledMap[it.key] = it.value.getEnabledPlayers()
            }
            return d
        }
    }

    // For loading
    constructor(data: MlgSettingData): this() {
        this.enabledMap = data.enabledMap
    }
}