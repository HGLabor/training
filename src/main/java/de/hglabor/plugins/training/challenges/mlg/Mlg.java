package de.hglabor.plugins.training.challenges.mlg;

import de.hglabor.plugins.training.Training;
import de.hglabor.plugins.training.challenges.Challenge;
import de.hglabor.plugins.training.region.Area;
import de.hglabor.plugins.training.region.Cuboid;
import de.hglabor.plugins.training.user.User;
import de.hglabor.plugins.training.user.UserList;
import de.hglabor.plugins.training.util.LocationUtils;
import de.hglabor.plugins.training.warp.worlds.MlgWorld;
import de.hglabor.utils.noriskutils.WorldEditUtils;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class Mlg implements Challenge {
    protected final String name;
    protected final ChatColor color;
    protected final Entity warpEntity;
    protected final Material borderMaterial, topMaterial;
    protected final Material bottomMaterial;
    protected final Material[] bottomMaterials;
    protected Cuboid cuboid;
    protected Location spawn;
    protected Material platformMaterial;
    protected int platformRadius;
    protected List<MlgPlatform> platforms;

    public Mlg(String name, ChatColor color, Class<? extends Entity> type, Material borderMaterial, Material[] bottomMaterials) {
        this.name = name;
        this.color = color;
        this.borderMaterial = borderMaterial;
        this.bottomMaterials = bottomMaterials;
        this.bottomMaterial = bottomMaterials[0];
        this.topMaterial = Material.BARRIER;
        this.spawn = LocationUtils.ZERO_MLG;
        this.platforms = new ArrayList<>();
        this.cuboid = new Cuboid(LocationUtils.ZERO_MLG, LocationUtils.ZERO_MLG);
        this.warpEntity = ((CraftWorld) MlgWorld.INSTANCE.getWorld()).createEntity(LocationUtils.MLG_SPAWN, type).getBukkitEntity();
        this.warpEntity.setInvulnerable(true);
        this.warpEntity.setPersistent(false);
        if (warpEntity instanceof LivingEntity) ((LivingEntity) this.warpEntity).setAI(false);
        this.warpEntity.setCustomName(color + name + " MLG");
        this.warpEntity.setCustomNameVisible(true);
    }

    public Mlg(String name, ChatColor color, Class<? extends Entity> type, Material borderMaterial, Material bottomMaterial) {
        this(name, color, type, borderMaterial, new Material[] {bottomMaterial});
    }

    public Entity getWarpEntity() {
        return warpEntity;
    }

    public Mlg withPlatforms(Material material, int radius, int... yPositions) {
        this.platformMaterial = material;
        this.platformRadius = radius;
        for (int yPosition : yPositions) {
            platforms.add(new MlgPlatform(this, LocationUtils.ZERO_MLG, radius, yPosition, material));
        }
        return this;
    }

    public abstract List<ItemStack> getMlgItems();

    public abstract void setMlgReady(Player player);

    @Override
    public void onEnter(Player player) {
        player.sendMessage("You entered " + this.getName() + " MLG");
        User user = UserList.INSTANCE.getUser(player);
        user.setRespawnLoc(getDefaultSpawn());
        setMlgReady(player);
    }

    @Override
    public void onLeave(Player player) {
        player.sendMessage("You left " + this.getName() + " MLG");
    }

    @Override
    public void onComplete(Player player) {
        User user = UserList.INSTANCE.getUser(player);
        user.addChallengeInfo(this, true);
        player.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "Successful MLG");
        player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
        Bukkit.getScheduler().runTaskLater(Training.getInstance(), () -> {
            teleportAndSetItems(player);
        }, 5L);
    }

    @Override
    public void onFailure(Player player) {
        player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "Failed MLG");
        Bukkit.getScheduler().runTaskLater(Training.getInstance(), () -> {
            teleportAndSetItems(player);
            player.playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 1, 1);
        }, 0);
    }

    public Location getDefaultSpawn() {
        return platforms.get((platforms.size() - 1) / 2).getSpawn().clone().add(0, 1, 0);
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
        int radius = platformRadius * 3;
        for (int index=0; index<bottomMaterials.length; index++) {
            WorldEditUtils.createCylinder(spawn.getWorld(), spawn.clone().add(0, index, 0), radius, true, 1, bottomMaterials[index]);
        }
        WorldEditUtils.createCylinder(spawn.getWorld(), spawn, radius, false, 255, borderMaterial);
        WorldEditUtils.createCylinder(spawn.getWorld(), spawn.clone().add(0, 255, 0), radius, true, 1, topMaterial);

        platforms.forEach(platform -> {
            Bukkit.getPluginManager().registerEvents(platform, Training.getInstance());
            platform.setSpawn(spawn.clone());
        });

        platforms.get(0).setUp(platforms.get(1));
        platforms.get(platforms.size() - 1).setDown(platforms.get(platforms.size() - 2));

        for (int i = 1; i < platforms.size() - 1; i++) {
            MlgPlatform mlgPlatform = platforms.get(i);
            mlgPlatform.setUp(platforms.get(i + 1));
            mlgPlatform.setDown(platforms.get(i - 1));
        }

        platforms.forEach(MlgPlatform::create);
        warpEntity.getLocation().getChunk().setForceLoaded(true);
        ((CraftWorld) MlgWorld.INSTANCE.getWorld()).addEntity(((CraftLivingEntity) warpEntity).getHandle(), CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    @Override
    public void stop() {
        platforms.forEach(MlgPlatform::clear);
        warpEntity.remove();
    }

    @Override
    public boolean isInChallenge(Player player) {
        return UserList.INSTANCE.getUser(player).isInChallenge(this);
    }

    protected void teleportAndSetItems(Player player) {
        User user = UserList.INSTANCE.getUser(player);
        if (!user.getRespawnLoc().equals(LocationUtils.DAMAGER_SPAWN)) {
            player.teleport(user.getRespawnLoc());
        } else {
            player.teleport(getDefaultSpawn());
        }
        setMlgReady(player);
    }

    @Override
    public void initConfig() {
        FileConfiguration config = Training.getInstance().getConfig();
        config.addDefault(String.format("%s.warpEntity.location", this.getName()), warpEntity.getLocation());
        config.addDefault(String.format("%s.mlgPlatform.spawn", this.getName()), spawn);
        config.addDefault(String.format("%s.location.first", this.getName()), cuboid.getFirst());
        config.addDefault(String.format("%s.location.second", this.getName()), cuboid.getSecond());
        config.options().copyDefaults(true);
        Training.getInstance().saveConfig();
    }

    @Override
    public void safeToConfig() {
        FileConfiguration config = Training.getInstance().getConfig();
        config.set(String.format("%s.warpEntity.location", this.getName()), warpEntity.getLocation());
        config.set(String.format("%s.mlgPlatform.spawn", this.getName()), spawn);
        config.set(String.format("%s.location.first", this.getName()), cuboid.getFirst());
        config.set(String.format("%s.location.second", this.getName()), cuboid.getSecond());
        Training.getInstance().saveConfig();
    }

    public void loadFromConfig() {
        Training.getInstance().reloadConfig();
        FileConfiguration config = Training.getInstance().getConfig();
        spawn = config.getLocation(String.format("%s.mlgPlatform.spawn", this.getName()), spawn);
        warpEntity.teleport(config.getLocation(String.format("%s.warpEntity.location", this.getName()), warpEntity.getLocation()));
        Location firstLoc = config.getLocation(String.format("%s.location.first", this.getName()), cuboid.getFirst());
        Location secondLoc = config.getLocation(String.format("%s.location.second", this.getName()), cuboid.getSecond());
        cuboid = new Cuboid(firstLoc, secondLoc);
    }

    @EventHandler
    public void onRightClickWarpEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity rightClicked = event.getRightClicked();
        if (rightClicked.equals(warpEntity)) {
            player.teleport(getDefaultSpawn());
            //SoundUtils.playTeleportSound(player); //TODO revert
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (isInChallenge(player)) {
            onFailure(player);
            for (ItemStack drop : event.getDrops()) {
                drop.setType(Material.AIR);
                drop.setAmount(0);
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (!isInChallenge(player)) {
                return;
            }
            if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                Block landedBlock = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
                if (Arrays.stream(bottomMaterials).noneMatch((b) -> b.equals(landedBlock.getType()))) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
