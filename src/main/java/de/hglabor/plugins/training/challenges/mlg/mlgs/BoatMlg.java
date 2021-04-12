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
import org.bukkit.entity.Boat;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BoatMlg extends Mlg {
    private final List<ItemStack> mlgItems = new ArrayList<>();

    public BoatMlg(String name, ChatColor color, Class<? extends LivingEntity> type) {
        super(name, color, type, Material.QUARTZ_BLOCK, Material.GOLD_BLOCK);
        this.addMlgMaterial(Material.OAK_BOAT);
    }

    private void addMlgMaterial(Material material) {
        this.mlgItems.add(new ItemBuilder(material).setName(ChatColor.AQUA + this.getName() + " MLG").build()); // e.g. Block Mlg - COBWEB
    }

    @EventHandler
    public void onPlayerPlaceBoat(@SuppressWarnings("deprecation") /* api works for this usage */ EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof Boat)) return;
        Player player = event.getPlayer();
        if (player == null) return;
        if (!(isInChallenge(player))) return;

        User user = UserList.INSTANCE.getUser(player);
        if (!user.getChallengeInfoOrDefault(this, false)) {
            if (event.getBlock().getType() != this.bottomMaterial) {
                event.setCancelled(true);
                return;
            }
            // Remove boat after 1 second (20 ticks)
            Bukkit.getScheduler().runTaskLater(Training.getInstance(), () -> event.getEntity().remove(), 20L);
        }
    }

    @EventHandler
    public void onPlayerEnterBoat(VehicleEnterEvent event) {
        if (!(event.getEntered() instanceof Player)) return;
        if (!(event.getVehicle() instanceof Boat)) return;
        Player player = (Player) event.getEntered();
        if (!(isInChallenge(player))) return;
        User user = UserList.INSTANCE.getUser(player);
        if (!user.getChallengeInfoOrDefault(this, false)) {
            onComplete(player);
            // Boat already gets removed in onPlayerPlaceBoat
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
