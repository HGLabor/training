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
                MlgSetting.setEnabledValues(MlgSettingData(it as MlgSettingData).enabledMap)
            }
        }

        fun save() {
            saveObj(PATH, getFromEnum())
        }

        private fun getFromEnum(): MlgSettingData {
            val data = MlgSettingData()
            MlgSetting.valuesMap().forEach {
                data.enabledMap[it.key] = it.value.getEnabledPlayers()
            }
            return data
        }
    }

    // For loading
    constructor(data: MlgSettingData): this() {
        this.enabledMap = data.enabledMap
    }
}