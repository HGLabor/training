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
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class LadderMlgInfo {
    private boolean hasDied;

    public boolean hasDied() {
        return hasDied;
    }

    public void setHasDied(boolean hasDied) {
        this.hasDied = hasDied;
    }
}

public class LadderMlg extends Mlg {
    private final List<ItemStack> mlgItems = new ArrayList<>();

    public LadderMlg(String name, ChatColor color, Class<? extends Entity> type) {
        super(name, color, type, Material.QUARTZ_BLOCK, new Material[]{Material.STONE, Material.SMOOTH_STONE, Material.SMOOTH_STONE}, new Double[]{100D, 20D, 10D});
        this.addMlgMaterials(Material.LADDER, Material.VINE);
    }

    private void addMlgMaterial(Material material) {
        String name = ChatColor.AQUA + this.getName() + " MLG - " + material.name(); // e.g. Block Mlg - COBWEB
        this.mlgItems.add(new ItemBuilder(material).setName(name).build());
    }

    private void addMlgMaterials(Material... materials) {
        for (Material material : materials) {
            this.addMlgMaterial(material);
        }
    }

    @Override
    public void onComplete(Player player) {
        User user = UserList.INSTANCE.getUser(player);
        player.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "Successful MLG");
        player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
        Bukkit.getScheduler().runTaskLater(Training.getInstance(), () -> {
            user.addChallengeInfo(this, new LadderMlgInfo());
            teleportAndSetItems(player);
        }, 5L);
    }

    @Override
    public void onFailure(Player player) {
        super.onFailure(player);
        User user = UserList.INSTANCE.getUser(player);
        LadderMlgInfo ladderMlgInfo = new LadderMlgInfo();
        ladderMlgInfo.setHasDied(true);
        user.addChallengeInfo(this, ladderMlgInfo);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block blockAgainst = event.getBlockAgainst();
        Block block = event.getBlock();
        if (!isInChallenge(player)) {
            return;
        }
        if (!canMlgHere(blockAgainst)) {
            player.sendMessage(ChatColor.RED + "Here you can't mlg"); //TODO localization
            event.setCancelled(true);
            return;
        }
        User user = UserList.INSTANCE.getUser(player);
        LadderMlgInfo ladderMlgInfo = user.getChallengeInfoOrDefault(this, new LadderMlgInfo());
        ladderMlgInfo.setHasDied(false);
        Bukkit.getScheduler().runTaskLater(Training.getInstance(), () -> {
            LadderMlgInfo ladderMlgInfo1 = user.getChallengeInfoOrDefault(this, new LadderMlgInfo());
            if (!ladderMlgInfo1.hasDied()) {
                onComplete(player);
            }
        }, 10L);
        // Remove ladder/vine after 10 ticks
        Bukkit.getScheduler().runTaskLater(Training.getInstance(), () ->  {
            block.setType(Material.AIR);
        }, 10L);
    }

    @Override
    public List<ItemStack> getMlgItems() {
        return mlgItems;
    }

    @Override
    public void setMlgReady(Player player) {
        User user = UserList.INSTANCE.getUser(player);
        if (user.getChallengeInfoOrDefault(this, null) == null) user.addChallengeInfo(this, new LadderMlgInfo());
        player.setFoodLevel(100);
        player.getInventory().clear();
        player.getInventory().setItem(0, WarpItems.WARP_SELECTOR);

        player.getInventory().setItem(3, getMlgItems().get(0));
        player.getInventory().setItem(4, getMlgItems().get(1));

        player.getInventory().setItem(7, WarpItems.HUB);
        player.getInventory().setItem(8, WarpItems.RESPAWN_ANCHOR);
    }
}
