package de.hglabor.plugins.training.challenges.damager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class CrapDamager extends Damager {
    private final List<Material> crapMaterials;
    private final Map<UUID, Long> timeStamps;
    private final Random random;
    private final int crapTick;

    public CrapDamager(String name, ChatColor color) {
        super(name, color);
        this.crapTick = 1;
        this.random = new Random();
        this.timeStamps = new HashMap<>();
        this.crapMaterials = Arrays.asList(
                Material.WOODEN_PICKAXE, Material.DIRT, Material.WHEAT_SEEDS,
                Material.COBBLESTONE, Material.OAK_PLANKS, Material.WOODEN_SWORD,
                Material.STONE_SWORD, Material.DIORITE, Material.OAK_SAPLING,
                Material.OAK_BUTTON, Material.CRAFTING_TABLE, Material.WOODEN_HOE,
                Material.ANVIL, Material.ARROW, Material.SAND, Material.COAL,
                Material.STICK, Material.POPPY, Material.SUNFLOWER
        );
    }

    @Override
    public void stop() {
        super.stop();
        this.timeStamps.clear();
    }

    @Override
    public void onEnter(Player player) {
        super.onEnter(player);
        timeStamps.put(player.getUniqueId(), System.currentTimeMillis() + (random.nextInt(crapTick) + 1) * 1000L);
    }

    @Override
    public void onComplete(Player player) {
        super.onComplete(player);
        timeStamps.remove(player.getUniqueId());
    }

    @Override
    public void onFailure(Player player) {
        super.onFailure(player);
        timeStamps.remove(player.getUniqueId());
    }

    @Override
    public Runnable getDamageRunnable() {
        return () -> {
            if (task.isCancelled()) {
                return;
            }
            for (UUID uuid : players.keySet()) {
                if (players.get(uuid)) {
                    continue;
                }

                Player player = (Player) Bukkit.getEntity(uuid);

                if (player != null) {
                    if (System.currentTimeMillis() > timeStamps.get(uuid)) {
                        timeStamps.put(uuid, System.currentTimeMillis() + (random.nextInt(crapTick) + 1) * 1000L);
                        player.getWorld().dropItem(player.getEyeLocation(), getRandomItemStack(), item -> item.setOwner(player.getUniqueId()));
                    }
                    player.damage(damage);
                }
            }
        };
    }

    private ItemStack getRandomItemStack() {
        Material material = crapMaterials.get(random.nextInt(crapMaterials.size()));
        return new ItemStack(material, random.nextInt(material.getMaxStackSize()) + 1);
    }
}
