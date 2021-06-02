package de.hglabor.plugins.training.recipes

import de.hglabor.plugins.training.main.PLUGIN
import net.axay.kspigot.event.listen
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapelessRecipe

private val NOTHING = ItemStack(Material.AIR)

fun registerCustomRecipes() {
    shapeless("mushroom_stew_with_cocoa_beans", Material.MUSHROOM_STEW, Material.COCOA_BEANS)
    remove(Material.BROWN_DYE)
}

private fun shapeless(namespacedKey: String, result: Material, vararg ingredients: Material) {
    val key = NamespacedKey(PLUGIN, namespacedKey)
    val recipe = ShapelessRecipe(key, ItemStack(result))
    recipe.ingredients(Material.BOWL, Material.COCOA_BEANS)
    Bukkit.addRecipe(recipe)
}

private fun remove(material: Material) {
    listen<PrepareItemCraftEvent> {
        if (it.recipe?.result?.type == material) it.inventory.result = NOTHING
    }
}

private fun ShapelessRecipe.ingredients(vararg ingredients: Material) {
    ingredients.forEach { addIngredient(it) }
}