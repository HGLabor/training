package de.hglabor.plugins.training.challenges.mlg.mlgs;

import de.hglabor.plugins.training.Training;
import de.hglabor.plugins.training.challenges.mlg.Mlg;
import de.hglabor.plugins.training.user.User;
import de.hglabor.plugins.training.user.UserList;
import de.hglabor.plugins.training.warp.WarpItems;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.event.entity.EntityMountEvent;

import java.util.List;

public class HorseMlg extends Mlg {
    public HorseMlg(String name, ChatColor color, Class<? extends LivingEntity> type) {
        super(name, color, type, Material.QUARTZ_BLOCK, Material.GOLD_BLOCK);
    }

    @EventHandler
    public void onBucket(EntityMountEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (!(event.getMount() instanceof Horse)) {
            return;
        }
        Player player = (Player) event.getEntity();
        Horse horse = (Horse) event.getMount();
        if (!isInChallenge(player)) {
            Bukkit.broadcastMessage("hi");
            event.setCancelled(true);
            return;
        }
        User user = UserList.INSTANCE.getUser(player);
        if (!user.getChallengeInfoOrDefault(this, false)) {
            onComplete(player);
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBucket(PlayerBucketFillEvent event) {
        Block blockClicked = event.getBlockClicked();
        if (!isInChallenge(event.getPlayer())) {
            return;
        }
        if (blockClicked.getType().equals(Material.WATER)) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onEnter(Player player) {
        player.sendMessage("You entered " + this.getName() + " MLG");
        setMlgReady(player);
    }

    @Override
    public void onLeave(Player player) {
        player.sendMessage("You left " + this.getName() + " MLG");
    }

    @Override
    public void onComplete(Player player) {
        User user = UserList.INSTANCE.getUser(player);
        user.addChallengeInfo(this, true);
        player.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "Successful MLG");
        player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
        Bukkit.getScheduler().runTaskLater(Training.getInstance(), () -> {
            teleportAndSetItems(player);
        }, 5L);
    }

    @Override
    public void onFailure(Player player) {
        player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "Failed MLG");
        Bukkit.getScheduler().runTaskLater(Training.getInstance(), () -> {
            teleportAndSetItems(player);
            player.playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 1, 1);
        }, 0);
    }

    @Override
    public List<ItemStack> getMlgItems() {
        return null;
    }

    public void setMlgReady(Player player) {
        User user = UserList.INSTANCE.getUser(player);
        user.addChallengeInfo(this, false);
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(100);
        player.getInventory().clear();
        player.getInventory().setItem(0, WarpItems.WARP_SELECTOR);
        player.getInventory().setItem(7, WarpItems.HUB);
        player.getInventory().setItem(8, WarpItems.RESPAWN_ANCHOR);
    }
}
