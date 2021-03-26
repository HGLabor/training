package de.hglabor.plugins.training.challenges;

import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;

public final class ChallengeManager {
    public final static ChallengeManager INSTANCE = new ChallengeManager();
    private final Set<Challenge> challenges;

    private ChallengeManager() {
        this.challenges = new HashSet<>();
    }

    public void addChallenge(Challenge challenge) {
        challenges.add(challenge);
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
