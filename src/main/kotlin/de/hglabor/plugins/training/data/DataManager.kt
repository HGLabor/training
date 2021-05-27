package de.hglabor.plugins.training.data

import de.hglabor.plugins.training.data.objects.MlgSettingData

object DataManager {
    fun load() {
        MlgSettingData.load()
    }

    fun save() {
        MlgSettingData.save()
    }
}