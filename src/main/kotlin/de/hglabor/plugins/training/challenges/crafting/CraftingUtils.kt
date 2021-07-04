package de.hglabor.plugins.training.challenges.crafting

import de.hglabor.plugins.training.utils.addAll
import de.hglabor.plugins.training.utils.stack
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.*
import java.util.*

object CraftingUtils {
    private val random = Random()
    private val WOOD_TYPES = arrayOf("jungle", "spruce", "birch", "acacia",
        "dark_oak", "crimson", "warped", "oak")
    private val LOG_ITEMS: Array<String> = arrayOf("_planks", "stick", "_planks", *WOOD_TYPES.let { array ->
        val list = ArrayList<String>()
        array.forEach { list += it + "_planks" }
        return@let list.toTypedArray()
    })
    private val EXCLUDED_ITEMS: Array<String> = arrayOf(
        *WOOD_TYPES.filter { it != "oak" }.toTypedArray(), "carpet", "pane", "terracotta", "glass",
        "banner", "bed", "wall", "slab", "stairs", "blackstone", "quartz", "sandstone",
        "polished", "chiseled", "pillar"
    )
    private val SPECIAL_ITEMS = arrayOf(Material.WARPED_FUNGUS_ON_A_STICK, Material.RED_BED)
    private val TO_CRAFT_MATERIALS = toCraftMaterials
    private fun getRecipes(itemStack: ItemStack): List<Recipe> = Bukkit.getRecipesFor(itemStack)
    private fun Material.recipes() = getRecipes(ItemStack(this))

    private fun isShapedRecipe(material: Material): Boolean {
        var isShapedRecipe = false
        for (recipe in material.recipes()) {
            isShapedRecipe = recipe is ShapedRecipe
        }
        return isShapedRecipe
    }

    private fun isExcludedMaterial(material: Material): Boolean {
        return EXCLUDED_ITEMS.any { material.name.toLowerCase().contains(it) }
    }

    private val toCraftMaterials: List<Material>
        get() {
            val materials: MutableList<Material> = Material.values().filter { !isExcludedMaterial(it) }.filter { isShapedRecipe(it) }.toMutableList()
            return materials.apply { addAll(SPECIAL_ITEMS)}
        }

    private fun getOriginMaterial(itemStack: ItemStack): List<ItemStack>? {
        for (logItem in LOG_ITEMS) if (itemStack.type.name.toLowerCase().endsWith(logItem)) return listOf(Material.OAK_LOG.stack())

        val itemStacks: MutableList<ItemStack> = ArrayList()
        with(itemStacks) {
            when (itemStack.type) {
                Material.FISHING_ROD -> addAll(Material.OAK_LOG, Material.STRING.stack(2))
                Material.RED_BED -> addAll(Material.OAK_LOG, Material.RED_WOOL.stack(3))
                Material.ARMOR_STAND -> addAll(Material.OAK_LOG.stack(3), Material.SMOOTH_STONE_SLAB)
                Material.CHEST_MINECART -> addAll(Material.OAK_LOG.stack(3), Material.IRON_BARS.stack(5))
                Material.TNT_MINECART -> addAll(Material.SAND.stack(4), Material.GUNPOWDER.stack(5), Material.IRON_BARS.stack(5))
                else -> {}
            }
        }
        return itemStacks.ifEmpty { null }
    }

    fun randomItem() = TO_CRAFT_MATERIALS[random.nextInt(TO_CRAFT_MATERIALS.size)]

    fun getCraftingItems(material: Material?): List<ItemStack> {
        val recipes = getRecipes(
            ItemStack(
                material!!
            )
        )
        val itemStacksForCrafting: MutableList<ItemStack> = ArrayList()
        if (recipes[0] is ShapedRecipe) {
            for (value in (recipes[0] as ShapedRecipe).ingredientMap.values) {
                value ?: continue
                itemStacksForCrafting.addAll(getOriginMaterial(value) ?: setOf(value))
            }
        }
        return itemStacksForCrafting
    }
}