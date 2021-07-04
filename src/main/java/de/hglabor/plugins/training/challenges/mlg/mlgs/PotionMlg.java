package de.hglabor.plugins.training.challenges.mlg.mlgs;

import de.hglabor.plugins.training.challenges.mlg.Mlg;
import de.hglabor.utils.noriskutils.ItemBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PotionMlg extends Mlg {
    private final List<ItemStack> mlgItems = new ArrayList<>();

    public PotionMlg(String name, ChatColor color, Class<? extends Entity> type) {
        super(name, color, type, Material.QUARTZ_BLOCK, Material.END_STONE);
        
        addPotionType(PotionEffectType.SLOW_FALLING);
    }

    private void addPotionType(PotionEffectType type) {
        String name = ChatColor.AQUA + this.getName() + " MLG";
        ItemStack potionStack = new ItemStack(Material.SPLASH_POTION);
        PotionMeta meta = (PotionMeta) potionStack.getItemMeta();
        PotionData data = new PotionData(PotionType.SLOW_FALLING);
        meta.setBasePotionData(data);
        potionStack.setItemMeta(meta);
        this.mlgItems.add(new ItemBuilder(potionStack).setName(name).build());
    }

    private void addPotionTypes(PotionEffectType... types) {
        for (PotionEffectType type : types) {
            this.addPotionType(type);
        }
    }

    @EventHandler
    public void onEntityPotionEffect(EntityPotionEffectEvent event) {
        if (!event.getAction().equals(EntityPotionEffectEvent.Action.ADDED)) {
            return;
        }
        if (!(event.getEntity() instanceof Player)) {
            // Don't give the effect to the entities e.g. levitator sheep
            event.setCancelled(true);
            return;
        }
        Player player = ((Player) event.getEntity());
        if (player.getLocation().getY() >= 19) {
            event.setCancelled(true);
            if (Arrays.stream(player.getInventory().getContents()).noneMatch(mlgItems::contains)) {
                // Player has probably thrown the potion
                player.sendMessage(ChatColor.RED + "Here you can't mlg");
                // Add potion again
                inventorySetup(player);
            }
            return;
        }
        handleMlg(player);
    }

    @Override
    public List<ItemStack> getMlgItems() {
        return mlgItems;
    }
}
