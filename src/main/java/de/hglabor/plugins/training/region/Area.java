package de.hglabor.plugins.training.region;

import org.bukkit.Location;

public interface Area {
    boolean contains(Location location);

    void setFirstLoc(Location first);

    void setSecondLoc(Location second);
}

