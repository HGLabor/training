package de.hglabor.plugins.training.warp.worlds;

import de.hglabor.plugins.training.Training;
import de.hglabor.plugins.training.user.User;
import de.hglabor.plugins.training.user.UserList;
import de.hglabor.plugins.training.util.LocationUtils;
import de.hglabor.plugins.training.warp.WarpItems;
import de.hglabor.utils.noriskutils.BungeeUtils;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public abstract class TrainingWorld implements Listener {
    protected final ItemStack warpItem;
    protected World world;

    protected TrainingWorld(ItemStack warpItem) {
        this.warpItem = warpItem;
    }

    public void init(World world) {
        this.world = world;
        this.world.setTime(6000);
        this.world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        this.world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        this.world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        this.world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        this.world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
        this.world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
        Bukkit.getPluginManager().registerEvents(this, Training.getInstance());
    }

    public abstract void setItems(Player player);

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getPlayer().getWorld().equals(world)) {
            return;
        }
        if (event.getItem() == null) {
            return;
        }
        ItemStack item = event.getItem();
        Action action = event.getAction();
        Player player = event.getPlayer();
        if (item.isSimilar(WarpItems.RESPAWN_ANCHOR)) {
            User user = UserList.INSTANCE.getUser(player);
            if (action.equals(Action.LEFT_CLICK_BLOCK) || action.equals(Action.LEFT_CLICK_AIR)) {
                Location location = player.getLocation().getBlock().getRelative(BlockFace.DOWN).getLocation();
                if (!location.getBlock().isSolid()) {
                    player.sendMessage(ChatColor.RED + "Here you can't set your respawn location.");      //TODO Localization
                    return;
                }
                user.setRespawnLoc(player.getLocation());
                player.sendMessage(ChatColor.GREEN + "You updated your respawn location.");
                player.playSound(player.getLocation(), Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON, 1, 1);
            } else if (action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_AIR)) {
                user.setRespawnLoc(LocationUtils.DAMAGER_SPAWN);
                player.sendMessage(ChatColor.GREEN + "Your respawn location is now default spawn.");
                player.playSound(player.getLocation(), Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, 1, 1);
            }
        } else if (item.isSimilar(WarpItems.HUB)) {
            BungeeUtils.send(player, "lobby", Training.getInstance());
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (WarpItems.isWarpItem(event.getBlock().getType())) {
            event.setCancelled(true);
        }

        if (!isAllowedToBuild(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (player.getWorld().equals(world)) {
            User user = UserList.INSTANCE.getUser(player);
            event.setRespawnLocation(user.getRespawnLoc());
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!event.getEntity().getWorld().equals(world)) {
            return;
        }

        if (event.getDamager().getWorld().equals(world) && event.getEntity().getWorld().equals(world)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.getPlayer().getWorld().equals(world)) {
            return;
        }
        if (!isAllowedToBuild(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!event.getEntity().getWorld().equals(world)) {
            return;
        }
        if (isSpawn((Player) event.getEntity())) {
            event.setFoodLevel(40);
            event.setCancelled(true);
        }
    }

    public World getWorld() {
        return world;
    }

    public Location getSpawn() {
        return world.getSpawnLocation();
    }

    public ItemStack getWarpItem() {
        return warpItem;
    }

    protected boolean isAllowedToBuild(Player player) {
        return player.getGameMode().equals(GameMode.CREATIVE) && player.getWorld().equals(world);
    }

    protected boolean isSpawn(Player player) {
        if (!player.getWorld().equals(world)) {
            return false;
        }
        User user = UserList.INSTANCE.getUser(player);
        return user.isSpawn();
    }
}
