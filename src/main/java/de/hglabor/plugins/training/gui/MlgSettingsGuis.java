package de.hglabor.plugins.training.gui;

import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.UUID;

public class MlgSettingsGuis {
    private static final HashMap<UUID, MlgSettingsGui> mlgSettingsGuiMap = new HashMap<>();

    private static MlgSettingsGui getNew(UUID uuid) {
        MlgSettingsGui mlgSettingsGui = new MlgSettingsGui(Bukkit.getPlayer(uuid));
        mlgSettingsGuiMap.put(uuid, mlgSettingsGui);
        return mlgSettingsGui;
    }

    public static MlgSettingsGui get(UUID uuid) {
        if (mlgSettingsGuiMap.containsKey(uuid)) {
            return mlgSettingsGuiMap.get(uuid);
        }
        return MlgSettingsGuis.getNew(uuid);
    }
}
