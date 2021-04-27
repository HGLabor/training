package de.hglabor.plugins.training.challenges.mlg;

import de.hglabor.plugins.training.user.User;
import de.hglabor.plugins.training.user.UserList;
import de.hglabor.utils.noriskutils.HologramUtils;
import de.hglabor.utils.noriskutils.WorldEditUtils;
import net.minecraft.server.v1_16_R3.EntityPanda;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPanda;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftSheep;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Random;

public class MlgPlatform implements Listener {
    private final Mlg mlg;
    private final int radius;
    private final int yPos;
    private final Material material;
    private Location spawn;
    private ArmorStand heightHologram;
    private MlgPlatform upPlatform, downPlatform;
    private MlgPlatform topPlatform, bottomPlatform;
    private Sheep upEntity, downEntity;
    private LivingEntity topEntity, bottomEntity;
    private Panda leftSupplyPanda, rightSupplyPanda;

    public MlgPlatform(Mlg mlg, Location spawn, int radius, int yPos, Material material) {
        this.mlg = mlg;
        this.spawn = spawn;
        this.radius = radius;
        this.yPos = yPos;
        this.material = material;
    }

    public void create() {
        World world = spawn.getWorld();
        spawn.getChunk().setForceLoaded(true);
        WorldEditUtils.createCylinder(world, spawn, radius, true, 1, material);
        heightHologram = HologramUtils.spawnHologram(spawn.clone().add(0, 4, 4), ChatColor.GOLD + ChatColor.BOLD.toString() + (int) spawn.getY());
        heightHologram.setPersistent(false);
        //Hhaha DRY goes brrrrrrrr
        double zCoordinate = radius - (radius / 3D);
        if (upPlatform != null) {
            Location loc = spawn.clone().add(1, 1, zCoordinate);
            loc.setYaw(180);
            upEntity = createLevitatorSheep(loc, ChatColor.BOLD + ChatColor.GREEN.toString() + "\u25B2", DyeColor.GREEN);
            Location loc2 = spawn.clone().add(1, 3, zCoordinate);
            topEntity = createTopBottomPhantom(loc2, ChatColor.BOLD + ChatColor.GREEN.toString() + "\u25B2");
        }
        if (downPlatform != null) {
            Location loc = spawn.clone().add(-1, 1, zCoordinate);
            loc.setYaw(180);
            downEntity = createLevitatorSheep(loc, ChatColor.BOLD + ChatColor.RED.toString() + "\u25BC", DyeColor.RED);
            Location loc2 = spawn.clone().add(-1, 3, zCoordinate);
            bottomEntity = createTopBottomPhantom(loc2, ChatColor.BOLD + ChatColor.RED.toString() + "\u25BC");

        }
        Location left = spawn.clone().add(radius - (radius / 3D), 1, 0);
        left.setYaw(90);
        leftSupplyPanda = createSupplyPanda(left);
        Location right = spawn.clone().add(-(radius - (radius / 3D)), 1, 0);
        right.setYaw(-90);
        rightSupplyPanda = createSupplyPanda(right);
    }

    private Sheep createLevitatorSheep(Location location, String name, DyeColor color) {
        CraftWorld world = (CraftWorld) location.getWorld();
        Sheep sheep = (Sheep) world.createEntity(location, Sheep.class).getBukkitEntity();
        sheep.setCustomName(name);
        sheep.setCustomNameVisible(true);
        sheep.setAI(false);
        sheep.setSilent(true);
        sheep.setPersistent(false);
        sheep.setInvulnerable(true);
        world.addEntity(((CraftSheep) sheep).getHandle(), CreatureSpawnEvent.SpawnReason.CUSTOM);
        sheep.setColor(color);
        return sheep;
    }

    private LivingEntity createTopBottomPhantom(Location location, String name) {
        CraftWorld world = (CraftWorld) location.getWorld();
        LivingEntity phantom = (LivingEntity) world.createEntity(location, Phantom.class).getBukkitEntity();
        phantom.setCustomName(name);
        phantom.setCustomNameVisible(true);
        phantom.setAI(false);
        phantom.setSilent(true);
        phantom.setPersistent(false);
        phantom.setInvulnerable(true);
        phantom.setRemoveWhenFarAway(false);
        world.addEntity(((CraftLivingEntity) phantom).getHandle(), CreatureSpawnEvent.SpawnReason.CUSTOM);
        return phantom;
    }

    private Panda createSupplyPanda(Location location) {
        CraftWorld world = (CraftWorld) location.getWorld();
        Panda panda = (Panda) world.createEntity(location, Panda.class).getBukkitEntity();
        panda.setAI(false);
        panda.setSilent(true);
        panda.setPersistent(false);
        panda.setInvulnerable(true);
        panda.setCustomName(mlg.getColor().toString() + ChatColor.BOLD + "Items");
        panda.setCustomNameVisible(true);
        panda.setHiddenGene(CraftPanda.fromNms(EntityPanda.Gene.values()[new Random().nextInt(EntityPanda.Gene.values().length)]));
        panda.setMainGene(CraftPanda.fromNms(EntityPanda.Gene.values()[new Random().nextInt(EntityPanda.Gene.values().length)]));
        world.addEntity(((CraftPanda) panda).getHandle(), CreatureSpawnEvent.SpawnReason.CUSTOM);
        return panda;
    }

    public void clear() {
        if (upEntity != null) upEntity.remove();
        if (downEntity != null) downEntity.remove();
        heightHologram.remove();
        leftSupplyPanda.remove();
        rightSupplyPanda.remove();
    }

    public Location getSpawn() {
        return spawn;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn.clone();
        this.spawn.setY(yPos);
    }

    public void setUp(MlgPlatform up) {
        this.upPlatform = up;
    }

    public void setTop(MlgPlatform top) {
        this.topPlatform = top;
    }

    public void setBottom(MlgPlatform bottom) {
        this.bottomPlatform = bottom;
    }

    public void setDown(MlgPlatform down) {
        this.downPlatform = down;
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        Entity rightClicked = event.getRightClicked();
        Player player = event.getPlayer();
        if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
            return;
        }
        if (upEntity != null && rightClicked.getUniqueId().equals(upEntity.getUniqueId())) {
            // Up
            player.teleport(upPlatform.getSpawn().clone().add(0, 1, 0));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 0);
        } else if (topPlatform != null && topEntity != null && rightClicked.getUniqueId().equals(topEntity.getUniqueId())) {
            // Top
            player.teleport(topPlatform.getSpawn().clone().add(0, 1, 0));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 0);
        } else if (downEntity != null && rightClicked.getUniqueId().equals(downEntity.getUniqueId())) {
            // Down
            player.teleport(downPlatform.getSpawn().clone().add(0, 1, 0));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        } else if (bottomPlatform != null && bottomEntity != null && rightClicked.getUniqueId().equals(bottomEntity.getUniqueId())) {
            // Bottom
            player.teleport(bottomPlatform.getSpawn().clone().add(0, 1, 0));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 0);
        } else if (rightClicked.getUniqueId().equals(rightSupplyPanda.getUniqueId()) || rightClicked.getUniqueId().equals(leftSupplyPanda.getUniqueId())) {
            User user = UserList.INSTANCE.getUser(player);
            if (!user.hasCooldown(getClass())) {
                mlg.setMlgReady(player);
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_LEATHER, 1, 1);
                player.sendMessage(ChatColor.AQUA + "You received " + mlg.getName() + " mlg equipment");
                user.addCooldown(getClass(), System.currentTimeMillis() + 1500L);
            } else {
                player.sendMessage(ChatColor.RED + "You are on cooldown.");
            }
        }
    }
}
