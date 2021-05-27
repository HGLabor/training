package de.hglabor.plugins.training.settings.mlg

import org.bukkit.Material
import org.bukkit.entity.Player
import java.util.*
import java.io.Serializable as javaSer

enum class MlgSetting (val settingName: String, val icon: Material, var enabled: HashMap<UUID, Boolean> = HashMap()) : javaSer {

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

    fun getEnabledPlayers(): ArrayList<UUID> {
        val players = ArrayList<UUID>()
        enabled.forEach {
            if (it.value) players.add(it.key)
        }
        return players
    }

    fun getEnabledValuesFrom(enabledValues: ArrayList<UUID>) {
        enabledValues.forEach {
            this.enabled[it] = true
        }
    }

    companion object {
        // Get values from the valuesMap
        fun setEnabledValues(enabledMap: HashMap<String, ArrayList<UUID>>) {
            values().forEach { mySetting ->
                val setting = enabledMap[mySetting.settingName]
                setting?.let { otherSetting ->
                    mySetting.getEnabledValuesFrom(otherSetting)
                }
            }
        }

        fun valuesMap(): HashMap<String, MlgSetting> {
            val map = HashMap<String, MlgSetting>()
            values().forEach {
                map[it.settingName] = it
            }
            return map
        }
    }
}