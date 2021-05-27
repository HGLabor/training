package de.hglabor.plugins.training.challenges.mlg.mlgs;

import de.hglabor.plugins.training.challenges.mlg.Mlg;
import de.hglabor.utils.noriskutils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BoatMlg extends Mlg {
    private final List<ItemStack> mlgItems;

    public BoatMlg(String name, ChatColor color, Class<? extends Entity> type) {
        super(name, color, type, Material.QUARTZ_BLOCK, Material.GOLD_BLOCK);
        this.mlgItems = new ArrayList<>();
        this.mlgItems.add(new ItemBuilder(Material.OAK_BOAT).setName(ChatColor.AQUA + this.getName() + " MLG").build());
    }

    @EventHandler
    public void onPlayerPlaceBoat(@SuppressWarnings("deprecation") /* api works for this usage */ EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof Boat)) {
            return;
        }
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        if (!(isInChallenge(player))) {
            event.setCancelled(true);
            return;
        }
        if (cantMlgHere(event.getBlock())) {
            event.setCancelled(true);
            return;
        }

        handleMlg(player, 20L);
        // Remove boat after 1 second (20 ticks)
        removeEntityLater(event.getEntity(), 20L);
    }

    @Override
    public List<ItemStack> getMlgItems() {
        return mlgItems;
    }

}
