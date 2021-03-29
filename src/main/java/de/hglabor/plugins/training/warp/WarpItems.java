package de.hglabor.plugins.training.warp;

import de.hglabor.utils.noriskutils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface WarpItems {
    ItemStack RESPAWN_ANCHOR = new ItemBuilder(Material.RESPAWN_ANCHOR)
            .setName("Left click = new spawn | Right click = reset")
            .build();
    ItemStack HUB = new ItemBuilder(Material.HEART_OF_THE_SEA)
            .setName("Hub")
            .build();
    ItemStack WARP_SELECTOR = new ItemBuilder(Material.NETHER_STAR)
            .setName("Warp selector")
            .build();
    List<ItemStack> WARP_ITEMS = List.of(RESPAWN_ANCHOR, HUB, WARP_SELECTOR);

    static boolean isWarpItem(ItemStack itemStack) {
        return WARP_ITEMS.stream().anyMatch(warpItem -> warpItem.isSimilar(itemStack));
    }
}
