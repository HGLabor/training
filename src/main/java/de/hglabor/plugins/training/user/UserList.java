package de.hglabor.plugins.training.user;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class UserList {
    public final static UserList INSTANCE = new UserList();
    private final Map<UUID, User> users;

    private UserList() {
        this.users = new HashMap<>();
    }

    public User getUser(Player player) {
        return users.computeIfAbsent(player.getUniqueId(), User::new);
    }
}
