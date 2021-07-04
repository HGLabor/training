package de.hglabor.plugins.training.challenges.crafting

import de.hglabor.plugins.training.challenges.SimpleChallenge
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.extensions.broadcast
import net.axay.kspigot.extensions.bukkit.actionBar
import net.axay.kspigot.extensions.bukkit.feedSaturate
import net.axay.kspigot.extensions.bukkit.title
import net.axay.kspigot.runnables.KSpigotRunnable
import net.axay.kspigot.runnables.task
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerDropItemEvent
import java.util.*
import kotlin.collections.HashMap

private const val TASK_PERIOD = 5 // seconds

class Crafting(name: String) : SimpleChallenge(name, KColors.BROWN) {

    private enum class State { PREPARATION, INGAME }

    private var state: State? = null
    private var task: KSpigotRunnable? = null
    private val queue = ArrayList<UUID>()
    private val finished = HashMap<UUID, Long>()
    private var currentItem = Material.GRINDSTONE
    private var startTime = System.currentTimeMillis()
    private val time get() = System.currentTimeMillis()

    /*
     * State is set to null at the end of a cycle -> is null at beginning
     * At start a new item is picked
     * -> announce it to the players
     * -> when a player joins mid phase it will get announced to him then
     *
     * counter is 1 at first round
      */
    override fun start() {
        var counter = 0
        task = task(period = TASK_PERIOD*20L) {
            if (it.isCancelled) return@task
            counter++
            when (state) {
                null -> {
                    // 1
                    state = State.PREPARATION
                    // New round -> Remove waiting players from queue
                    queue.clear()

                    // New round -> Remove finished players
                    finished.clear()

                    // New item to craft
                    currentItem = CraftingUtils.randomItem()
                    // Announce item
                    players.forEach { uuid -> Bukkit.getPlayer(uuid)?.title("Item: ${KColors.AQUA}${currentItem.name}") }
                }
                State.PREPARATION -> {
                    // 2
                    state = State.INGAME
                    players.forEach { uuid -> Bukkit.getPlayer(uuid)?.title("${KColors.GREEN}GOOD LUCK!") }
                    players.forEach { uuid -> Bukkit.getPlayer(uuid)?.sendMessage("start crafting") }
                    startTime = time
                }
                State.INGAME -> {
                    if(counter.rem(4) == 0) {
                        // 4 (crafting time = double preparation time)
                        players.forEach { uuid ->
                            val player = Bukkit.getPlayer(uuid) ?: return@forEach
                            if (!finished.containsKey(player.uniqueId)) onFailure(player)
                        }
                        state = null // New cycle
                    }
                }
            }
        }
    }

    override fun stop() {
        task?.cancel()
        state = null
    }

    @EventHandler
    fun openInv(event: InventoryOpenEvent) = with(event) {
        broadcast("inv")
        if (player !is Player) return@with
        if (!isInChallenge(player as Player)) return@with
        if (queue.contains(player.uniqueId) || state != State.INGAME) event.isCancelled = true
    }
 
    @EventHandler
    fun crafting(event: CraftItemEvent) = with(event) {
        if (recipe.result.type != this@Crafting.currentItem || whoClicked !is Player) return@with
        val timeNeeded = time - startTime
        val message = "${KColors.ORANGE}${finished.size+1}. ${KColors.AQUA}${whoClicked.name} ${KColors.GRAY}| ${KColors.RED}Time: $String${String.format("%.3f", timeNeeded)}s"
        players.forEach { Bukkit.getPlayer(it)?.sendMessage(message) }
        finished[whoClicked.uniqueId] = timeNeeded
        (whoClicked as Player).actionBar("§6Time needed: §e$timeNeeded")
    }

    @EventHandler
    fun foodLevelChange(event: FoodLevelChangeEvent) = with(event) {
        if (entity !is Player) return
        (entity as Player).feedSaturate()
        event.isCancelled = true
    }

    @EventHandler
    fun dropItem(event: PlayerDropItemEvent) {
        if (isInChallenge(event.player)) event.isCancelled = true
    }

    override fun onEnter(player: Player) {
        super.onEnter(player)
        player.inventory.clear()
        if (state == State.PREPARATION) player.title("Prepare to craft: ${currentItem.name}")
        else if (state == State.INGAME) {
            player.title("${KColors.YELLOW}Entered Queue")
            queue.add(player.uniqueId)
        }
    }

    override fun onLeave(player: Player) {
        super.onLeave(player)
        queue.remove(player.uniqueId)
    }
}