package de.hglabor.plugins.training.challenges.mlg.mlgs;

import de.hglabor.plugins.training.challenges.mlg.Mlg;
import de.hglabor.utils.noriskutils.ItemBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MinecartMlg extends Mlg {
    private final List<ItemStack> mlgItems;
    private final List<Minecart> minecarts;
    private final int minecartAmount;

    public MinecartMlg(String name, ChatColor color, Class<? extends Entity> type) {
        super(name, color, type, Material.QUARTZ_BLOCK, new Material[]{Material.STONE, Material.RAIL});
        this.minecartAmount = 75;
        this.minecarts = new ArrayList<>();
        this.mlgItems = new ArrayList<>();
        this.mlgItems.add(new ItemBuilder(Material.MINECART).setName(ChatColor.AQUA + this.getName() + " MLG").build());
    }

    @Override
    public void start() {
        super.start();
        Random random = new Random();
        for (int i = 0; i < minecartAmount; i++) {
            int x = random.nextInt(getBorderRadius());
            int z = random.nextInt(getBorderRadius());
            Minecart minecart = (Minecart) spawn.getWorld().spawnEntity(spawn.clone().add(random.nextBoolean() ? x : x * -1, 1, random.nextBoolean() ? z : z * -1), EntityType.MINECART);
            minecart.setPersistent(false);
            minecarts.add(minecart);
        }
    }

    @Override
    public void stop() {
        super.stop();
        minecarts.forEach(Entity::remove);
        minecarts.clear();
    }

    // Stop players from "hitting" minecarts
    @EventHandler
    public void onDestroyMinecart(VehicleDamageEvent event) {
        if (event.getAttacker() instanceof Player) {
            Player player = ((Player) event.getAttacker());
            if (!(isInChallenge(player))) {
                return;
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPlaceMinecart(@SuppressWarnings("deprecation") /* api works for this usage */ EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof Minecart)) return;
        Player player = event.getPlayer();
        if (player == null) return;
        if (!(isInChallenge(player))) {
            event.setCancelled(true);
            return;
        }
        removeEntityLater(event.getEntity(), 20L);
    }

    @EventHandler
    public void onPlayerEnterMinecart(VehicleEnterEvent event) {
        if (!(event.getEntered() instanceof Player)) return;
        if (!(event.getVehicle() instanceof Minecart)) return;
        Player player = (Player) event.getEntered();
        if (!(isInChallenge(player))) {
            event.setCancelled(true);
            return;
        }
        handleMlg(player, 5L);
    }


    @Override
    public List<ItemStack> getMlgItems() {
        return mlgItems;
    }
}
