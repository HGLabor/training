package de.hglabor.plugins.training.region;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public interface Area {
    void onEnterArea(Player consumer);

    boolean contains(Location location);
}

