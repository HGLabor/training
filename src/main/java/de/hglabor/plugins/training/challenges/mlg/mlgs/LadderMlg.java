package de.hglabor.plugins.training.challenges.mlg.mlgs;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class LadderMlg extends AbstractBlockMlg {
    public LadderMlg(String name, ChatColor color, Class<? extends Entity> type) {
        super(name, color, type, Material.QUARTZ_BLOCK, new Material[]{Material.STONE, Material.SMOOTH_STONE, Material.SMOOTH_STONE}, new Double[]{100D, 20D, 10D});
        this.addMlgMaterials(Material.LADDER, Material.VINE);
    }

    @Override
    public void handleBlockRemoval(Block block) {
        // Remove ladder/vine after 10 ticks
        removeBlockLater(block, 10L);
    }

    @Override
    protected void inventorySetup(Player player) {
        player.getInventory().setItem(3, getMlgItems().get(0));
        player.getInventory().setItem(4, getMlgItems().get(1));
    }
}
