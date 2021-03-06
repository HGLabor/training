package de.hglabor.plugins.training.challenges;

import de.hglabor.plugins.training.main.TrainingKt;
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
        TrainingKt.getPLUGIN().registerAllEventListeners(challenge);
        challenge.initConfig();
        challenge.loadFromConfig();
        challenge.start();
    }

    /** Register multiple challenges @ once */
    public void registerAll(Challenge... challenges) {
        for (Challenge challenge : challenges) {
            this.register(challenge);
        }
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
