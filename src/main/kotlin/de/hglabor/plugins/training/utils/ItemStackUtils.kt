package de.hglabor.plugins.training.utils

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

fun MutableList<ItemStack>.add(material: Material, amount: Int = 1) = add(ItemStack(material, amount))
fun MutableList<ItemStack>.addAll(vararg items: Any) {
    items.forEach {
        if (it is Material) add(it)
        else if (it is ItemStack) add(it)
    }
}

fun Material.stack(amount: Int = 1): ItemStack = ItemStack(this, amount)
fun List<Material>.stack(): List<ItemStack> {
    val itemStacks = ArrayList<ItemStack>()
    forEach { itemStacks.add(it.stack()) }
    return itemStacks
}
