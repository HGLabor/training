package de.hglabor.plugins.training.challenges.mlg.mlgs;

import de.hglabor.plugins.training.Training;
import de.hglabor.plugins.training.challenges.mlg.Mlg;
import de.hglabor.plugins.training.user.User;
import de.hglabor.plugins.training.user.UserList;
import de.hglabor.plugins.training.warp.WarpItems;
import de.hglabor.utils.noriskutils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BlockMlg extends Mlg {
    private final List<ItemStack> mlgItems = new ArrayList<>();

    public BlockMlg(String name, ChatColor color, Class<? extends LivingEntity> type) {
        super(name, color, type, Material.QUARTZ_BLOCK, Material.GOLD_BLOCK);
        this.addMlgMaterials(Material.COBWEB, Material.SLIME_BLOCK, Material.SCAFFOLDING, Material.TWISTING_VINES);
    }

    private void addMlgMaterial(Material material) {
        this.mlgItems.add(new ItemBuilder(material).setName(ChatColor.AQUA + this.getName() + " MLG - " + material.name()).build()); // e.g. Block Mlg - COBWEB
    }

    private void addMlgMaterials(Material... materials) {
        for (Material material : materials) {
            this.addMlgMaterial(material);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block blockAgainst = event.getBlockAgainst();
        Block block = event.getBlock();
        if (!isInChallenge(player)) {
            event.setCancelled(true);
            return;
        }
        if (blockAgainst.getType().equals(platformMaterial) || blockAgainst.getType().equals(borderMaterial) || blockAgainst.getType().equals(topMaterial)) {
            player.sendMessage(ChatColor.RED + "Here you can't mlg"); //TODO localization
            event.setCancelled(true);
            return;
        }
        if (blockAgainst.getType().equals(bottomMaterial)) {
            User user = UserList.INSTANCE.getUser(player);
            if (!user.getChallengeInfoOrDefault(this, false)) {
                // Check for special cases
                if (block.getType().equals(Material.TWISTING_VINES)) {
                    // Twisting vines mlg only works when the player is inside the block space
                    Location blockLocation = block.getLocation();
                    Location playerLocation = player.getLocation();
                    if (!(playerLocation.getBlockX() == blockLocation.getBlockX() && playerLocation.getBlockZ() == blockLocation.getBlockZ())) {
                        onFailure(player);
                    }
                    else onComplete(player);
                } else if (block.getType().equals(Material.SCAFFOLDING)) {
                    // If the player doesn't place the scaffholding when "inside" it (on the same y coordinate), he lands on top of it and dies
                    if (!(player.getLocation().getBlockY() == block.getLocation().getBlockY())) {
                        onFailure(player);
                    }
                    else onComplete(player);
                }
                else onComplete(player);
                Bukkit.getScheduler().runTaskLater(Training.getInstance(), () -> this.clearOut(block), 5L);
            } else {
                event.setCancelled(true);
            }
        }
    }

    /** Clears out the block and the two above it to avoid people abusing e.g. two cobwebs at once */
    private void clearOut(Block block) {
        block.setType(Material.AIR);
        for (int x=0; x<=1; x++) {
            block = block.getRelative(BlockFace.UP); // get above block
            block.setType(Material.AIR); // clear it
        }
    }

    @Override
    public List<ItemStack> getMlgItems() {
        return mlgItems;
    }

    public void setMlgReady(Player player) {
        User user = UserList.INSTANCE.getUser(player);
        user.addChallengeInfo(this, false);
        player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
        player.setFoodLevel(100);
        player.getInventory().clear();
        player.getInventory().setItem(0, WarpItems.WARP_SELECTOR);

        int size = mlgItems.size();
        // size must not be grater than 6, because there are a max of 6 items slots free in the inventory hotbar
        assert size <= 6;

        boolean plus1 = (size <= 4); // If there is enough space, move it 1 to the right because it looks nicer with 1 slot space
        for (int slot=1; slot<=size; slot++) { // Start at slot 1 not 0 because of warp selector
            player.getInventory().setItem((plus1 ? (slot+1) : slot), mlgItems.get(slot-1));
        }
        player.getInventory().setItem(7, WarpItems.HUB);
        player.getInventory().setItem(8, WarpItems.RESPAWN_ANCHOR);
    }
}