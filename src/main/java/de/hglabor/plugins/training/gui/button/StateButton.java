package de.hglabor.plugins.training.gui.button;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;

public class StateButton extends AbstractButton {
    private final String[] states;
    private int currentState;
    public StateButton(Inventory gui, int slot, String title, Material material, String[] states) {
        super(gui, slot, title, material);
        this.states = states;
        currentState = 0;
        updateDescription();
    }

    @Override
    protected void onClick(ClickType clickType) {
        currentState++;
        if (currentState >= states.length) {
            currentState = 0;
        }
        updateDescription();
    }

    private void updateDescription() {
        this.description = getCurrentState();
        this.updateItemStack();
    }

    public String getCurrentState() {
        return states[currentState];
    }

    public void setCurrentState(String state) {
        currentState = ArrayUtils.indexOf(states, state);
        updateDescription();
    }
}
