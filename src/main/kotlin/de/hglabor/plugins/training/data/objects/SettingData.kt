package de.hglabor.plugins.training.data.objects

import de.hglabor.plugins.training.data.loadObj
import de.hglabor.plugins.training.data.saveObj
import de.hglabor.plugins.training.settings.mlg.Setting
import java.io.Serializable
import java.util.*

class SettingData() : Serializable {
    private var enabledMap: HashMap<String, ArrayList<UUID>> = HashMap()

    companion object {
        const val serialVersionUID: Long = -6867189741698444290

        private const val PATH = "mlg_settings"
        fun load() {
            loadObj(PATH)?.let {
                Setting.setEnabledValues(SettingData(it as SettingData).enabledMap)
            }
        }

        fun save() {
            saveObj(PATH, getFromEnum())
        }

        private fun getFromEnum(): SettingData {
            val data = SettingData()
            Setting.valuesMap().forEach {
                data.enabledMap[it.key] = it.value.getEnabledPlayers()
            }
            return data
        }
    }

    // For loading
    constructor(data: SettingData): this() {
        this.enabledMap = data.enabledMap
    }
}