package de.hglabor.plugins.training.challenges.damager;

import de.hglabor.plugins.training.Training;
import de.hglabor.plugins.training.challenges.Challenge;
import de.hglabor.plugins.training.mechanics.SoupHealing;
import de.hglabor.plugins.training.region.Area;
import de.hglabor.plugins.training.region.Cuboid;
import de.hglabor.plugins.training.user.User;
import de.hglabor.plugins.training.user.UserList;
import de.hglabor.plugins.training.util.LocationUtils;
import de.hglabor.utils.noriskutils.HologramUtils;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class Damager implements Challenge {
    protected final ChatColor color;
    protected final String name;
    protected final Map<UUID, Boolean> players;
    protected final SoupHealing soupHealing;
    protected final String configKey;
    protected final ArmorStand[] holograms;
    protected final Set<Item> droppedItems;
    protected double damage;
    protected int soupsToEat;
    protected long tickSpeed;
    protected Location hologramOrigin;
    protected Cuboid cuboid;
    protected BukkitTask task;

    public Damager(String name, ChatColor color) {
        this.name = name;
        this.configKey = "Damager" + name;
        this.color = color;
        this.tickSpeed = 20L;
        this.damage = 4D;
        this.soupsToEat = 96;
        this.droppedItems = new HashSet<>();
        this.holograms = new ArmorStand[3];
        this.soupHealing = new SoupHealing();
        this.players = new HashMap<>();
        this.hologramOrigin = LocationUtils.ZERO_DAMAGER;
        this.cuboid = new Cuboid(LocationUtils.ZERO_DAMAGER, LocationUtils.ZERO_DAMAGER);
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

    public void setHologramOrigin(Location hologramOrigin) {
        this.hologramOrigin = hologramOrigin;
    }

    public Runnable getDamageRunnable() {
        return () -> {
            if (task.isCancelled()) {
                return;
            }
            List<UUID> uuids = new ArrayList<>(players.keySet());
            for (UUID uuid : uuids) {
                if (players.get(uuid)) {
                    continue;
                }
                Player player = (Player) Bukkit.getEntity(uuid);
                if (player != null) {
                    player.damage(getDamage());
                }
            }
        };
    }

    public double getDamage() {
        return damage;
    }

    @Override
    public void start() {
        setHolograms();
        task = Bukkit.getScheduler().runTaskTimer(Training.getInstance(), getDamageRunnable(), 0, tickSpeed);
    }

    public void setHolograms() {
        holograms[0] = HologramUtils.spawnHologram(hologramOrigin, color + this.getName() + " Damager");
        holograms[1] = HologramUtils.spawnHologram(hologramOrigin.clone().subtract(0, 0.25, 0), ChatColor.WHITE + "Damage: " + ChatColor.GOLD + damage / 2 + ChatColor.RED.toString() + " \u2764");
        holograms[2] = HologramUtils.spawnHologram(hologramOrigin.clone().subtract(0, 0.5, 0), ChatColor.WHITE + "Tickrate: " + ChatColor.GOLD + tickSpeed);
        for (ArmorStand hologram : holograms) {
            hologram.getLocation().getChunk().setForceLoaded(true);
            hologram.setPersistent(false);
        }
    }

    @Override
    public void stop() {
        for (ArmorStand hologram : holograms) {
            hologram.remove();
        }
        task.cancel();
    }

    @Override
    public void onEnter(Player player) {
        player.sendMessage("You entered " + this.getName());
        players.put(player.getUniqueId(), false);
        PlayerInventory inventory = player.getInventory();
        player.setHealth(player.getMaxHealth());
        inventory.clear();
        inventory.addItem(new ItemStack(Material.STONE_SWORD));
        int size = 32;
        if (soupsToEat > size) {
            int soupsLeft = soupsToEat - size;
            inventory.setItem(13, new ItemStack(Material.BOWL, soupsLeft));
            inventory.setItem(14, new ItemStack(Material.RED_MUSHROOM, soupsLeft));
            inventory.setItem(15, new ItemStack(Material.BROWN_MUSHROOM, soupsLeft));
        }
        for (int i = 0; i < size; i++) {
            inventory.addItem(new ItemStack(Material.MUSHROOM_STEW));
        }
    }

    @Override
    public void onComplete(Player player) {
        User user = UserList.INSTANCE.getUser(player);
        players.put(player.getUniqueId(), true);
        player.closeInventory();
        player.getInventory().clear();
        player.sendMessage(ChatColor.GREEN + "You completed " + this.getName()); //TODO Localization
        printAndResetSoupStats(player, user);
        player.teleport(user.getRespawnLoc());
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_CELEBRATE, 1, 1);
    }

    @Override
    public void onFailure(Player player) {
        User user = UserList.INSTANCE.getUser(player);
        players.remove(player.getUniqueId());
        player.sendMessage(ChatColor.RED + "You failed " + this.getName()); //TODO Localization
        printAndResetSoupStats(player, user);
    }

    private void printAndResetSoupStats(Player player, User user) {
        player.sendMessage("Soups eaten: " + user.getSoupStats().getSoupsEaten());
        player.sendMessage("Soups dropped: " + user.getSoupStats().getSoupsDropped());
        user.getSoupStats().reset();
    }

    @Override
    public void onLeave(Player player) {
        player.sendMessage("You left " + this.getName());
        droppedItems.removeIf(item -> {
            if (item.getOwner() != null && item.getOwner().equals(player.getUniqueId())) {
                item.getItemStack().setType(Material.AIR);
                item.getItemStack().setAmount(0);
                item.remove();
                return true;
            }
            return false;
        });
        players.remove(player.getUniqueId());
    }

    @Override
    public boolean isInChallenge(Player player) {
        return players.containsKey(player.getUniqueId());
    }

    @Override
    public void initConfig() {
        FileConfiguration config = Training.getInstance().getConfig();
        config.addDefault(String.format("%s.damage", configKey), damage);
        config.addDefault(String.format("%s.soupsToEat", configKey), soupsToEat);
        config.addDefault(String.format("%s.tickSpeed", configKey), tickSpeed);
        config.addDefault(String.format("%s.location.first", configKey), cuboid.getFirst());
        config.addDefault(String.format("%s.location.second", configKey), cuboid.getSecond());
        config.addDefault(String.format("%s.hologram.location", configKey), hologramOrigin);
        config.options().copyDefaults(true);
        Training.getInstance().saveConfig();
    }

    @Override
    public void safeToConfig() {
        FileConfiguration config = Training.getInstance().getConfig();
        config.set(String.format("%s.damage", configKey), damage);
        config.set(String.format("%s.soupsToEat", configKey), soupsToEat);
        config.set(String.format("%s.tickSpeed", configKey), tickSpeed);
        config.set(String.format("%s.location.first", configKey), cuboid.getFirst());
        config.set(String.format("%s.location.second", configKey), cuboid.getSecond());
        config.set(String.format("%s.hologram.location", configKey), hologramOrigin);
        Training.getInstance().saveConfig();
    }

    @Override
    public void loadFromConfig() {
        Training.getInstance().reloadConfig();
        FileConfiguration config = Training.getInstance().getConfig();
        damage = config.getDouble(String.format("%s.damage", configKey), damage);
        soupsToEat = config.getInt(String.format("%s.soupsToEat", configKey), soupsToEat);
        tickSpeed = config.getLong(String.format("%s.tickSpeed", configKey), tickSpeed);
        Location firstLoc = config.getLocation(String.format("%s.location.first", configKey), cuboid.getFirst());
        Location secondLoc = config.getLocation(String.format("%s.location.second", configKey), cuboid.getSecond());
        cuboid = new Cuboid(firstLoc, secondLoc);
        hologramOrigin = config.getLocation(String.format("%s.hologram.location", configKey), hologramOrigin);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        User user = UserList.INSTANCE.getUser(player);

        if (!isInChallenge(player)) {
            return;
        }

        if (soupHealing.onRightClickSoup(event)) {
            user.getSoupStats().increaseSoupsEaten();
            if (user.getSoupStats().getSoupsEaten() == soupsToEat) {
                onComplete(player);
            }
        }
    }

    @EventHandler
    public void onPlayerAttemptPickupItem(PlayerAttemptPickupItemEvent event) {
        Item item = event.getItem();
        Player player = event.getPlayer();
        if (item.getOwner() != null && !item.getOwner().equals(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && isInChallenge((Player) event.getEntity())) {
            if (!event.getCause().equals(EntityDamageEvent.DamageCause.CUSTOM)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Item itemDrop = event.getItemDrop();
        if (isInChallenge(player)) {
            User user = UserList.INSTANCE.getUser(player);
            itemDrop.setOwner(player.getUniqueId());
            droppedItems.add(itemDrop);
            if (SoupHealing.SOUP_MATERIAL.contains(itemDrop.getItemStack().getType())) {
                user.getSoupStats().increaseSoupsDropped();
            }
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
        }
    }
}
