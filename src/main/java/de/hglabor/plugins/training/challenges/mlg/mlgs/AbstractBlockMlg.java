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

public abstract class AbstractBlockMlg extends Mlg {
    protected final List<ItemStack> mlgItems;

    public AbstractBlockMlg(String name, ChatColor color, Class<? extends Entity> type, Material borderMaterial, Material bottomMaterial) {
        super(name, color, type, borderMaterial, bottomMaterial);
        mlgItems = new ArrayList<>();
    }

    public AbstractBlockMlg(String name, ChatColor color, Class<? extends Entity> type, Material borderMaterial, Material[] bottomMaterials, Double[] bottomMaterialPercentages) {
        super(name, color, type, borderMaterial, bottomMaterials, bottomMaterialPercentages);
        this.mlgItems = new ArrayList<>();
    }

    protected void addMlgMaterials(Material... materials) {
        for (Material material : materials) {
            String name = ChatColor.AQUA + this.getName() + " MLG - " + material.name(); // e.g. Block Mlg - COBWEB
            switch (material) {
                case SCAFFOLDING:
                case TWISTING_VINES:
                    name += " - ONLY Y = 25 OR Y = 250 LOL";
                default:
                    this.mlgItems.add(new ItemBuilder(material).setName(name).build());
            }
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
        if (cantMlgHere(blockAgainst)) {
            player.sendMessage(ChatColor.RED + "Here you can't mlg"); //TODO localization
            event.setCancelled(true);
            return;
        }
        event.setCancelled(false);
        handleMlg(player);
        handleBlockRemoval(block);
    }

    /** Remove the block after some time */
    protected abstract void handleBlockRemoval(Block block);

    @Override
    public List<ItemStack> getMlgItems() {
        return mlgItems;
    }
}
