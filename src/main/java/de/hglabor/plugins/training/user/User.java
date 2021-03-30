package de.hglabor.plugins.training.user;

import de.hglabor.plugins.training.challenges.Challenge;
import de.hglabor.plugins.training.region.Area;
import de.hglabor.plugins.training.user.stats.SoupStats;
import de.hglabor.plugins.training.util.LocationUtils;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class User {
    private final UUID uuid;
    private final SoupStats soupStats;
    private final Map<Class<?>, Long> cooldowns;
    private final Map<Challenge, Object> challengeInfos;
    private Area area;
    private Challenge challenge;
    private Location respawnLoc;
    private boolean isSpawn;

    public User(UUID uuid) {
        this.uuid = uuid;
        this.respawnLoc = LocationUtils.DAMAGER_SPAWN;
        this.soupStats = new SoupStats();
        this.cooldowns = new HashMap<>();
        this.challengeInfos = new HashMap<>();
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

    public void addCooldown(Class<?> clazz, long timeStamp) {
        this.cooldowns.put(clazz, timeStamp);
    }

    public <T> void addChallengeInfo(Challenge challenge, T value) {
        this.challengeInfos.put(challenge, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getChallengeInfo(Challenge challenge) {
        return (T) challengeInfos.get(challenge);
    }

    public <T> T getChallengeInfoOrDefault(Challenge challenge, T def) {
        if (!challengeInfos.containsKey(challenge)) {
            return def;
        }
        return getChallengeInfo(challenge);
    }

    public boolean hasCooldown(Class<?> clazz) {
        if (!this.cooldowns.containsKey(clazz)) {
            return false;
        }
        return this.cooldowns.get(clazz) > System.currentTimeMillis();
    }

    public boolean isInChallenge(Challenge challenge) {
        if (this.challenge == null) {
            return false;
        }
        return this.challenge.equals(challenge);
    }
}
