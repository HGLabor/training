package de.hglabor.plugins.training.command;

import de.hglabor.plugins.training.gui.guis.MlgSettingsGui;
import dev.jorel.commandapi.CommandAPICommand;

public class MlgSettingsCommand {
    public MlgSettingsCommand() {
        new CommandAPICommand("mlgsettings")
                .executesPlayer((player, objects) -> {
                    MlgSettingsGui.INSTANCE.open(player);
                }).register();
    }
}
