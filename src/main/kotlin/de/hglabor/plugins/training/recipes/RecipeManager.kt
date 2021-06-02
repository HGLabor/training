package de.hglabor.plugins.training.recipes

import de.hglabor.plugins.training.main.PLUGIN
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapelessRecipe

fun registerCustomRecipes() {
    shapeless("mushroom_stew_with_cocoa_beans", Material.MUSHROOM_STEW, Material.COCOA_BEANS)
}

private fun shapeless(namespacedKey: String, result: Material, vararg ingredients: Material) {
    val key = NamespacedKey(PLUGIN, namespacedKey)
    val recipe = ShapelessRecipe(key, ItemStack(result))
    recipe.ingredients(Material.BOWL, Material.COCOA_BEANS)
    Bukkit.addRecipe(recipe)
}

private fun ShapelessRecipe.ingredients(vararg ingredients: Material) {
    ingredients.forEach { addIngredient(it) }
}