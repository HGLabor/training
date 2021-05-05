package de.hglabor.plugins.training.command;

import de.hglabor.plugins.training.settings.MlgSettings;
import dev.jorel.commandapi.CommandAPICommand;

public class MlgSettingsCommand {
    public MlgSettingsCommand() {
        new CommandAPICommand("mlgsettings")
                .executesPlayer((player, objects) -> {
                    MlgSettings.get(player.getUniqueId()).openGui();
                }).register();
    }
}
