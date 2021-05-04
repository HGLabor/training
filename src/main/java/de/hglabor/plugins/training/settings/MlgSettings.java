package de.hglabor.plugins.training.settings;

import de.hglabor.plugins.training.gui.MlgSettingsGui;
import de.hglabor.plugins.training.gui.MlgSettingsGuis;

import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;

public class MlgSettings implements Serializable {
    public static HashMap<UUID, MlgSettings> mlgSettingsMap = new HashMap<>();
    public boolean jumpSneakState;
    private final UUID uuid;

    private MlgSettings(UUID uuid) {
        // Create gui if not created yet
        MlgSettingsGuis.get(uuid);
        this.uuid = uuid;
    }

    private static MlgSettings getNew(UUID uuid) {
        MlgSettings mlgSettings = new MlgSettings(uuid);
        mlgSettingsMap.put(uuid, mlgSettings);
        return mlgSettings;
    }

    public static MlgSettings get(UUID uuid) {
        if (mlgSettingsMap.containsKey(uuid)) {
            return mlgSettingsMap.get(uuid);
        }
        return MlgSettings.getNew(uuid);
    }

    public void openGui() {
        MlgSettingsGui gui = MlgSettingsGuis.get(uuid);
        gui.open(() -> this.jumpSneakState = gui.jumpSneakElevator.getState());
        gui.jumpSneakElevator.setState(jumpSneakState);
    }
}
