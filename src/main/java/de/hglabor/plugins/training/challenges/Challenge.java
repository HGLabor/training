package de.hglabor.plugins.training.challenges;

import de.hglabor.plugins.training.region.Area;
import de.hglabor.plugins.training.user.User;
import de.hglabor.plugins.training.user.UserList;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public interface Challenge extends Listener {
    String getName();

    Area getArea();

    ChatColor getColor();

    void start();

    void stop();

    default void restart() {
        stop();
        loadFromConfig();
        start();
    }

    void onEnter(Player player);

    void onLeave(Player player);

    void onComplete(Player player);

    void onFailure(Player player);

    boolean isInChallenge(Player player);

    void initConfig();

    void safeToConfig();

    void loadFromConfig();

    default User getUser(Player player) {
        return UserList.INSTANCE.getUser(player);
    }
}
