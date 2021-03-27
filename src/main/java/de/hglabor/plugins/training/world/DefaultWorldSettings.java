package de.hglabor.plugins.training.world;

import de.hglabor.plugins.training.Training;
import de.hglabor.plugins.training.user.User;
import de.hglabor.plugins.training.user.UserList;
import de.hglabor.plugins.training.util.LocationUtils;
import de.hglabor.utils.noriskutils.BungeeUtils;
import de.hglabor.utils.noriskutils.ItemBuilder;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class DefaultWorldSettings implements Listener {
    private final static ItemStack RESPAWN_ANCHOR = new ItemBuilder(Material.RESPAWN_ANCHOR)
            .setName("Left click = new spawn | Right click = reset")
            .build();
    private final static ItemStack WARP_SELECTOR = new ItemBuilder(Material.NETHER_STAR)
            .setName("Warp selector")
            .build();
    private final static ItemStack HUB = new ItemBuilder(Material.HEART_OF_THE_SEA)
            .setName("Hub")
            .build();
    private final World world;

    public DefaultWorldSettings(World world) {
        this.world = world;
        this.world.setTime(6000);
        this.world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        this.world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        this.world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        this.world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        this.world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
    }

    public static void setItems(Player player) {
        player.getInventory().clear();
        player.getInventory().setItem(0, WARP_SELECTOR);
        player.getInventory().setItem(7, HUB);
        player.getInventory().setItem(8, RESPAWN_ANCHOR);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        User user = UserList.INSTANCE.getUser(event.getPlayer());
        event.setRespawnLocation(user.getRespawnLoc());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() != null) {
            ItemStack item = event.getItem();
            Action action = event.getAction();
            Player player = event.getPlayer();
            if (item.isSimilar(RESPAWN_ANCHOR)) {
                User user = UserList.INSTANCE.getUser(player);
                if (action.equals(Action.LEFT_CLICK_BLOCK) || action.equals(Action.LEFT_CLICK_AIR)) {
                    Location location = player.getLocation().getBlock().getRelative(BlockFace.DOWN).getLocation();
                    if (!location.getBlock().isSolid()) {
                        player.sendMessage(ChatColor.RED + "Here you can't set your respawn location.");      //TODO Localization
                        return;
                    }
                    user.setRespawnLoc(player.getLocation());
                    player.sendMessage(ChatColor.GREEN + "You updated your respawn location.");
                } else if (action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_AIR)) {
                    user.setRespawnLoc(LocationUtils.SPAWN);
                    player.sendMessage(ChatColor.GREEN + "Your respawn location is now default spawn.");
                }
            } else if (item.isSimilar(HUB)) {
                BungeeUtils.send(player, "lobby", Training.getInstance());
            } else if (item.isSimilar(WARP_SELECTOR)) {
                // player.openInventory();
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (isSpawn(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!isAllowedToBuild(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!isAllowedToBuild(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager().getWorld().equals(world) && event.getEntity().getWorld().equals(world)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerAttemptPickupItem(PlayerAttemptPickupItemEvent event) {
        if (isSpawn(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    private boolean isAllowedToBuild(Player player) {
        return player.getGameMode().equals(GameMode.CREATIVE) && player.getWorld().equals(world);
    }

    private boolean isSpawn(Player player) {
        if (!player.getWorld().equals(world)) {
            return false;
        }
        User user = UserList.INSTANCE.getUser(player);
        return user.isSpawn();
    }
}
