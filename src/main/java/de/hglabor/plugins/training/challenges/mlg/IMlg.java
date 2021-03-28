package de.hglabor.plugins.training.challenges.mlg;

import de.hglabor.plugins.training.challenges.Challenge;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public interface IMlg extends Challenge {
    LivingEntity getWarpEntity();

    void onRightClickWarpEntity(PlayerInteractEntityEvent event);
}
