package de.hglabor.plugins.training.warp.worlds;

import de.hglabor.plugins.training.events.PlayerInventoryOpenEvent;
import de.hglabor.plugins.training.main.TrainingKt;
import de.hglabor.plugins.training.warp.WarpItems;
import de.hglabor.utils.noriskutils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

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

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().getWorld() == world && !notAllowedToBuild(event.getPlayer())) {
            event.setCancelled(false);
            event.setBuild(true);
        }
    }

    // Refill/Recraft when opening inventory
    @EventHandler
    public void onOpenInventory(PlayerInventoryOpenEvent event) {
        // Player has opened inv
        Player player = event.getPlayer();

        Material material = Material.getMaterial(TrainingKt.getPLUGIN().getConfig().getString("refillTrainingBlock", Material.ORANGE_TERRACOTTA.name()));
        Material playerStandingOn = world.getBlockAt(player.getLocation().clone().add(0, -1, 0)).getType();
        Bukkit.getLogger().info("Player Standing on: " + playerStandingOn.name());
        if (material != playerStandingOn) return;

        // Player is standing on training block
        player.getInventory().clear();
        player.getInventory().addItem(new ItemStack(Material.STONE_SWORD));
        final ItemStack soup = new ItemStack(Material.MUSHROOM_STEW);
        for (int i = 9; i <= 35; i++) player.getInventory().setItem(i, soup);
    }

    @EventHandler
    public void inv(InventoryOpenEvent event) {
        Bukkit.getLogger().info("open inv event");
    }
}
