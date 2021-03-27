package de.hglabor.plugins.training.challenges;

import de.hglabor.plugins.training.Training;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;

public final class ChallengeManager {
    public final static ChallengeManager INSTANCE = new ChallengeManager();
    private final Set<Challenge> challenges;

    private ChallengeManager() {
        this.challenges = new HashSet<>();
    }

    public void register(Challenge challenge) {
        challenges.add(challenge);
        Bukkit.getPluginManager().registerEvents(challenge, Training.getInstance());
        challenge.initConfig();
        challenge.loadFromConfig();
        challenge.start();
    }

    public Set<Challenge> getChallenges() {
        return challenges;
    }

    public Challenge byRegion(Location location) {
        for (Challenge challenge : challenges) {
            if (challenge.getArea().contains(location)) {
                return challenge;
            }
        }
        return null;
    }
}
