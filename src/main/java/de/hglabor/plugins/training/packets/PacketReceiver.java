package de.hglabor.plugins.training.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import de.hglabor.plugins.training.events.PlayerInventoryOpenEvent;
import de.hglabor.plugins.training.main.Training;
import de.hglabor.plugins.training.main.TrainingKt;
import de.hglabor.plugins.training.settings.mlg.Setting;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;


public class PacketReceiver {
    public static HashMap<UUID, PacketContainer> metaPackets = new HashMap<>();
    public static void listen() {
        // Entity Spawns
        TrainingKt.getPLUGIN().protocolManager.addPacketListener(new PacketAdapter(TrainingKt.getPLUGIN(), ListenerPriority.NORMAL, PacketType.Play.Server.SPAWN_ENTITY_LIVING) {
            @Override
            public void onPacketSending(PacketEvent event) {
                UUID uuid = event.getPlayer().getUniqueId();
                Entity entity = event.getPacket().getEntityModifier(event).read(0);
                switch (entity.getType()) {
                    case PHANTOM:
                        if (!Setting.TOP_BOTTOM_PHANTOMS.getEnabled(uuid)) event.setCancelled(true);
                        break;
                    case SHEEP:
                        if (!Setting.LEVITATOR_SHEEP.getEnabled(uuid)) event.setCancelled(true);
                        break;
                    case PANDA:
                        if (!Setting.SUPPLY_PANDAS.getEnabled(uuid)) event.setCancelled(true);
                        break;
                }
            }
        });
        // Entity Metadata
        TrainingKt.getPLUGIN().protocolManager.addPacketListener(new PacketAdapter(TrainingKt.getPLUGIN(), ListenerPriority.NORMAL, PacketType.Play.Server.ENTITY_METADATA) {
            @Override
            public void onPacketSending(PacketEvent event) {
                Entity entity = event.getPacket().getEntityModifier(event).read(0);
                switch (entity.getType()) {
                    case PHANTOM:
                    case SHEEP:
                    case PANDA:
                        if (!metaPackets.containsKey(entity.getUniqueId())) metaPackets.put(entity.getUniqueId(), event.getPacket());
                        break;
                }
            }
        });

        // Player open inventory
        TrainingKt.getPLUGIN().protocolManager.addPacketListener(new PacketAdapter(TrainingKt.getPLUGIN(), ListenerPriority.NORMAL, PacketType.Play.Client.CLIENT_COMMAND) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Bukkit.getLogger().info("got packet");
                if (event.getPacket().getClientCommands().read(0) == EnumWrappers.ClientCommand.OPEN_INVENTORY_ACHIEVEMENT) {
                    // Call PlayerInventoryOpenEvent
                    Bukkit.getPluginManager().callEvent(new PlayerInventoryOpenEvent(event.getPlayer()));
                    Bukkit.getLogger().info("Called event XD");
                }
            }
        });
        Bukkit.getLogger().info("Added all packet listeners");
    }
}
