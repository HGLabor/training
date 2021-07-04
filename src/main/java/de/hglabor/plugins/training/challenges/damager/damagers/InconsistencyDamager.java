package de.hglabor.plugins.training.challenges.damager.damagers;

import de.hglabor.plugins.training.challenges.damager.Damager;
import de.hglabor.plugins.training.main.TrainingKt;
import de.hglabor.utils.noriskutils.ChanceUtils;
import de.hglabor.utils.noriskutils.HologramUtils;
import net.md_5.bungee.api.ChatColor;

import java.util.Arrays;

public class InconsistencyDamager extends Damager {
    private double minDamage;

    public InconsistencyDamager(String name, ChatColor color) {
        super(name, color);
    }

    @Override
    public void setHolograms() {
        holograms[0] = HologramUtils.spawnHologram(hologramOrigin, color + this.getName() + " Damager");
        holograms[1] = HologramUtils.spawnHologram(hologramOrigin.clone().subtract(0, 0.25, 0), ChatColor.WHITE + "Damage: " + ChatColor.GOLD + minDamage / 2 + " - " + damage / 2 + ChatColor.RED.toString() + " \u2764");
        holograms[2] = HologramUtils.spawnHologram(hologramOrigin.clone().subtract(0, 0.5, 0), ChatColor.WHITE + "Tickrate: " + ChatColor.GOLD + tickSpeed);
        Arrays.stream(holograms).forEach(hologram -> hologram.setPersistent(false));
    }

    @Override
    public void initConfig() {
        TrainingKt.getPLUGIN().getConfig().addDefault(String.format("%s.minDamage", configKey), minDamage);
        super.initConfig();
    }

    @Override
    public double getDamage() {
        return ChanceUtils.getRandomDouble(minDamage, damage);
    }

    @Override
    public void loadFromConfig() {
        super.loadFromConfig();
        minDamage = TrainingKt.getPLUGIN().getConfig().getDouble(String.format("%s.minDamage", configKey), minDamage);
    }

    @Override
    public void saveToConfig() {
        TrainingKt.getPLUGIN().getConfig().set(String.format("%s.minDamage", configKey), minDamage);
        super.saveToConfig();
    }
}
