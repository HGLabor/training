package de.hglabor.plugins.training.challenges.damager.damagers;

import de.hglabor.plugins.training.challenges.damager.Damager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class ImpossibleDamager extends Damager {
    public ImpossibleDamager(String name, ChatColor color) {
        super(name, color);
    }

    @Override
    public void onEnter(Player player) {
        super.onEnter(player);
        player.setMaximumNoDamageTicks(0);
    }

    @Override
    public void onComplete(Player player) {
        super.onComplete(player);
        player.setMaximumNoDamageTicks(20);
    }

    @Override
    public void onFailure(Player player) {
        super.onFailure(player);
        player.setMaximumNoDamageTicks(20);
    }
}
