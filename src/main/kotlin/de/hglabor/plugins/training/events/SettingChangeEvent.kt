package de.hglabor.plugins.training.events

import de.hglabor.plugins.training.settings.mlg.Setting
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import java.util.*

class SettingChangeEvent(val setting: Setting, val playerUUID: UUID, val targetValue: Boolean) : Event(), Cancellable {
    companion object {
        private val handlers = HandlerList()
        @Suppress("unused")
        @JvmStatic
        fun getHandlerList() = handlers
    }

    private var cancelled: Boolean = false

    override fun getHandlers(): HandlerList = SettingChangeEvent.handlers

    override fun isCancelled(): Boolean = cancelled

    override fun setCancelled(cancel: Boolean) {
        this.cancelled = cancel
    }
}