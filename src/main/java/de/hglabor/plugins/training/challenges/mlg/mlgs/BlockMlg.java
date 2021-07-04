package de.hglabor.plugins.training.challenges.mlg.mlgs;

import de.hglabor.plugins.training.main.TrainingKt;
import org.bukkit.Bukkit;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

public class BlockMlg extends AbstractBlockMlg {

    public BlockMlg(String name, ChatColor color, Class<? extends Entity> type) {
        super(name, color, type, Material.QUARTZ_BLOCK, Material.GOLD_BLOCK);
        this.addMlgMaterials(Material.WATER_BUCKET, Material.COBWEB, Material.SLIME_BLOCK, Material.SCAFFOLDING, Material.TWISTING_VINES, Material.HONEY_BLOCK);
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        Block blockClicked = event.getBlockClicked();
        Block block = event.getBlock();
        if (!isInChallenge(player)) {
            event.setCancelled(true);
            return;
        }
        if (cantMlgHere(blockClicked)) {
            player.sendMessage(ChatColor.RED + "Here you can't mlg"); //TODO localization
            event.setCancelled(true);
            return;
        }

        handleMlg(player);
        removeBlockLater(block, 10L);
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        Block blockClicked = event.getBlockClicked();
        if (!isInChallenge(event.getPlayer())) {
            return;
        }
        if (blockClicked.getType().equals(Material.WATER)) {
            event.setCancelled(true);
        }
    }

    @Override
    protected void handleBlockRemoval(Block block) {
        Bukkit.getScheduler().runTaskLater(TrainingKt.getPLUGIN(), () -> clearOut(block), 5L);
    }

    /**
     * Clears out the block and the two above it to avoid people abusing e.g. two cobwebs at once
     */
    private void clearOut(Block block) {
        block.setType(Material.AIR);
        for (int x = 0; x <= 1; x++) {
            block = block.getRelative(BlockFace.UP); // get above block
            block.setType(Material.AIR); // clear it
        }
    }

    @Override
    protected void inventorySetup(Player player) {
        int size = mlgItems.size();
        // size must not be grater than 6, because there are a max of 6 items slots free in the inventory hotbar
        assert size <= 6;

        boolean plus1 = (size <= 4); // If there is enough space, move it 1 to the right because it looks nicer with 1 slot space
        for (int slot = 1; slot <= size; slot++) { // Start at slot 1 not 0 because of warp selector
            player.getInventory().setItem((plus1 ? (slot + 1) : slot), mlgItems.get(slot - 1));
        }
    }
}
