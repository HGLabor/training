package de.hglabor.plugins.training.warp;

import de.hglabor.plugins.training.user.User;
import de.hglabor.plugins.training.user.UserList;
import de.hglabor.plugins.training.warp.worlds.DamagerWorld;
import de.hglabor.plugins.training.warp.worlds.MlgWorld;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.stream.IntStream;

public class WarpSelector implements Listener {
    private final static Inventory INVENTORY;
    private final static String TITLE;

    static {
        TITLE = "Warps";
        INVENTORY = Bukkit.createInventory(null, 9 * 3, TITLE);
        ItemStack placeHolder = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        IntStream.range(0, INVENTORY.getSize()).forEach(i -> INVENTORY.setItem(i, placeHolder));
        INVENTORY.setItem(10, DamagerWorld.INSTANCE.getWarpItem());
        INVENTORY.setItem(12, MlgWorld.INSTANCE.getWarpItem());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() != null) {
            if (event.getItem().isSimilar(WarpItems.WARP_SELECTOR)) {
                Player player = event.getPlayer();
                player.openInventory(INVENTORY);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (title.equals(TITLE)) {
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);
            ItemStack item = event.getCurrentItem();
            if (DamagerWorld.INSTANCE.getWarpItem().isSimilar(item)) {
                User user = UserList.INSTANCE.getUser(player);
                user.setRespawnLoc(DamagerWorld.INSTANCE.getSpawn());
                player.teleport(DamagerWorld.INSTANCE.getSpawn());
                //SoundUtils.playTeleportSound(player); //TODO revert
            } else if (MlgWorld.INSTANCE.getWarpItem().isSimilar(item)) {
                player.teleport(MlgWorld.INSTANCE.getSpawn());
                //SoundUtils.playTeleportSound(player); //TODO revert
            }
        }
    }
}
