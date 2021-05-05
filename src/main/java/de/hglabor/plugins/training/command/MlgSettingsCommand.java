package de.hglabor.plugins.training.command;

import de.hglabor.plugins.training.Training;
import de.hglabor.plugins.training.settings.MlgSettings;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.TraderLlama;

public class MlgSettingsCommand {
    public MlgSettingsCommand() {
        new CommandAPICommand("mlgsettings")
                .executesPlayer((player, objects) -> {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Training.getInstance(), () -> MlgSettings.get(player.getUniqueId()).openGui()); }).register();
    }
}
