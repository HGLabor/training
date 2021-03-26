package de.hglabor.plugins.training.user;

import de.hglabor.plugins.training.region.Area;

import java.util.UUID;

public class User {
    private final UUID uuid;
    private Area area;

    public User(UUID uuid) {
        this.uuid = uuid;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }
}
