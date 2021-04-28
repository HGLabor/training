package de.hglabor.plugins.training.challenges.mlg.scoreboard;

import de.hglabor.utils.noriskutils.ChatUtils;
import de.hglabor.utils.noriskutils.scoreboard.ScoreboardPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Locale;
import java.util.UUID;

public class MlgPlayer implements ScoreboardPlayer {
    private Scoreboard scoreboard;
    private Objective objective;
    private final UUID uuid;
    private final String name;
    private final String mlgName;

    private MlgPlayer(UUID uuid, String mlgName) {
        this.uuid = uuid;
        this.name = Bukkit.getOfflinePlayer(uuid).getName();
        this.mlgName = mlgName;
    }

    public static MlgPlayer get(UUID uuid, String mlgName) {
        MlgPlayer mlgPlayer = new MlgPlayer(uuid, mlgName);
        MlgPlayerList.addMlgPlayer(uuid, mlgPlayer);
        return mlgPlayer;
    }

    public static MlgPlayer get(Player player, String mlgName) {
        return get(player.getUniqueId(), mlgName);
    }

    @Override
    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    @Override
    public void setScoreboard(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    @Override
    public Objective getObjective() {
        return objective;
    }

    @Override
    public void setObjective(Objective objective) {
        this.objective = objective;
    }

    @Override
    public Locale getLocale() {
        return ChatUtils.locale(uuid);
    }

    @Override
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public String getName() {
        return name;
    }

    public String getMlgName() {
        return mlgName;
    }
}
