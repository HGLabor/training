package de.hglabor.plugins.training.challenges.damager;

import de.hglabor.plugins.training.Training;
import de.hglabor.plugins.training.challenges.Challenge;
import de.hglabor.plugins.training.region.Cuboid;
import de.hglabor.plugins.training.util.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Damager implements Challenge {
    protected Cuboid cuboid;
    protected double damage;
    protected int soupsToEat;
    protected long tickSpeed;
    protected Set<UUID> players;
    private BukkitTask task;

    public Damager() {
        this.players = new HashSet<>();
        this.cuboid = new Cuboid(LocationUtils.ZERO, LocationUtils.ZERO);
        this.cuboid.setOnEnterConsumer(player -> {
            players.add(player.getUniqueId());
            PlayerInventory inventory = player.getInventory();
            inventory.addItem(new ItemStack(Material.STONE_SWORD));
        });
    }

    public void addPlayer(Player player) {
        players.add(player.getUniqueId());
    }

    @Override
    public Cuboid getArea() {
        return cuboid;
    }

    @Override
    public void start() {
        task = Bukkit.getScheduler().runTaskTimer(Training.getInstance(), () -> {
            for (UUID uuid : players) {
                Player player = (Player) Bukkit.getEntity(uuid);
                if (player != null) {
                    player.damage(damage);
                }
            }
        }, 0, tickSpeed);
    }

    @Override
    public void stop() {
        task.cancel();
    }
}
