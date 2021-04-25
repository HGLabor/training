package de.hglabor.plugins.training.mechanics;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SoupHealing {
    public static final List<Material> SOUP_MATERIAL;

    static {
        SOUP_MATERIAL = Arrays.asList(Material.MUSHROOM_STEW, Material.SUSPICIOUS_STEW);
    }

    public boolean onRightClickSoup(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() != Action.LEFT_CLICK_AIR) {
            ItemStack itemStack = event.getItem();
            if (itemStack != null) {
                if (event.hasItem() && SOUP_MATERIAL.contains(event.getMaterial())) {
                    if (event.getHand() == EquipmentSlot.OFF_HAND) {
                        return false;
                    }

                    int amountToHeal = 7;

                    if (player.getHealth() < Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue()) {
                        player.setHealth(Math.min(player.getHealth() + (double) amountToHeal, Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue()));
                        player.getInventory().setItemInMainHand(new ItemStack(Material.BOWL));
                        return true;
                    } else if (player.getFoodLevel() < 20) {
                        player.setFoodLevel(player.getFoodLevel() + 6);
                        player.setSaturation(player.getSaturation() + 7.0F);
                        player.getInventory().setItemInMainHand(new ItemStack(Material.BOWL));
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
