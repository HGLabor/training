package de.hglabor.plugins.training.challenges.mlg.mlgs;

import de.hglabor.plugins.training.challenges.mlg.Mlg;
import de.hglabor.plugins.training.main.TrainingKt;
import de.hglabor.utils.noriskutils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.spigotmc.event.entity.EntityMountEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StriderMlg extends Mlg {
    private final List<ItemStack> mlgItems = new ArrayList<>();
    private final List<Strider> striders;
    private final int striderAmount;

    public StriderMlg(String name, ChatColor color, Class<? extends Entity> type) {
        super(name, color, type, Material.QUARTZ_BLOCK, new Material[]{Material.GOLD_BLOCK, Material.LAVA});
        this.striders = new ArrayList<>();
        this.striderAmount = 125;
        this.addMlgMaterial(Material.SADDLE);
        this.mlgItems.add(new ItemBuilder(Material.FISHING_ROD).setUnbreakable(true).build());
    }

    private void addMlgMaterial(Material material) {
        this.mlgItems.add(new ItemBuilder(material).setName(ChatColor.AQUA + this.getName() + " MLG").build()); // e.g. Block Mlg - COBWEB
    }

    @Override
    public void start() {
        super.start();
        Random random = new Random();
        for (int i = 0; i < striderAmount; i++) {
            int x = random.nextInt(getBorderRadius());
            int z = random.nextInt(getBorderRadius());
            Strider strider = (Strider) spawn.getWorld().spawnEntity(spawn.clone().add(random.nextBoolean() ? x : x * -1, 1, random.nextBoolean() ? z : z * -1), EntityType.STRIDER);
            strider.setPersistent(false);
            strider.setAdult();
            strider.setMetadata(this.getName(), new FixedMetadataValue(TrainingKt.getPLUGIN(), ""));
            strider.setInvulnerable(true);
            AttributeInstance health = strider.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if (health != null) {
                health.setBaseValue(1000);
                strider.setHealth(health.getBaseValue());
            }
            strider.setSaddle(false);
            striders.add(strider);
        }
    }

    @Override
    public void stop() {
        super.stop();
        striders.forEach(Entity::remove);
        striders.clear();
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        if (!isInChallenge(player)) {
            return;
        }
        if (!(event.getCaught() instanceof Strider) && event.getState().equals(PlayerFishEvent.State.CAUGHT_ENTITY)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getHitEntity() == null) {
            return;
        }
        Projectile projectile = event.getEntity();
        Entity hitEntity = event.getHitEntity();
        if (!(projectile.getShooter() instanceof Player) || (hitEntity instanceof Strider)) {
            return;
        }
        if (isInChallenge((Player) projectile.getShooter()) && (!(hitEntity instanceof Player) || isInChallenge((Player) hitEntity))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClickStrider(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        if (!isInChallenge(player)) {
            event.setCancelled(true);
            return;
        }
        if (!(event.getRightClicked() instanceof Strider)) return;
        // Remove saddle after 1 second (20 ticks)
        Bukkit.getScheduler().runTaskLater(TrainingKt.getPLUGIN(), () -> ((Strider) event.getRightClicked()).setSaddle(false), 20L);
    }

    @EventHandler
    public void onMountStrider(EntityMountEvent event) {
        if (!(event.getMount() instanceof Strider)) {
            return;
        }
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = ((Player) event.getEntity());
        if (!isInChallenge(player)) {
            event.setCancelled(true);
            return;
        }

        handleMlg(player, 0L);
    }

    @EventHandler
    public void onLavaDamage(EntityDamageByBlockEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = ((Player) event.getEntity());
        if (!isInChallenge(player)) {
            event.setCancelled(true);
            return;
        }
        if (!(event.getCause().equals(EntityDamageEvent.DamageCause.LAVA))) {
            event.setCancelled(true);
            return;
        }
        if (event.getCause().equals(EntityDamageEvent.DamageCause.LAVA)) {
            // Player got lava damage
            onFailure(player);
            player.setFireTicks(0);
        }
    }

    @EventHandler
    public void onStriderDamage(EntityDamageEvent event) {
        if (event.getEntity().hasMetadata(this.getName()) && event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
            event.setCancelled(true);
        }
    }

    @Override
    public List<ItemStack> getMlgItems() {
        return mlgItems;
    }

    @Override
    protected void inventorySetup(Player player) {
        player.getInventory().setItem(1, mlgItems.get(1));
        player.getInventory().setItem(4, mlgItems.get(0));
    }
}
