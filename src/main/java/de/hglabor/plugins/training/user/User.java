package de.hglabor.plugins.training.user;

import de.hglabor.plugins.training.challenges.Challenge;
import de.hglabor.plugins.training.region.Area;
import de.hglabor.plugins.training.user.stats.SoupStats;
import de.hglabor.plugins.training.util.LocationUtils;
import org.bukkit.Location;

import java.util.UUID;

public class User {
    private final UUID uuid;
    private final SoupStats soupStats;
    private Area area;
    private Challenge challenge;
    private Location respawnLoc;
    private boolean isSpawn;

    public User(UUID uuid) {
        this.uuid = uuid;
        this.respawnLoc = LocationUtils.DAMAGER_SPAWN;
        this.soupStats = new SoupStats();
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    public boolean isSameArea(Area area) {
        if (area == null) {
            return false;
        }
        return this.area.equals(area);
    }

    public Location getRespawnLoc() {
        return respawnLoc;
    }

    public void setRespawnLoc(Location respawnLoc) {
        this.respawnLoc = respawnLoc;
    }

    public boolean isSpawn() {
        return isSpawn;
    }

    public void setSpawn(boolean spawn) {
        isSpawn = spawn;
    }

    public SoupStats getSoupStats() {
        return soupStats;
    }

    public boolean isInChallenge(Challenge challenge) {
        if (this.challenge == null) {
            return false;
        }
        return this.challenge.equals(challenge);
    }
}
