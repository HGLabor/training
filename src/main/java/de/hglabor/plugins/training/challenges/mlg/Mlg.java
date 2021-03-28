package de.hglabor.plugins.training.challenges.mlg;

import de.hglabor.plugins.training.Training;
import de.hglabor.plugins.training.region.Area;
import de.hglabor.plugins.training.region.Cuboid;
import de.hglabor.plugins.training.util.LocationUtils;
import de.hglabor.plugins.training.warp.worlds.MlgWorld;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.List;

public class Mlg implements IMlg {
    protected final String name;
    protected final ChatColor color;
    protected final LivingEntity warpEntity;
    protected Cuboid cuboid;
    protected Location spawn;
    protected List<MlgPlatform> platforms;

    public Mlg(String name, ChatColor color, Class<? extends LivingEntity> type) {
        this.name = name;
        this.color = color;
        this.spawn = LocationUtils.ZERO;
        this.cuboid = new Cuboid(LocationUtils.ZERO, LocationUtils.ZERO);
        this.warpEntity = (LivingEntity) ((CraftWorld) MlgWorld.getWorld()).createEntity(LocationUtils.MLG_SPAWN, type).getBukkitEntity();
        this.warpEntity.setInvulnerable(true);
        this.warpEntity.setAI(false);
        this.warpEntity.setCustomName(color + name + " MLG");
        this.warpEntity.setCustomNameVisible(true);
    }

    public Mlg withPlatforms(Material material, int radius, int... yPositions) {
        for (int yPosition : yPositions) {
            platforms.add(new MlgPlatform(LocationUtils.ZERO, radius, yPosition, material));
        }
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Area getArea() {
        return cuboid;
    }

    @Override
    public ChatColor getColor() {
        return color;
    }

    @Override
    public void start() {
        platforms.forEach(platform -> {
            Bukkit.getPluginManager().registerEvents(platform, Training.getInstance());
            platform.setSpawn(spawn);
            platform.create();
        });
        
        platforms.get(0).setUp(platforms.get(1));
        platforms.get(platforms.size() - 1).setDown(platforms.get(platforms.size() - 2));

        for (int i = 1; i < platforms.size() - 1; i++) {
            MlgPlatform mlgPlatform = platforms.get(i);
            mlgPlatform.setUp(platforms.get(i + 1));
            mlgPlatform.setDown(platforms.get(i - 1));
        }

        ((CraftWorld) MlgWorld.getWorld()).addEntity(((CraftLivingEntity) warpEntity).getHandle(), CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    @Override
    public void stop() {
        platforms.forEach(MlgPlatform::clear);
        warpEntity.remove();
    }

    @Override
    public void onEnter(Player player) {
    }

    @Override
    public void onComplete(Player player) {
    }

    @Override
    public void onFailure(Player player) {
    }

    @Override
    public void onLeave(Player player) {
    }

    @Override
    public boolean isInChallenge(Player player) {
        return false;
    }

    @Override
    public void initConfig() {
        FileConfiguration config = Training.getInstance().getConfig();
        config.addDefault(String.format("%s.warpEntity.location", warpEntity.getLocation()), warpEntity.getLocation());
        config.addDefault(String.format("%s.mlgPlatform.spawn", spawn), spawn);
        config.options().copyDefaults(true);
        Training.getInstance().saveConfig();
    }

    @Override
    public void safeToConfig() {
        FileConfiguration config = Training.getInstance().getConfig();
        config.set(String.format("%s.warpEntity.location", warpEntity.getLocation()), warpEntity.getLocation());
        config.set(String.format("%s.mlgPlatform.spawn", spawn), spawn);
        Training.getInstance().saveConfig();
    }

    @Override
    public void loadFromConfig() {
        Training.getInstance().reloadConfig();
        FileConfiguration config = Training.getInstance().getConfig();
        spawn = config.getLocation(String.format("%s.mlgPlatform.spawn", spawn), spawn);
        warpEntity.teleport(config.getLocation(String.format("%s.warpEntity.location", warpEntity.getLocation()), warpEntity.getLocation()));
    }

    @Override
    public LivingEntity getWarpEntity() {
        return warpEntity;
    }

    @EventHandler
    public void onRightClickWarpEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity rightClicked = event.getRightClicked();
        if (rightClicked.equals(warpEntity)) {
            player.teleport(platforms.get(0).getSpawn());
        }
    }
}
