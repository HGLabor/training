package de.hglabor.plugins.training.command;

import de.hglabor.plugins.training.settings.mlg.SettingGui;
import dev.jorel.commandapi.CommandAPICommand;

public class SettingsCommand {
    public SettingsCommand() {
        new CommandAPICommand("settings")
                .executesPlayer((player, objects) -> {
                    SettingGui.INSTANCE.open(player);
                }).register();
    }
}
