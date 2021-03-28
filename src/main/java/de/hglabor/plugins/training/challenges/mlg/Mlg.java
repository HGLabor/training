package de.hglabor.plugins.training.challenges.mlg;

import de.hglabor.plugins.training.Training;
import de.hglabor.plugins.training.region.Area;
import de.hglabor.plugins.training.region.Cuboid;
import de.hglabor.plugins.training.util.LocationUtils;
import de.hglabor.plugins.training.warp.worlds.MlgWorld;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class Mlg implements IMlg {
    protected final String name;
    protected final ChatColor color;
    protected final LivingEntity warpEntity;
    protected Cuboid cuboid;

    public Mlg(String name, ChatColor color, Class<? extends LivingEntity> type) {
        this.name = name;
        this.color = color;
        this.cuboid = new Cuboid(LocationUtils.ZERO, LocationUtils.ZERO);
        this.warpEntity = (LivingEntity) ((CraftWorld) MlgWorld.getWorld()).createEntity(LocationUtils.MLG_SPAWN, type).getBukkitEntity();
        this.warpEntity.setInvulnerable(true);
        this.warpEntity.setAI(false);
        this.warpEntity.setCustomName(color + name);
        this.warpEntity.setCustomNameVisible(true);
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
        ((CraftWorld) MlgWorld.getWorld()).addEntity(((CraftLivingEntity) warpEntity).getHandle(), CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    @Override
    public void stop() {
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
        config.options().copyDefaults(true);
        Training.getInstance().saveConfig();
    }

    @Override
    public void safeToConfig() {
        FileConfiguration config = Training.getInstance().getConfig();
        config.set(String.format("%s.warpEntity.location", warpEntity.getLocation()), warpEntity.getLocation());
        Training.getInstance().saveConfig();
    }

    @Override
    public void loadFromConfig() {
        Training.getInstance().reloadConfig();
        FileConfiguration config = Training.getInstance().getConfig();
        warpEntity.teleport(config.getLocation(String.format("%s.warpEntity.location", warpEntity.getLocation()), warpEntity.getLocation()));
    }

    @Override
    public LivingEntity getWarpEntity() {
        return warpEntity;
    }
}
