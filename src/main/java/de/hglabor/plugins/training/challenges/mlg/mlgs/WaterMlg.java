package de.hglabor.plugins.training.challenges.mlg.mlgs;

import de.hglabor.plugins.training.Training;
import de.hglabor.plugins.training.challenges.mlg.Mlg;
import de.hglabor.plugins.training.user.User;
import de.hglabor.plugins.training.user.UserList;
import de.hglabor.plugins.training.warp.WarpItems;
import de.hglabor.plugins.training.warp.WarpSelector;
import de.hglabor.utils.noriskutils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class WaterMlg extends Mlg {
    private final ItemStack mlgItem;

    public WaterMlg(String name, ChatColor color, Class<? extends LivingEntity> type) {
        super(name, color, type, Material.QUARTZ_BLOCK, Material.GOLD_BLOCK);
        this.mlgItem = new ItemBuilder(Material.WATER_BUCKET).setName(ChatColor.AQUA + this.getName() + " MLG").build();
    }

    @EventHandler
    public void onBucket(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        Block blockClicked = event.getBlockClicked();
        Block block = event.getBlock();
        if (blockClicked.getType().equals(platformMaterial) || blockClicked.getType().equals(borderMaterial)) {
            player.sendMessage(ChatColor.RED + "Here you can't mlg"); //TODO localization
            event.setCancelled(true);
            return;
        }
        if (blockClicked.getType().equals(bottomMaterial)) {
            onComplete(player);
            Bukkit.getScheduler().runTaskLater(Training.getInstance(), () -> {
                block.setType(Material.AIR);
            }, 10);
        }
    }

    @EventHandler
    public void onBucket(PlayerBucketFillEvent event) {
        Block blockClicked = event.getBlockClicked();
        if (blockClicked.getType().equals(bottomMaterial)) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onEnter(Player player) {
        player.sendMessage("You entered " + this.getName() + " MLG");
        setItems(player);
    }

    @Override
    public void onLeave(Player player) {
        player.sendMessage("You left " + this.getName() + " MLG");
    }

    @Override
    public void onComplete(Player player) {
        player.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "Successful MLG");
        player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
        User user = UserList.INSTANCE.getUser(player);
        Bukkit.getScheduler().runTaskLater(Training.getInstance(), () -> {
            teleportAndSetItems(player);
        }, 10L);
    }

    @Override
    public void onFailure(Player player) {
        Bukkit.getScheduler().runTaskLater(Training.getInstance(), () -> {
            teleportAndSetItems(player);
        }, 0);
    }

    @Override
    public List<ItemStack> getMlgItems() {
        return Collections.singletonList(mlgItem);
    }

    @Override
    public void setItems(Player player) {
        player.getInventory().clear();
        player.getInventory().setItem(0, WarpItems.WARP_SELECTOR);
        player.getInventory().setItem(4, mlgItem);
        player.getInventory().setItem(7, WarpItems.HUB);
        player.getInventory().setItem(8, WarpItems.RESPAWN_ANCHOR);
    }
}
