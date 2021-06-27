package de.hglabor.plugins.training.events

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import de.hglabor.plugins.training.packets.PacketReceiver

/**
 * Called when the OPEN_INVENTORY_ACHIEVEMENT CLIENT_COMMAND packet was sent from the client.
 * Is delayed depending on the ping of the client and therefore not cancellable.
 * @see PacketReceiver.listen
 */
class PlayerInventoryOpenEvent(val player: Player) : Event() {
    val inventory by lazy { player.inventory }
    companion object {
        private val handlers = HandlerList()
        @Suppress("unused")
        @JvmStatic
        fun getHandlerList() = handlers
    }

    override fun getHandlers(): HandlerList = PlayerInventoryOpenEvent.handlers
}