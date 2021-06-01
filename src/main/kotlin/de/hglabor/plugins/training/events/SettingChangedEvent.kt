package de.hglabor.plugins.training.events

import de.hglabor.plugins.training.settings.mlg.Setting
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import java.util.*

/**
 * Called AFTER a setting was changed
 * @see SettingChangeEvent
 */
class SettingChangedEvent(val setting: Setting, val playerUUID: UUID, val targetValue: Boolean) : Event() {
    companion object {
        private val handlers = HandlerList()
        @Suppress("unused")
        @JvmStatic
        fun getHandlerList() = handlers
    }

    override fun getHandlers(): HandlerList = SettingChangedEvent.handlers
}