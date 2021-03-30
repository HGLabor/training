package de.hglabor.plugins.training.user;

import de.hglabor.plugins.training.util.LocationUtils;
import de.hglabor.plugins.training.warp.worlds.DamagerWorld;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class UserList implements Listener {
    public final static UserList INSTANCE = new UserList();
    private final Map<UUID, User> users;

    private UserList() {
        this.users = new HashMap<>();
    }

    public User getUser(Player player) {
        return users.computeIfAbsent(player.getUniqueId(), User::new);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        event.getPlayer().teleport(LocationUtils.DAMAGER_SPAWN);
        getUser(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Player player = event.getPlayer();
        UUID uniqueId = player.getUniqueId();
        User user = getUser(player);
        if (user.getChallenge() != null) {
            user.getChallenge().onLeave(player);
        }
        users.remove(uniqueId);
    }
}
