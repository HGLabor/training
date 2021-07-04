package de.hglabor.plugins.training.challenges.listener;

import de.hglabor.plugins.training.challenges.Challenge;
import de.hglabor.plugins.training.challenges.ChallengeManager;
import de.hglabor.plugins.training.user.User;
import de.hglabor.plugins.training.user.UserList;
import de.hglabor.plugins.training.warp.worlds.DamagerWorld;
import de.hglabor.plugins.training.warp.worlds.MlgWorld;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Objects;

public class ChallengeCuboidListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        //Player was just moving mouse
        if (event.getTo().distanceSquared(event.getFrom()) == 0) {
            return;
        }
        Player player = event.getPlayer();
        Challenge challenge = ChallengeManager.INSTANCE.byRegion(event.getTo());
        User user = UserList.INSTANCE.getUser(player);

        if (challenge != null) {
            if (user.isInChallenge(challenge)) {
                return;
            }
            leavePreviousChallenge(player, user);
            if (user.isSpawn()) {
                user.setSpawn(false);
                player.setWalkSpeed(0.2F);
            }
            user.setChallenge(challenge);
            challenge.onEnter(player);
        } else {
            leavePreviousChallenge(player, user);
            if (!user.isSpawn()) {
                user.setSpawn(true);
                player.setHealth(player.getHealthScale());
                player.setWalkSpeed(0.5F);
                DamagerWorld.INSTANCE.setItems(player);
                MlgWorld.INSTANCE.setItems(player);
            }
        }
    }

    private void leavePreviousChallenge(Player player, User user) {
        if (user.getChallenge() != null) {
            user.getChallenge().onLeave(player);
            user.setChallenge(null);
        }
    }
}
