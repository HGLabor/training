package de.hglabor.plugins.training.gui;

import de.hglabor.plugins.training.Training;
import de.hglabor.plugins.training.gui.button.StateButton;
import de.hglabor.utils.noriskutils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.stream.IntStream;

import static de.hglabor.plugins.training.settings.MlgSettings.JumpSneakStates;

public class MlgSettingsGui implements Listener {
    private static final String TITLE = "Mlg Settings";
    private static final ItemStack PLACE_HOLDER = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setName(ChatColor.BOLD + "").build();
    public StateButton jumpSneakElevator;
    private final Player player;
    private Inventory gui;
    private Runnable onClose;

    public MlgSettingsGui(Player player) {
        this.player = player;
    }

    public void open(Runnable onClose) {
        player.sendMessage("Opening settings gui");
        gui = Bukkit.createInventory(player, 9 * 3, TITLE);
        IntStream.range(0, gui.getSize()).forEach(i -> gui.setItem(i, PLACE_HOLDER));
        jumpSneakElevator = new StateButton(gui, 10, ChatColor.BOLD + "" + ChatColor.BLUE + "Jump/Sneak Elevator", Material.MAGENTA_GLAZED_TERRACOTTA, new String[] { JumpSneakStates.ALWAYS_ACTIVE, JumpSneakStates.ONLY_GLASS, JumpSneakStates.DISABLED });

        Training.getInstance().registerAllEventListeners(this, jumpSneakElevator);
        player.openInventory(gui);
        this.onClose = onClose;
    }

    @EventHandler
    public void onPlayerCloseInventory(InventoryCloseEvent event) {
        if (event.getInventory().equals(gui)) {
            onClose.run();
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Cancel clicks on placeholder
        if (event.getCurrentItem() != null && event.getCurrentItem().isSimilar(PLACE_HOLDER)) {
            event.setCancelled(true);
        }
    }
}
