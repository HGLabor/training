package de.hglabor.plugins.training.settings.mlg

import net.axay.kspigot.chat.KColors
import net.axay.kspigot.gui.GUIType
import net.axay.kspigot.gui.Slots
import net.axay.kspigot.gui.elements.GUIRectSpaceCompound
import net.axay.kspigot.gui.kSpigotGUI
import net.axay.kspigot.gui.openGUI
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import net.axay.kspigot.items.toLoreList
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.*

object SettingGui {
    fun open(player: Player): InventoryView? = player.openGUI(mlgSettingsGui(player.uniqueId))

    private fun mlgSettingsGui(uuid: UUID) = kSpigotGUI(GUIType.THREE_BY_NINE) {
        title = "${KColors.BLACK}MLG SETTINGS"

        page(1) {
            placeholder(Slots.Border, ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE))

            lateinit var warpsCompound: GUIRectSpaceCompound<*, Setting>
            warpsCompound = createRectCompound(
                // Position the "buttons" from slot 2|2 to slot 2|8
                Slots.RowTwoSlotTwo,
                Slots.RowTwoSlotSeven,
                iconGenerator = {
                    it.setDefaultEnabledIfMissing(uuid)
                    val enabled = it.getEnabled(uuid)
                    val enabledString = (if (enabled) "${KColors.GREEN}Enabled" else "${KColors.RED}Disabled") + KColors.WHITE.toString()
                    val lore = enabledString.toLoreList(KColors.BOLD).toMutableList().apply {
                        this += " "
                        this += "Click on this item to toggle.".toLoreList(KColors.LIGHTSLATEGRAY, KColors.ITALIC)
                    }
                    if (it.icon == null) itemStack(Material.PLAYER_HEAD) {
                        meta<SkullMeta> {
                            name = "${KColors.AQUA}${it.settingName}"
                            this.lore = lore
                            owner = it.headOwner
                        }
                    }
                    else itemStack(it.icon) {
                        meta {
                            name = "${KColors.AQUA}${it.settingName}"
                            this.lore = lore
                        }
                    }
                },

                onClick = { clickEvent, element ->
                    clickEvent.bukkitEvent.isCancelled = true
                    element.toggle(uuid)
                    warpsCompound.setContent(Setting.values().asIterable())
                }
            )
            warpsCompound.addContent(Setting.values().asIterable())
        }
    }
}