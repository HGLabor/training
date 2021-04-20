package de.hglabor.plugins.training.challenges.mlg.mlgs;

import de.hglabor.plugins.training.Training;
import de.hglabor.plugins.training.challenges.mlg.Mlg;
import de.hglabor.plugins.training.user.User;
import de.hglabor.plugins.training.user.UserList;
import de.hglabor.plugins.training.warp.WarpItems;
import de.hglabor.utils.noriskutils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

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

    @EventHandler
    public void onPlayerPlaceMinecart(@SuppressWarnings("deprecation") /* api works for this usage */ EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof Minecart)) return;
        Player player = event.getPlayer();
        if (player == null) return;
        if (!(isInChallenge(player))) {
            event.setCancelled(true);
            return;
        }

        User user = UserList.INSTANCE.getUser(player);
        if (!user.getChallengeInfoOrDefault(this, false)) {
            if (Arrays.stream(this.bottomMaterials).noneMatch((m -> m.equals(event.getBlock().getType())))) {
                event.setCancelled(true);
                return;
            }
            // Remove minecart after 1 second (20 ticks)
            Bukkit.getScheduler().runTaskLater(Training.getInstance(), () -> event.getEntity().remove(), 20L);
        }
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
        User user = UserList.INSTANCE.getUser(player);
        if (!user.getChallengeInfoOrDefault(this, false)) {
            onComplete(player);
            // Minecart already gets removed in onPlayerPlaceBoat
        } else {
            event.setCancelled(true);
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
        player.getInventory().setItem(4, mlgItems.get(0));
        player.getInventory().setItem(7, WarpItems.HUB);
        player.getInventory().setItem(8, WarpItems.RESPAWN_ANCHOR);
    }
}
