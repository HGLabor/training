package de.hglabor.plugins.training.warp.worlds;

import de.hglabor.utils.noriskutils.ItemBuilder;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class MlgWorld implements Listener {
    private final static ItemStack WARP_ITEM = new ItemBuilder(Material.WATER_BUCKET)
            .setName("MLG")
            .build();
    private static World world;

    public MlgWorld(World world) {
        MlgWorld.world = world;
        MlgWorld.world.setTime(6000);
        MlgWorld.world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        MlgWorld.world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        MlgWorld.world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        MlgWorld.world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        MlgWorld.world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
    }

    public static ItemStack getWarpItem() {
        return WARP_ITEM;
    }

    public static World getWorld() {
        return world;
    }
}

