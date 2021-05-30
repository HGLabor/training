package de.hglabor.plugins.training.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import de.hglabor.plugins.training.gui.setting.MlgSetting;
import de.hglabor.plugins.training.main.Training;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import java.util.UUID;


public class PacketManager {
    public static void init(Training plugin, ProtocolManager manager) {
        manager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.SPAWN_ENTITY_LIVING) {
            @Override
            public void onPacketSending(PacketEvent event) {
                UUID uuid = event.getPlayer().getUniqueId();
                Bukkit.broadcastMessage("event");
                Entity entity = event.getPacket().getEntityModifier(event).read(0);
                switch (entity.getType()) {
                    case PHANTOM:
                        if (!MlgSetting.TOP_BOTTOM_PHANTOMS.getEnabled(uuid)) event.setCancelled(true);
                        Bukkit.broadcastMessage("Phantom!");
                        break;
                    case SHEEP:
                        if (!MlgSetting.LEVITATOR_SHEEP.getEnabled(uuid)) event.setCancelled(true);
                        Bukkit.broadcastMessage("Sheep");
                        break;
                }
                if (!MlgSetting.LEVITATOR_SHEEP.getEnabled(event.getPlayer().getUniqueId())) {
                    event.setCancelled(true);
                }
            }
        });
    }
}
