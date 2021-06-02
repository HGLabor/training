package de.hglabor.plugins.training.packets

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import de.hglabor.plugins.training.main.PLUGIN
import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.lang.reflect.InvocationTargetException

object PacketSender {
    fun hideEntities(player: Player, vararg entities: Entity): Boolean {
        val container = PacketContainer(PacketType.Play.Server.ENTITY_DESTROY)
        container.integerArrays.write(0, entities.ids())
        return try {
            PLUGIN.protocolManager.sendServerPacket(player, container)
            true
        }
        catch(e: InvocationTargetException) {
            Bukkit.getLogger().warning("Error while trying to send ENTITY_DESTROY packet:")
            e.printStackTrace()
            false
        }
    }

    fun showEntities(player: Player, entity: Entity) {
        try {
            PLUGIN.protocolManager.sendServerPacket(player, spawnPacket(entity))
            PLUGIN.protocolManager.sendServerPacket(player, PacketReceiver.metaPackets[entity.uniqueId])
        }
        catch(e: InvocationTargetException) {
            Bukkit.getLogger().warning("Error while trying to send SPAWN_ENTITY_LIVING packet:")
            e.printStackTrace()
        }
    }

    private fun spawnPacket(entity: Entity): PacketContainer =  ProtocolLibrary.getProtocolManager()
            .createPacketConstructor(PacketType.Play.Server.SPAWN_ENTITY_LIVING, entity).createPacket(entity)

    private fun Array<out Entity>.ids(): IntArray {
        val array = IntArray(size)
        forEach {
            array[indexOf(it)] = it.entityId
        }
        return array
    }
}
