package de.hglabor.plugins.training.listener;

import de.hglabor.plugins.training.challenges.Challenge;
import de.hglabor.plugins.training.challenges.ChallengeManager;
import de.hglabor.plugins.training.user.User;
import de.hglabor.plugins.training.user.UserList;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class RegionMoveListener implements Listener {

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
            if (user.getArea().equals(challenge.getArea())) {
                return;
            }
            user.setArea(challenge.getArea());
            challenge.getArea().onEnterArea(player);
        } else {
            //TODO Spawn
        }
    }
}
