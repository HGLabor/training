package de.hglabor.plugins.training;

import org.bukkit.plugin.java.JavaPlugin;

public final class Training extends JavaPlugin {
    private static Training instance;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    public static Training getInstance() {
        return instance;
    }
}
