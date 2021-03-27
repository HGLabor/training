package de.hglabor.plugins.training;

import de.hglabor.plugins.training.challenges.Challenge;
import de.hglabor.plugins.training.challenges.ChallengeManager;
import de.hglabor.plugins.training.challenges.damager.CrapDamager;
import de.hglabor.plugins.training.challenges.damager.Damager;
import de.hglabor.plugins.training.challenges.damager.ImpossibleDamager;
import de.hglabor.plugins.training.challenges.damager.InconsistencyDamager;
import de.hglabor.plugins.training.challenges.listener.ChallengeCuboidListener;
import de.hglabor.plugins.training.command.ChallengeCommand;
import de.hglabor.plugins.training.command.DamagerCommand;
import de.hglabor.plugins.training.user.UserList;
import de.hglabor.plugins.training.world.DefaultWorldSettings;
import dev.jorel.commandapi.CommandAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
        ChallengeManager.INSTANCE.register(new Damager("Easy", ChatColor.GREEN));
        ChallengeManager.INSTANCE.register(new Damager("Medium", ChatColor.YELLOW));
        ChallengeManager.INSTANCE.register(new Damager("Hard", ChatColor.RED));
        ChallengeManager.INSTANCE.register(new CrapDamager("Crap", ChatColor.AQUA));
        ChallengeManager.INSTANCE.register(new ImpossibleDamager("Impossible", ChatColor.DARK_GRAY));
        ChallengeManager.INSTANCE.register(new InconsistencyDamager("Inconsistency", ChatColor.LIGHT_PURPLE));

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        Bukkit.getPluginManager().registerEvents(UserList.INSTANCE, this);
        Bukkit.getPluginManager().registerEvents(new ChallengeCuboidListener(), this);
        Bukkit.getPluginManager().registerEvents(new DefaultWorldSettings(Bukkit.getWorld("world")), this);

        CommandAPI.onEnable(this);
        new DamagerCommand();
        new ChallengeCommand();
    }

    @Override
    public void onDisable() {
        ChallengeManager.INSTANCE.getChallenges().forEach(Challenge::stop);
    }
}
