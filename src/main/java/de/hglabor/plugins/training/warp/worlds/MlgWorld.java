package de.hglabor.plugins.training.warp.worlds;

import de.hglabor.plugins.training.warp.WarpItems;
import de.hglabor.utils.noriskutils.ItemBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.inventory.ItemStack;

public class MlgWorld extends TrainingWorld {
    public final static MlgWorld INSTANCE = new MlgWorld();

    private MlgWorld() {
        super(new ItemBuilder(Material.WATER_BUCKET).setName(ChatColor.AQUA + "MLG").build());
    }

    @Override
    public void init(World world) {
        super.init(world);
        world.getWorldBorder().setSize(2500 * 2);
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
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (event.getPlayer().getWorld().equals(world)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onVehicleColission(VehicleEntityCollisionEvent event) {
        if ((event.getVehicle() instanceof Minecart || event.getVehicle() instanceof Boat)) {
            if (event.getEntity().getWorld().equals(world)) {
                event.setCancelled(true);
                event.setCollisionCancelled(true);
            }
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

