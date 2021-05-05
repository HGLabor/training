package de.hglabor.plugins.training.gui.button;

import de.hglabor.plugins.training.Training;
import de.hglabor.plugins.training.gui.AbstractGui;
import de.hglabor.utils.noriskutils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public abstract class AbstractButton implements Listener {
    private final UUID uuid;
    protected String title;
    protected String description = "";
    protected Material material;
    public ItemStack itemStack;
    protected AbstractGui context;
    protected int slot;
    private boolean callled;

    public AbstractButton(UUID uuid, AbstractGui context, int slot, String title, Material material) {
        this.title = title;
        this.material = material;
        this.context = context;
        this.slot = slot;
        this.uuid = uuid;
        updateItemStack();
        Training.getInstance().registerAllEventListeners(this);
    }

    protected void updateItemStack() {
        itemStack = new ItemBuilder(material).setName(title).setDescription(description).build();
        context.getInventory().setItem(slot, itemStack);
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) {
            return;
        }
        if (!event.getWhoClicked().getUniqueId().equals(this.uuid)) {
            return;
        }
        if (event.getCurrentItem().isSimilar(itemStack)) {
            event.setCancelled(true);
            // Get's called twice somehow LOL so only call onClick() every 2 calls
            if (this.callled) {
                this.callled = false;
                return;
            }
            onClick(event.getClick());
            this.callled = true;
        }
    }

    protected abstract void onClick(ClickType clickType);
}
