package de.hglabor.plugins.training.challenges.mlg;

import de.hglabor.utils.noriskutils.HologramUtils;
import de.hglabor.utils.noriskutils.WorldEditUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftVillager;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class MlgPlatform implements Listener {
    private Location spawn;
    private final int radius;
    private final int yPos;
    private final Material material;
    private ArmorStand heightHologram;
    private MlgPlatform upPlatform, downPlatform;
    private Villager upVillager, downVillager;

    public MlgPlatform(Location spawn, int radius, int yPos, Material material) {
        this.spawn = spawn;
        this.radius = radius;
        this.yPos = yPos;
        this.material = material;
    }

    public void create() {
        World world = spawn.getWorld();
        WorldEditUtils.createCylinder(world, spawn, radius, true, 1, material);
        heightHologram = HologramUtils.spawnHologram(spawn.clone().add(0, 2, 0), ChatColor.BOLD.toString() + spawn.getY());
        if (upPlatform != null) {
            upVillager = (Villager) ((CraftWorld) world).createEntity(spawn.add(radius - (radius / 3D), 0, 1), Villager.class).getBukkitEntity();
            upVillager.setCustomName(ChatColor.BOLD + ChatColor.GREEN.toString() + "\u25B2");
            upVillager.setCustomNameVisible(true);
            ((CraftWorld) world).addEntity(((CraftVillager) upVillager).getHandle(), CreatureSpawnEvent.SpawnReason.CUSTOM);
        }
        if (downPlatform != null) {
            downVillager = (Villager) ((CraftWorld) world).createEntity(spawn.add(radius - (radius / 3D), 0, -1), Villager.class).getBukkitEntity();
            upVillager.setCustomName(ChatColor.BOLD + ChatColor.RED.toString() + "\u25BC");
            upVillager.setCustomNameVisible(true);
            ((CraftWorld) world).addEntity(((CraftVillager) downVillager).getHandle(), CreatureSpawnEvent.SpawnReason.CUSTOM);
        }
    }

    public void clear() {
        if (upVillager != null) upVillager.remove();
        if (downVillager != null) downVillager.remove();
        heightHologram.remove();
    }

    public Location getSpawn() {
        return spawn;
    }

    public void setUp(MlgPlatform up) {
        this.upPlatform = up;
    }

    public void setDown(MlgPlatform down) {
        this.downPlatform = down;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
        this.spawn.setY(yPos);
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        Entity rightClicked = event.getRightClicked();
        Player player = event.getPlayer();
        if (rightClicked.equals(upVillager)) {
            player.teleport(upPlatform.getSpawn());
        } else if (rightClicked.equals(downVillager)) {
            player.teleport(downPlatform.getSpawn());
        }
    }
}
