package de.hglabor.plugins.training.warp.worlds;

import de.hglabor.plugins.training.warp.WarpItems;
import de.hglabor.utils.noriskutils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class MlgWorld extends TrainingWorld {
    public final static MlgWorld INSTANCE = new MlgWorld();

    private MlgWorld() {
        super(new ItemBuilder(Material.WATER_BUCKET).setName(ChatColor.AQUA + "MLG").build());
    }

    @EventHandler
    public void onBlockPhysics(BlockFromToEvent event) {
        if (!event.getBlock().getWorld().equals(world)) {
            return;
        }
        if (event.getBlock().isLiquid()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (!player.getWorld().equals(world)) {
            return;
        }
        ItemStack currentItem = event.getItemDrop().getItemStack();
        if (WarpItems.isWarpItem(currentItem)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (!player.getWorld().equals(world)) {
            return;
        }
        ItemStack currentItem = event.getCurrentItem();
        if (currentItem == null) {
            return;
        }
        if (WarpItems.isWarpItem(currentItem)) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!event.getEntity().getWorld().equals(world)) {
            return;
        }
        event.setFoodLevel(40);
        event.setCancelled(true);
    }

    @Override
    public void setItems(Player player) {

    }
}

