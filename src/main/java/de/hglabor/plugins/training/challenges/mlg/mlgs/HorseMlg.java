package de.hglabor.plugins.training.challenges.mlg.mlgs;

import de.hglabor.plugins.training.challenges.mlg.Mlg;
import de.hglabor.plugins.training.user.User;
import de.hglabor.plugins.training.user.UserList;
import de.hglabor.plugins.training.warp.WarpItems;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.event.entity.EntityMountEvent;

import java.util.ArrayList;
import java.util.List;

public class HorseMlg extends Mlg {
    private final List<Horse> horses;
    private final int horseAmount;

    public HorseMlg(String name, ChatColor color, Class<? extends LivingEntity> type) {
        super(name, color, type, Material.QUARTZ_BLOCK, Material.GOLD_BLOCK);
        this.horses = new ArrayList<>();
        this.horseAmount = 100;
    }

    @Override
    public void start() {
        super.start();
        for (int i = 0; i < horseAmount; i++) {
            Horse horse = (Horse) spawn.getWorld().spawnEntity(spawn.clone().add(0, 1, 0), EntityType.HORSE);
            horse.setPersistent(false);
            horses.add(horse);
        }
    }

    @Override
    public void stop() {
        super.stop();
        horses.forEach(Entity::remove);
        horses.clear();
    }

    @EventHandler
    public void onMountHorse(EntityMountEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (!(event.getMount() instanceof Horse)) {
            return;
        }
        Player player = (Player) event.getEntity();
        if (!isInChallenge(player)) {
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
