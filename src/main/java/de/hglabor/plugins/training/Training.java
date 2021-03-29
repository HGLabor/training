package de.hglabor.plugins.training;

import de.hglabor.plugins.training.challenges.Challenge;
import de.hglabor.plugins.training.challenges.ChallengeManager;
import de.hglabor.plugins.training.challenges.damager.damagers.CrapDamager;
import de.hglabor.plugins.training.challenges.damager.Damager;
import de.hglabor.plugins.training.challenges.damager.damagers.ImpossibleDamager;
import de.hglabor.plugins.training.challenges.damager.damagers.InconsistencyDamager;
import de.hglabor.plugins.training.challenges.listener.ChallengeCuboidListener;
import de.hglabor.plugins.training.challenges.mlg.Mlg;
import de.hglabor.plugins.training.challenges.mlg.mlgs.WaterMlg;
import de.hglabor.plugins.training.command.ChallengeCommand;
import de.hglabor.plugins.training.command.DamagerCommand;
import de.hglabor.plugins.training.user.UserList;
import de.hglabor.plugins.training.warp.WarpSelector;
import de.hglabor.plugins.training.warp.worlds.DamagerWorld;
import de.hglabor.plugins.training.warp.worlds.MlgWorld;
import dev.jorel.commandapi.CommandAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
import org.bukkit.entity.IronGolem;
import org.bukkit.plugin.java.JavaPlugin;

public final class Training extends JavaPlugin {
    private static Training instance;

    public static Training getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;
        CommandAPI.onLoad(true);
    }

    @Override
    public void onEnable() {
        Bukkit.createWorld(new WorldCreator("mlg"));

        DamagerWorld.INSTANCE.init(Bukkit.getWorld("world"));
        MlgWorld.INSTANCE.init(Bukkit.getWorld("mlg"));

        Bukkit.getPluginManager().registerEvents(UserList.INSTANCE, this);
        Bukkit.getPluginManager().registerEvents(new ChallengeCuboidListener(), this);
        Bukkit.getPluginManager().registerEvents(new WarpSelector(), this);

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        ChallengeManager.INSTANCE.register(new Damager("Easy", ChatColor.GREEN));
        ChallengeManager.INSTANCE.register(new Damager("Medium", ChatColor.YELLOW));
        ChallengeManager.INSTANCE.register(new Damager("Hard", ChatColor.RED));
        ChallengeManager.INSTANCE.register(new CrapDamager("Crap", ChatColor.AQUA));
        ChallengeManager.INSTANCE.register(new ImpossibleDamager("Impossible", ChatColor.DARK_GRAY));
        ChallengeManager.INSTANCE.register(new InconsistencyDamager("Inconsistency", ChatColor.LIGHT_PURPLE));

        Mlg waterMlg = new WaterMlg("Water", ChatColor.AQUA, IronGolem.class).withPlatforms(Material.IRON_BLOCK, 10, 25, 50, 100, 150, 200, 250);
        ChallengeManager.INSTANCE.register(waterMlg);


        CommandAPI.onEnable(this);
        new DamagerCommand();
        new ChallengeCommand();
    }

    @Override
    public void onDisable() {
        ChallengeManager.INSTANCE.getChallenges().forEach(Challenge::stop);
    }
}
