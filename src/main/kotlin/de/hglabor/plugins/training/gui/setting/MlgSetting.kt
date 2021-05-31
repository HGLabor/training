package de.hglabor.plugins.training.gui.setting

import org.bukkit.Material
import org.bukkit.entity.Player
import java.util.*

enum class MlgSetting (val settingName: String, val icon: Material, var enabled: HashMap<UUID, Boolean> = HashMap()) {

    JUMP_SNEAK_ELEVATOR("Jump/Sneak Elevator", Material.MAGENTA_GLAZED_TERRACOTTA),
    LEVITATOR_SHEEP("Levitator Sheep", Material.SHEARS),
    TOP_BOTTOM_PHANTOMS("Top/Bottom Phantoms", Material.PHANTOM_MEMBRANE)

    ;

    fun toggle(uuid: UUID) { enabled[uuid] = enabled[uuid]?.not() ?: false }

    // best fun name ever CHANGE MY MIND
    fun setDefaultEnabledIfMissing(uuid: UUID, default: Boolean = false) {
        if (enabled[uuid] != null) return
        enabled[uuid] = default
    }

    fun getEnabled(uuid: UUID) = enabled[uuid] ?: false
    fun getEnabled(player: Player) = enabled[player.uniqueId] ?: false
}