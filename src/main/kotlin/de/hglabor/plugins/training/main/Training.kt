package de.hglabor.plugins.training.main

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import de.hglabor.plugins.training.challenges.ChallengeManager
import de.hglabor.plugins.training.challenges.damager.Damager
import de.hglabor.plugins.training.challenges.damager.damagers.CrapDamager
import de.hglabor.plugins.training.challenges.damager.damagers.ImpossibleDamager
import de.hglabor.plugins.training.challenges.damager.damagers.InconsistencyDamager
import de.hglabor.plugins.training.challenges.listener.ChallengeCuboidListener
import de.hglabor.plugins.training.challenges.mlg.mlgs.*
import de.hglabor.plugins.training.command.ChallengeCommand
import de.hglabor.plugins.training.command.DamagerCommand
import de.hglabor.plugins.training.command.MlgCommand
import de.hglabor.plugins.training.command.SettingsCommand
import de.hglabor.plugins.training.data.DataManager
import de.hglabor.plugins.training.packets.PacketReceiver
import de.hglabor.plugins.training.recipes.registerCustomRecipes
import de.hglabor.plugins.training.settings.mlg.SettingGui
import de.hglabor.plugins.training.user.UserList
import de.hglabor.plugins.training.warp.WarpItems
import de.hglabor.plugins.training.warp.WarpSelector
import de.hglabor.plugins.training.warp.worlds.DamagerWorld
import de.hglabor.plugins.training.warp.worlds.MlgWorld
import dev.jorel.commandapi.CommandAPI
import net.axay.kspigot.event.listen
import net.axay.kspigot.main.KSpigot
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.WorldCreator
import org.bukkit.entity.*
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class Training : KSpigot() {
    companion object {
        lateinit var INSTANCE: Training; private set
    }

    lateinit var protocolManager: ProtocolManager

    override fun load() {
        INSTANCE = this

        CommandAPI.onLoad(true)
    }

    override fun startup() {
        Bukkit.createWorld(WorldCreator("mlg"))

        DamagerWorld.INSTANCE.init(Bukkit.getWorld("world"))
        MlgWorld.INSTANCE.init(Bukkit.getWorld("mlg"))
        registerAllEventListeners(UserList.INSTANCE, ChallengeCuboidListener(), WarpSelector())

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord")

        ChallengeManager.INSTANCE.register(Damager("Noob", ChatColor.AQUA))
        ChallengeManager.INSTANCE.register(Damager("Easy", ChatColor.GREEN))
        ChallengeManager.INSTANCE.register(Damager("Medium", ChatColor.YELLOW))
        ChallengeManager.INSTANCE.register(Damager("Hard", ChatColor.RED))
        ChallengeManager.INSTANCE.register(CrapDamager("Crap", ChatColor.AQUA))
        ChallengeManager.INSTANCE.register(ImpossibleDamager("Impossible", ChatColor.DARK_GRAY))
        ChallengeManager.INSTANCE.register(InconsistencyDamager("Inconsistency", ChatColor.LIGHT_PURPLE))

        val blockMlg = BlockMlg("Block", ChatColor.WHITE, Slime::class.java).withPlatforms(Material.POLISHED_DIORITE, 10,10, 20, 50, 100, 150, 200, 250)
        val horseMlg = HorseMlg("Horse", ChatColor.GOLD, Horse::class.java).withPlatforms(Material.DARK_OAK_PLANKS, 10,25, 50, 100, 150, 200, 250)
        val boatMlg = BoatMlg("Boat", ChatColor.YELLOW, Boat::class.java).withPlatforms(Material.OAK_PLANKS, 10, 25, 50, 100, 150, 200, 250)
        val minecartMlg = MinecartMlg("Minecart", ChatColor.GRAY, Minecart::class.java).withPlatforms(Material.GRAY_GLAZED_TERRACOTTA, 10,10, 15, 20, 25, 50, 100, 150, 200, 250)
        val striderMlg = StriderMlg("Strider", ChatColor.LIGHT_PURPLE, Strider::class.java).withPlatforms(Material.CRIMSON_NYLIUM, 10, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 90, 100, 200, 250)
        val ladderMlg = LadderMlg("Ladder", ChatColor.GOLD, WanderingTrader::class.java).withPlatforms(Material.STRIPPED_OAK_WOOD, 10, 21, 50, 100, 150, 200, 250)
        val potionMlg = PotionMlg("Potion", ChatColor.GREEN, Witch::class.java).withPlatforms(Material.SMOOTH_SANDSTONE, 10, 20, 50, 100, 150, 200, 250)
        ChallengeManager.INSTANCE.registerAll(blockMlg, horseMlg, boatMlg, striderMlg, minecartMlg, ladderMlg, potionMlg)

        CommandAPI.onEnable(this)
        DamagerCommand()
        ChallengeCommand()
        MlgCommand()
        SettingsCommand()

        DataManager.load()

        protocolManager = ProtocolLibrary.getProtocolManager()
        PacketReceiver.listen()

        listen<InventoryClickEvent> { event ->
            if (event.currentItem == null) return@listen

            val item: ItemStack = event.currentItem!!
            if (!WarpItems.isWarpItem(item)) return@listen

            event.isCancelled = true
            val player = event.whoClicked as Player

            if (item.isSimilar(WarpItems.SETTINGS)) SettingGui.open(player)
        }

        registerCustomRecipes()
    }

    fun registerAllEventListeners(vararg eventListeners: Listener) {
        eventListeners.forEach { Bukkit.getPluginManager().registerEvents(it, this) }
    }

    override fun shutdown() {
        ChallengeManager.INSTANCE.challenges.forEach { it.stop() }

        DataManager.save()
    }
}

val PLUGIN by lazy { Training.INSTANCE }