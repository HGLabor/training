package de.hglabor.plugins.training.settings;

import de.hglabor.plugins.training.gui.MlgSettingsGui;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;

public class MlgSettings implements Serializable {
    public static HashMap<UUID, MlgSettings> mlgSettingsMap = new HashMap<>();

    public interface JumpSneakStates {
        String ALWAYS_ACTIVE = ChatColor.GREEN + "Always active";
        String ONLY_GLASS = ChatColor.YELLOW + "Only when on glass blocks";
        String DISABLED = ChatColor.RED + "Disabled";
    }
    private String jumpSneakState = JumpSneakStates.ALWAYS_ACTIVE;

    private MlgSettings(UUID uuid) {

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

    public void openGui(UUID uuid) {
        MlgSettingsGui gui = new MlgSettingsGui(Bukkit.getPlayer(uuid));
        gui.open(() -> this.jumpSneakState = gui.jumpSneakElevator.getCurrentState());
        gui.jumpSneakElevator.setCurrentState(jumpSneakState);
    }
}
