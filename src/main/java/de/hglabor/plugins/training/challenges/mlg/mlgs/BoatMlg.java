package de.hglabor.plugins.training.challenges.mlg.mlgs;

import de.hglabor.plugins.training.Training;
import de.hglabor.plugins.training.challenges.mlg.Mlg;
import de.hglabor.plugins.training.user.User;
import de.hglabor.plugins.training.user.UserList;
import de.hglabor.plugins.training.warp.WarpItems;
import de.hglabor.utils.noriskutils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class BoatMlgInfo {
    private boolean hasEnteredBoat;
    private boolean hasDied;

    public boolean hasEnteredBoat() {
        return hasEnteredBoat;
    }

    public void setHasEnteredBoat(boolean hasEnteredBoat) {
        this.hasEnteredBoat = hasEnteredBoat;
    }

    public boolean hasDied() {
        return hasDied;
    }

    public void setHasDied(boolean hasDied) {
        this.hasDied = hasDied;
    }
}

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
        if (!canMlgHere(event.getBlock())) {
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

    public void setMlgReady(Player player) {
        setMaxHealth(player);
        player.setFoodLevel(100);
        player.getInventory().clear();
        player.getInventory().setItem(0, WarpItems.WARP_SELECTOR);
        player.getInventory().setItem(4, mlgItems.get(0));
        player.getInventory().setItem(7, WarpItems.HUB);
        player.getInventory().setItem(8, WarpItems.RESPAWN_ANCHOR);
    }
}
