package de.hglabor.plugins.training.data.objects

import de.hglabor.plugins.training.challenges.mlg.streaks.StreakPlayer
import de.hglabor.plugins.training.challenges.mlg.streaks.StreakPlayers
import de.hglabor.plugins.training.data.loadObj
import de.hglabor.plugins.training.data.saveObj
import java.io.Serializable
import java.util.*

class StreakData() : Serializable {
    var streakPlayers: HashMap<UUID, StreakPlayer> = StreakPlayers.getStreakPlayers()

    companion object {
        const val serialVersionUID: Long = -6867189741698444290

        private const val PATH = "streaks"
        fun load() {
            loadObj(PATH)?.let {
                StreakPlayers.setStreakPlayers(StreakData(it as StreakData).streakPlayers)
            }
        }

        fun save() {
            saveObj(PATH, StreakData())
        }
    }

    // For loading
    constructor(data: StreakData): this() {
        this.streakPlayers = data.streakPlayers
    }
}