package de.hglabor.plugins.training;

import de.hglabor.plugins.training.challenges.Challenge;
import de.hglabor.plugins.training.challenges.ChallengeManager;
import de.hglabor.plugins.training.challenges.damager.Damager;
import de.hglabor.plugins.training.challenges.damager.damagers.CrapDamager;
import de.hglabor.plugins.training.challenges.damager.damagers.ImpossibleDamager;
import de.hglabor.plugins.training.challenges.damager.damagers.InconsistencyDamager;
import de.hglabor.plugins.training.challenges.listener.ChallengeCuboidListener;
import de.hglabor.plugins.training.challenges.mlg.Mlg;
import de.hglabor.plugins.training.challenges.mlg.mlgs.*;
import de.hglabor.plugins.training.command.ChallengeCommand;
import de.hglabor.plugins.training.command.DamagerCommand;
import de.hglabor.plugins.training.command.MlgCommand;
import de.hglabor.plugins.training.user.UserList;
import de.hglabor.plugins.training.warp.WarpSelector;
import de.hglabor.plugins.training.warp.worlds.DamagerWorld;
import de.hglabor.plugins.training.warp.worlds.MlgWorld;
import dev.jorel.commandapi.CommandAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
import org.bukkit.entity.*;
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

        Mlg blockMlg = new BlockMlg("Block", ChatColor.WHITE, IronGolem.class).withPlatforms(Material.POLISHED_DIORITE, 10, 10, 20, 50, 100, 150, 200, 250);
        Mlg horseMlg = new HorseMlg("Horse", ChatColor.GOLD, Horse.class).withPlatforms(Material.DARK_OAK_PLANKS, 10, 25, 50, 100, 150, 200, 250);
        Mlg boatMlg = new BoatMlg("Boat", ChatColor.YELLOW, Boat.class).withPlatforms(Material.OAK_PLANKS, 10, 25, 50, 100, 150, 200, 250);
        Mlg minecartMlg = new MinecartMlg("Minecart", ChatColor.GRAY, Minecart.class).withPlatforms(Material.GRAY_GLAZED_TERRACOTTA, 10, 10, 15, 20, 25, 50, 100, 150, 200, 250);
        Mlg striderMlg = new StriderMlg("Strider", ChatColor.LIGHT_PURPLE, Strider.class).withPlatforms(Material.CRIMSON_NYLIUM, 10, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 90, 100, 200, 250);
        Mlg ladderMlg = new LadderMlg("Ladder", ChatColor.GOLD, WanderingTrader.class).withPlatforms(Material.STRIPPED_OAK_WOOD, 10, 10, 20, 50, 100, 150, 200, 250);
        Mlg potionMlg = new PotionMlg("Potion", ChatColor.GREEN, Fox.class).withPlatforms(Material.SMOOTH_SANDSTONE, 10, 10, 20, 50, 100, 150, 200, 250);
        ChallengeManager.INSTANCE.registerAll(blockMlg, horseMlg, boatMlg, striderMlg, minecartMlg, ladderMlg/*, potionMlg*/);


        CommandAPI.onEnable(this);
        new DamagerCommand();
        new ChallengeCommand();
        new MlgCommand();
    }

    @Override
    public void onDisable() {
        ChallengeManager.INSTANCE.getChallenges().forEach(Challenge::stop);
    }
}
