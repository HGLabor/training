package de.hglabor.plugins.training.data

import de.hglabor.plugins.training.data.objects.MlgSettingData
import de.hglabor.plugins.training.data.objects.StreakData

object DataManager {
    fun load() {
        MlgSettingData.load()
        StreakData.load()
    }

    fun save() {
        MlgSettingData.save()
        StreakData.save()
    }
}