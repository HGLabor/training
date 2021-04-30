package de.hglabor.plugins.training.gui.button;

import de.hglabor.plugins.training.Training;
import de.hglabor.utils.noriskutils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractButton implements Listener {
    protected String title;
    protected String description = "";
    protected Material material;
    public ItemStack itemStack;
    protected Inventory gui;
    protected int slot;

    public AbstractButton(Inventory gui, int slot, String title, Material material) {
        this.title = title;
        this.material = material;
        this.gui = gui;
        this.slot = slot;
        updateItemStack();
        Training.getInstance().registerAllEventListeners(this);
    }

    protected void updateItemStack() {
        itemStack = new ItemBuilder(material).setName(title).setDescription(description).build();
        gui.setItem(slot, itemStack);
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) {
            return;
        }
        if (event.getCurrentItem().isSimilar(itemStack)) {
            event.setCancelled(true);
            onClick(event.getClick());
        }
    }

    protected abstract void onClick(ClickType clickType);
}
