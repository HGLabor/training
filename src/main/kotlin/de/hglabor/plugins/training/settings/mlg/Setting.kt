package de.hglabor.plugins.training.settings.mlg

import de.hglabor.plugins.training.events.SettingChangeEvent
import de.hglabor.plugins.training.events.SettingChangedEvent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import java.util.*
import java.io.Serializable as javaSer

enum class Setting (val settingName: String, val icon: Material? = null, val headOwner: String? = null, private var enabled: HashMap<UUID, Boolean> = HashMap(), private val default: Boolean = true, val type: Type = Type.MLG) : javaSer {

    // Mlg Settings
    JUMP_SNEAK_ELEVATOR("Jump/Sneak Elevator", Material.MAGENTA_GLAZED_TERRACOTTA),
    LEVITATOR_SHEEP("Levitator Sheep", headOwner = "Kolish"),
    TOP_BOTTOM_PHANTOMS("Top/Bottom Phantoms", Material.PHANTOM_MEMBRANE),
    SUPPLY_PANDAS("Supply Pandas", headOwner = "Can"),

    // Damager Settings
    COCOA_RECRAFT("Cocoa Recraft", Material.COCOA_BEANS, default = false, type = Type.DAMAGER),
    MOVE_COCOA_RECRAFT("Move Cocoa Recraft one to the left", Material.STRIPPED_JUNGLE_WOOD, default = false, type = Type.DAMAGER),
    MOVE_MUSHROOM_RECRAFT("Move Mushroom Recraft one to the right", Material.RED_MUSHROOM_BLOCK, default = false, type = Type.DAMAGER),

    ;

    enum class Type { MLG, DAMAGER }

    fun toggle(uuid: UUID) {
        // This is the only time the setting is actually changed from the gui
        // so this is the only time we need to call the SettingChangeEvent

        // Call SettingChangeEvent
        val target = enabled[uuid]?.not() ?: default
        val event = SettingChangeEvent(this, uuid, target)
        Bukkit.getPluginManager().callEvent(event)

        if (event.isCancelled) return

        enabled[uuid] = target

        // Call SettingChangedEvent
        Bukkit.getPluginManager().callEvent(SettingChangedEvent(this, uuid, target))
    }

    // best fun name ever CHANGE MY MIND
    fun setDefaultEnabledIfMissing(uuid: UUID, default: Boolean = this.default) {
        if (enabled[uuid] != null) return
        enabled[uuid] = default
    }

    fun getEnabled(uuid: UUID) = enabled[uuid] ?: default
    fun getEnabled(player: Player) = enabled[player.uniqueId] ?: default

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

        fun valuesMap(): HashMap<String, Setting> {
            val map = HashMap<String, Setting>()
            values().forEach {
                map[it.settingName] = it
            }
            return map
        }

        fun typeValues(type: Type): List<Setting> = values().filter { it.type == type }
    }
}