package de.hglabor.plugins.training.data

import de.hglabor.plugins.training.data.objects.SettingData
import de.hglabor.plugins.training.data.objects.StreakData

object DataManager {
    fun load() {
        SettingData.load()
        StreakData.load()
    }

    fun save() {
        SettingData.save()
        StreakData.save()
    }
}