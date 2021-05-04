package de.hglabor.plugins.training.gui.button;

import de.hglabor.plugins.training.gui.AbstractGui;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;

public class ToggleButton extends AbstractButton {
    private boolean state;
    public ToggleButton(AbstractGui context, int slot, String title, Material material) {
        super(context, slot, title, material);
        state = true;
        updateDescription();
    }

    @Override
    protected void onClick(ClickType clickType) {
        if (!clickType.isLeftClick()) return;
        state = !state;
        Bukkit.getLogger().info("Set state to " + state);
        updateDescription();
    }

    private void updateDescription() {
        if (state) {
            this.description = ChatColor.GREEN + "Enabled";
        }
        else {
            this.description = ChatColor.RED + "Disabled";
        }
        this.updateItemStack();
    }


    public boolean getState() {
        return state;
    }

    public void setState(boolean value) {
        this.state = value;
        updateDescription();
    }
}
