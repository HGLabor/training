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
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class BoatMlgInfo {
    private boolean hasEnteredBoat;
    private boolean hasDied;

    public boolean hasEnteredBoat() {
        return hasEnteredBoat;
    }

    public void setHasEnteredBoat(boolean hasEnteredBoat) {
        this.hasEnteredBoat = hasEnteredBoat;
    }

    public boolean hasDied() {
        return hasDied;
    }

    public void setHasDied(boolean hasDied) {
        this.hasDied = hasDied;
    }
}

public class BoatMlg extends Mlg {
    private final List<ItemStack> mlgItems;

    public BoatMlg(String name, ChatColor color, Class<? extends Entity> type) {
        super(name, color, type, Material.QUARTZ_BLOCK, Material.GOLD_BLOCK);
        this.mlgItems = new ArrayList<>();
        this.mlgItems.add(new ItemBuilder(Material.OAK_BOAT).setName(ChatColor.AQUA + this.getName() + " MLG").build());
    }

    @EventHandler
    public void onPlayerPlaceBoat(@SuppressWarnings("deprecation") /* api works for this usage */ EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof Boat)) {
            return;
        }
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        if (!(isInChallenge(player))) {
            event.setCancelled(true);
            return;
        }
        if (!canMlgHere(event.getBlock())) {
            event.setCancelled(true);
            return;
        }

        User user = UserList.INSTANCE.getUser(player);
        BoatMlgInfo boatMlgInfo = user.getChallengeInfoOrDefault(this, new BoatMlgInfo());
        boatMlgInfo.setHasDied(false);
        // Remove boat after 1 second (20 ticks)
        Bukkit.getScheduler().runTaskLater(Training.getInstance(), () -> event.getEntity().remove(), 20L);
    }

    @EventHandler
    public void onPlayerEnterBoat(VehicleEnterEvent event) {
        if (!(event.getEntered() instanceof Player)) return;
        if (!(event.getVehicle() instanceof Boat)) return;
        Player player = (Player) event.getEntered();
        if (!(isInChallenge(player))) {
            event.setCancelled(true);
            return;
        }
        User user = UserList.INSTANCE.getUser(player);
        BoatMlgInfo boatMlgInfo = user.getChallengeInfoOrDefault(this, new BoatMlgInfo());
        if (!boatMlgInfo.hasEnteredBoat()) {
            boatMlgInfo.setHasEnteredBoat(true);
            Bukkit.getScheduler().runTaskLater(Training.getInstance(), () -> {
                if (!boatMlgInfo.hasDied()) {
                    onComplete(player);
                }
            }, 10L);
        }
        // Boat already gets removed in onPlayerPlaceBoat
    }

    @Override
    public void onComplete(Player player) {
        User user = UserList.INSTANCE.getUser(player);
        player.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "Successful MLG");
        player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
        Bukkit.getScheduler().runTaskLater(Training.getInstance(), () -> {
            user.addChallengeInfo(this, new BoatMlgInfo());
            teleportAndSetItems(player);
        }, 5L);
    }

    @Override
    public void onFailure(Player player) {
        super.onFailure(player);
        User user = UserList.INSTANCE.getUser(player);
        BoatMlgInfo boatMlgInfo = new BoatMlgInfo();
        boatMlgInfo.setHasDied(true);
        user.addChallengeInfo(this, boatMlgInfo);
    }

    @Override
    public List<ItemStack> getMlgItems() {
        return mlgItems;
    }

    public void setMlgReady(Player player) {
        User user = UserList.INSTANCE.getUser(player);
        user.addChallengeInfo(this, new BoatMlgInfo());
        player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
        player.setFoodLevel(100);
        player.getInventory().clear();
        player.getInventory().setItem(0, WarpItems.WARP_SELECTOR);
        player.getInventory().setItem(4, mlgItems.get(0));
        player.getInventory().setItem(7, WarpItems.HUB);
        player.getInventory().setItem(8, WarpItems.RESPAWN_ANCHOR);
    }
}
