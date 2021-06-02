package de.hglabor.plugins.training.warp.worlds;

import de.hglabor.plugins.training.warp.WarpItems;
import de.hglabor.utils.noriskutils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class DamagerWorld extends TrainingWorld {
    public static final DamagerWorld INSTANCE = new DamagerWorld();

    private DamagerWorld() {
        super(new ItemBuilder(Material.STONE_SWORD).setName(ChatColor.RED + "Damager").build());
    }

    public void setItems(Player player) {
        player.getInventory().clear();
        player.getInventory().setItem(0, WarpItems.WARP_SELECTOR);
        player.getInventory().setItem(7, WarpItems.HUB);
        player.getInventory().setItem(8, WarpItems.RESPAWN_ANCHOR);
        player.getInventory().setItem(17, WarpItems.SETTINGS);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (isSpawn(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerAttemptPickupItem(PlayerAttemptPickupItemEvent event) {
        if (isSpawn(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}
