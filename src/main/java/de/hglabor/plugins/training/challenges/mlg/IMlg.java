package de.hglabor.plugins.training.challenges.mlg;

import de.hglabor.plugins.training.challenges.Challenge;
import org.bukkit.entity.LivingEntity;

public interface IMlg extends Challenge {
    LivingEntity getWarpEntity();
}
