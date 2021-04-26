package de.hglabor.plugins.training.challenges.mlg.mlgs;

import de.hglabor.plugins.training.challenges.mlg.Mlg;
import de.hglabor.utils.noriskutils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
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
    public void onPotionThrow(PlayerInteractEvent event) {
        if (!(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
            return;
        }
        if (event.getItem() == null) {
            return;
        }
        if (!(event.getItem().getType().equals(Material.SPLASH_POTION))) {
            return;
        }
        Player player = event.getPlayer();
        if (player.getLocation().getBlockY() >= 9) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Here you can't mlg");
            return;
        }

        handleMlg(player, 15L);
    }

    @Override
    protected void handleMlgSetup(Player player) {
        super.handleMlgSetup(player);
    }

    @Override
    public List<ItemStack> getMlgItems() {
        return mlgItems;
    }
}
