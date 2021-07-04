package de.hglabor.plugins.training.challenges

import de.hglabor.plugins.training.main.PLUGIN
import de.hglabor.plugins.training.region.Area
import de.hglabor.plugins.training.region.Cuboid
import net.axay.kspigot.chat.KColors
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

private val SPAWN = Location(Bukkit.getWorld("world"), 0.0, 0.0, 0.0)

abstract class SimpleChallenge(private val mName: String, private val mColor: ChatColor) : Challenge {
    private lateinit var cuboid: Cuboid
    protected val players = ArrayList<UUID>()

    override fun getColor(): ChatColor = mColor
    override fun getName(): String = mName
    override fun getArea(): Area = cuboid
    open val completedMessage: String? = null
    open val failureMessage: String? = null
    open val messageName = name

    override fun initConfig() {
        PLUGIN.config.addDefault("$name.location.first", SPAWN)
        PLUGIN.config.addDefault("$name.location.second", SPAWN)
        PLUGIN.saveConfig()
    }

    override fun loadFromConfig() {
        PLUGIN.reloadConfig()
        val config = PLUGIN.config
        cuboid = Cuboid(config.getLocation("$name.location.first") ?: SPAWN, config.getLocation("$name.location.second") ?: SPAWN)
    }

    override fun saveToConfig() {
        PLUGIN.config.set("$name.location.first", cuboid.first)
        PLUGIN.config.set("$name.location.second", cuboid.second)
        PLUGIN.saveConfig()
    }

    override fun start() {}
    override fun stop() {}
    override fun isInChallenge(player: Player): Boolean = false

    override fun onComplete(player: Player) {
        player.sendMessage(completedMessage ?: "${KColors.GREEN}You completed $messageName")
        player.closeInventory()
    }

    override fun onFailure(player: Player) {
        player.sendMessage(failureMessage ?: "${KColors.RED}You failed $messageName")
        player.closeInventory()
    }

    override fun onEnter(player: Player) {
        player.sendMessage("You entered $messageName")
        players.add(player.uniqueId)
    }

    override fun onLeave(player: Player) {
        player.sendMessage("You left $messageName")
        players.remove(player.uniqueId)
    }
}