package de.hglabor.plugins.training.gui.setting

import org.bukkit.Material
import java.util.*
import kotlin.collections.HashMap

enum class MlgSetting (val settingName: String, val icon: Material, var enabled: HashMap<UUID, Boolean> = HashMap()) {

    JUMP_SNEAK_ELEVATOR("Jump/Sneak Elevator", Material.MAGENTA_GLAZED_TERRACOTTA),
    LEVITATOR_SHEEP("Levitator Sheep", Material.SHEARS),
    TOP_BOTTOM_PHANTOMS("Top/Bottom Phantoms", Material.PHANTOM_MEMBRANE)

    ;

    fun toggle(uuid: UUID) { enabled[uuid] = enabled[uuid]?.not() ?: false }

    // best fun name ever CHANGE MY MIND
    fun setDefaultEnabledIfMissing(uuid: UUID) {
        if (enabled[uuid] != null) return
        enabled[uuid] = false
    }
}