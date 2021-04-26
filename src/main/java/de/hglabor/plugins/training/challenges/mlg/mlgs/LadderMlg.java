package de.hglabor.plugins.training.challenges.mlg.mlgs;

import de.hglabor.plugins.training.challenges.mlg.Mlg;
import de.hglabor.utils.noriskutils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

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

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block blockAgainst = event.getBlockAgainst();
        Block block = event.getBlock();
        if (!isInChallenge(player)) {
            return;
        }
        if (isAllowedToBuild(player) && getMlgItems().stream().noneMatch(mlgItem -> mlgItem.getType().equals(block.getType()))) {
            event.setCancelled(false);
            return;
        }
        if (!canMlgHere(blockAgainst)) {
            player.sendMessage(ChatColor.RED + "Here you can't mlg"); //TODO localization
            event.setCancelled(true);
            return;
        }
        event.setCancelled(false);
        handleMlg(player);
        // Remove ladder/vine after 10 ticks
        removeBlockLater(block, 10L);
    }

    @Override
    public List<ItemStack> getMlgItems() {
        return mlgItems;
    }

    @Override
    protected void inventorySetup(Player player) {
        player.getInventory().setItem(3, getMlgItems().get(0));
        player.getInventory().setItem(4, getMlgItems().get(1));
    }
}
