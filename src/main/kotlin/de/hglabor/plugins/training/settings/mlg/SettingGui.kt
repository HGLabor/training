package de.hglabor.plugins.training.settings.mlg

import net.axay.kspigot.chat.KColors
import net.axay.kspigot.gui.*
import net.axay.kspigot.gui.elements.GUIRectSpaceCompound
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
        title = "${KColors.BLACK}SETTINGS"

        val iconGenerator: (Setting) -> ItemStack = {
            it.setDefaultEnabledIfMissing(uuid)
            val enabled = it.getEnabled(uuid)
            val enabledString = (if (enabled) "${KColors.GREEN}Enabled" else "${KColors.RED}Disabled") + KColors.WHITE.toString()
            val lore = enabledString.toLoreList(KColors.BOLD).toMutableList().apply {
                this += " "
                this += "Click on this item to toggle.".toLoreList(KColors.LIGHTSLATEGRAY, KColors.ITALIC)
            }
            if (it.icon == null) itemStack(Material.PLAYER_HEAD) {
                meta<SkullMeta> {
                    name = (if (it.type == Setting.Type.DAMAGER) KColors.BROWN.toString() else KColors.AQUA.toString()) + it.settingName
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
        }


        page(1) {

            transitionFrom = PageChangeEffect.SWIPE_HORIZONTALLY
            transitionTo = PageChangeEffect.SWIPE_HORIZONTALLY

            placeholder(Slots.All, ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE))

            lateinit var mlgSettingsCompound: GUIRectSpaceCompound<*, Setting>
            mlgSettingsCompound = createRectCompound(
                // Position the "buttons" from slot 2|2 to slot 2|7
                Slots.RowTwoSlotTwo,
                Slots.RowTwoSlotSeven,
                iconGenerator = iconGenerator,

                onClick = { clickEvent, element ->
                    clickEvent.bukkitEvent.isCancelled = true
                    element.toggle(uuid)
                    mlgSettingsCompound.setContent(Setting.typeValues(Setting.Type.MLG))
                }
            )

            mlgSettingsCompound.addContent(Setting.typeValues(Setting.Type.MLG))

            nextPage(Slots.RowTwoSlotNine, itemStack(Material.RED_CONCRETE) {
                meta { name = "${KColors.BROWN}Damager Settings" }
            })
        }

        page(2) {
            transitionFrom = PageChangeEffect.SWIPE_HORIZONTALLY
            transitionTo = PageChangeEffect.SWIPE_HORIZONTALLY

            placeholder(Slots.All, ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE))

            previousPage(Slots.RowTwoSlotOne, itemStack(Material.WATER_BUCKET) {
                meta { name = "${KColors.AQUA}Mlg Settings"}
            })

            lateinit var damagerSettingsCompound: GUIRectSpaceCompound<*, Setting>
            damagerSettingsCompound = createRectCompound(
                // Position the "buttons" from slot 2|3 to slot 2|8
                Slots.RowTwoSlotThree,
                Slots.RowTwoSlotEight,
                iconGenerator = iconGenerator,

                onClick = { clickEvent, element ->
                    clickEvent.bukkitEvent.isCancelled = true
                    element.toggle(uuid)
                    damagerSettingsCompound.setContent(Setting.typeValues(Setting.Type.DAMAGER))
                }
            )

            damagerSettingsCompound.addContent(Setting.typeValues(Setting.Type.DAMAGER))
        }
    }
}