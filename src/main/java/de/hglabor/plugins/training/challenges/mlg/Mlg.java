package de.hglabor.plugins.training.challenges.mlg;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import de.hglabor.plugins.training.challenges.Challenge;
import de.hglabor.plugins.training.challenges.mlg.scoreboard.MlgPlayer;
import de.hglabor.plugins.training.challenges.mlg.scoreboard.MlgPlayerList;
import de.hglabor.plugins.training.challenges.mlg.scoreboard.MlgScoreboard;
import de.hglabor.plugins.training.challenges.mlg.streaks.StreakPlayer;
import de.hglabor.plugins.training.challenges.mlg.streaks.StreakPlayers;
import de.hglabor.plugins.training.events.SettingChangedEvent;
import de.hglabor.plugins.training.main.TrainingKt;
import de.hglabor.plugins.training.packets.PacketSender;
import de.hglabor.plugins.training.region.Area;
import de.hglabor.plugins.training.region.Cuboid;
import de.hglabor.plugins.training.user.User;
import de.hglabor.plugins.training.user.UserList;
import de.hglabor.plugins.training.util.LocationUtils;
import de.hglabor.plugins.training.warp.WarpItems;
import de.hglabor.plugins.training.warp.worlds.MlgWorld;
import de.hglabor.utils.noriskutils.SoundUtils;
import de.hglabor.utils.noriskutils.WorldEditUtils;
import org.bukkit.Bukkit;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.*;


class MlgInfo {
    private boolean hasDied;
    private boolean hasDoneAction;

    public boolean hasDied() {
        return hasDied;
    }

    public boolean hasDoneAction() {
        return hasDoneAction;
    }

    public void setHasDied(boolean hasDied) {
        this.hasDied = hasDied;
    }

    public void setHasDoneAction(boolean hasDoneAction) {
        this.hasDoneAction = hasDoneAction;
    }
}

public abstract class Mlg implements Challenge {
    protected final String name;
    protected final ChatColor color;
    protected final Entity warpEntity;
    protected final Material borderMaterial, topMaterial;
    protected final Material bottomMaterial;
    protected final Material[] bottomMaterials;
    protected Cuboid cuboid;
    protected Location spawn;
    protected Material platformMaterial;
    protected int platformRadius;
    protected List<MlgPlatform> platforms;
    protected boolean randomizedBottomMaterials = false;
    protected Double[] bottomMaterialPercentages = null;

    public Mlg(String name, ChatColor color, Class<? extends Entity> type, Material borderMaterial, Material[] bottomMaterials) {
        this.name = name;
        this.color = color;
        this.borderMaterial = borderMaterial;
        this.bottomMaterials = bottomMaterials;
        this.bottomMaterial = bottomMaterials[0];
        this.topMaterial = Material.BARRIER;
        this.spawn = LocationUtils.ZERO_MLG;
        this.platforms = new ArrayList<>();
        this.cuboid = new Cuboid(LocationUtils.ZERO_MLG, LocationUtils.ZERO_MLG);
        this.warpEntity = ((CraftWorld) MlgWorld.INSTANCE.getWorld()).createEntity(LocationUtils.MLG_SPAWN, type).getBukkitEntity();
        this.warpEntity.setInvulnerable(true);
        this.warpEntity.setPersistent(false);
        if (warpEntity instanceof LivingEntity) {
            ((LivingEntity) this.warpEntity).setAI(false);
            ((LivingEntity) this.warpEntity).setRemoveWhenFarAway(false);
        }
        if (this.warpEntity instanceof Slime) {
            ((Slime) this.warpEntity).setSize(10);
        }
        this.warpEntity.setCustomName(color + name + " MLG");
        this.warpEntity.setCustomNameVisible(true);
    }

    public Mlg(String name, ChatColor color, Class<? extends Entity> type, Material borderMaterial, Material bottomMaterial) {
        this(name, color, type, borderMaterial, new Material[]{bottomMaterial});
    }

    public Mlg(String name, ChatColor color, Class<? extends Entity> type, Material borderMaterial, Material[] bottomMaterials, Double[] bottomMaterialPercentages) {
        this(name, color, type, borderMaterial, bottomMaterials);
        randomizedBottomMaterials = true;
        this.bottomMaterialPercentages = bottomMaterialPercentages;
    }

    public Entity getWarpEntity() {
        return warpEntity;
    }

    public Mlg withPlatforms(Material material, int radius, int... yPositions) {
        this.platformMaterial = material;
        this.platformRadius = radius;
        for (int yPosition : yPositions) {
            platforms.add(new MlgPlatform(this, LocationUtils.ZERO_MLG, radius, yPosition, material));
        }
        return this;
    }

    public abstract List<ItemStack> getMlgItems();

    public void setMlgReady(Player player) {
        handlePlayerSetup(player);
    }

    protected boolean cantMlgHere(Block blockAgainst) {
        return Arrays.stream(bottomMaterials).noneMatch(m -> m.equals(blockAgainst.getType()));
    }

    @Override
    public void onEnter(Player player) {
        player.sendMessage("You entered " + this.getName() + " MLG");
        User user = UserList.INSTANCE.getUser(player);
        user.setRespawnLoc(getDefaultSpawn());
        setMlgReady(player);
        handleMlgSetup(player);

        // Scoreboard
        StreakPlayers.addStreakPlayer(player.getUniqueId(), new StreakPlayer());
        MlgScoreboard.create(MlgPlayer.get(player.getUniqueId(), getName()));
    }

    @Override
    public void onLeave(Player player) {
        player.sendMessage("You left " + this.getName() + " MLG");

        // Remove scoreboard
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    @Override
    public void onComplete(Player player) {
        handleMlgSetup(player);
        StreakPlayer.get(player).increaseStreak(MlgPlayerList.getMlgPlayer(player.getUniqueId()).getMlgName());
        MlgScoreboard.update(MlgPlayerList.getMlgPlayer(player.getUniqueId()));
        player.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "Successful MLG");
        player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
        Bukkit.getScheduler().runTaskLater(TrainingKt.getPLUGIN(), () -> teleportAndSetItems(player), 5L);
    }

    @Override
    public void onFailure(Player player) {
        player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "Failed MLG");
        handleMlgDeath(player);
        StreakPlayer.get(player).resetStreak(MlgPlayerList.getMlgPlayer(player.getUniqueId()).getMlgName());
        MlgScoreboard.update(MlgPlayerList.getMlgPlayer(player.getUniqueId()));
        Bukkit.getScheduler().runTaskLater(TrainingKt.getPLUGIN(), () -> {
            teleportAndSetItems(player);
            player.playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 1, 1);
        }, 0);
    }

    public Location getDefaultSpawn() {
        return platforms.get((platforms.size() - 1) / 2).getSpawn().clone().add(0, 1, 0);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Area getArea() {
        return cuboid;
    }

    @Override
    public ChatColor getColor() {
        return color;
    }

    public static void createRandomCylinder(World world, Location startLocation, int radius, boolean filled, int height, Map<Material, Double> blockPercentages) {
        try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(world), -1)) {
            editSession.setFastMode(true);
            RandomPattern randomPattern = new RandomPattern();
            for (Map.Entry<Material, Double> blockPercentage : blockPercentages.entrySet()) {
                // Add block with percentage
                randomPattern.add(BukkitAdapter.asBlockState(new ItemStack(blockPercentage.getKey())), blockPercentage.getValue());
            }
            // Create cylinder
            editSession.makeCylinder(BukkitAdapter.asBlockVector(startLocation), randomPattern, radius, height, filled);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        for (int index=0; index<bottomMaterials.length; index++) {
            if (randomizedBottomMaterials && bottomMaterialPercentages[index] != 100) {
                Map<Material, Double> blockPercentages = new HashMap<>();
                blockPercentages.put(bottomMaterials[index], bottomMaterialPercentages[index]);
                blockPercentages.put(Material.AIR, 100-bottomMaterialPercentages[index]); // e.g. 10% STONE results in 10% stone and 90% air
                createRandomCylinder(spawn.getWorld(), spawn.clone().add(0, index, 0), getBorderRadius(), true, 1, blockPercentages);
            }
            else WorldEditUtils.createCylinder(spawn.getWorld(), spawn.clone().add(0, index, 0), getBorderRadius(), true, 1, bottomMaterials[index]);
        }
        WorldEditUtils.createCylinder(spawn.getWorld(), spawn, getBorderRadius(), false, 255, borderMaterial);
        WorldEditUtils.createCylinder(spawn.getWorld(), spawn.clone().add(0, 255, 0), getBorderRadius(), true, 1, topMaterial);

        platforms.forEach(platform -> {
            Bukkit.getPluginManager().registerEvents(platform, TrainingKt.getPLUGIN());
            platform.setSpawn(spawn.clone());
        });

        platforms.get(0).setUp(platforms.get(1));
        platforms.get(0).setTop(platforms.get(platforms.size() - 1));
        platforms.get(platforms.size() - 1).setDown(platforms.get(platforms.size() - 2));
        platforms.get(platforms.size() - 1).setBottom(platforms.get(0));

        for (int i = 1; i < platforms.size() - 1; i++) {
            MlgPlatform mlgPlatform = platforms.get(i);
            mlgPlatform.setUp(platforms.get(i + 1));
            mlgPlatform.setTop(platforms.get(platforms.size() - 1));
            mlgPlatform.setDown(platforms.get(i - 1));
            mlgPlatform.setBottom(platforms.get(0));
        }

        platforms.forEach(MlgPlatform::create);
        warpEntity.getLocation().getChunk().setForceLoaded(true);
        ((CraftWorld) MlgWorld.INSTANCE.getWorld()).addEntity(((CraftEntity) warpEntity).getHandle(), CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    @Override
    public void stop() {
        platforms.forEach(MlgPlatform::clear);
        warpEntity.remove();
    }

    protected int getBorderRadius() {
        return platformRadius * 3;
    }

    @Override
    public boolean isInChallenge(Player player) {
        return UserList.INSTANCE.getUser(player).isInChallenge(this);
    }

    protected void teleportAndSetItems(Player player) {
        User user = UserList.INSTANCE.getUser(player);
        if (!user.getRespawnLoc().equals(LocationUtils.DAMAGER_SPAWN)) {
            player.teleport(user.getRespawnLoc());
        } else {
            player.teleport(getDefaultSpawn());
        }
        setMlgReady(player);
    }

    @Override
    public void initConfig() {
        FileConfiguration config = TrainingKt.getPLUGIN().getConfig();
        config.addDefault(String.format("%s.warpEntity.location", this.getName()), warpEntity.getLocation());
        config.addDefault(String.format("%s.mlgPlatform.spawn", this.getName()), spawn);
        config.addDefault(String.format("%s.location.first", this.getName()), cuboid.getFirst());
        config.addDefault(String.format("%s.location.second", this.getName()), cuboid.getSecond());
        config.options().copyDefaults(true);
        TrainingKt.getPLUGIN().saveConfig();
    }

    @Override
    public void saveToConfig() {
        FileConfiguration config = TrainingKt.getPLUGIN().getConfig();
        config.set(String.format("%s.warpEntity.location", this.getName()), warpEntity.getLocation());
        config.set(String.format("%s.mlgPlatform.spawn", this.getName()), spawn);
        config.set(String.format("%s.location.first", this.getName()), cuboid.getFirst());
        config.set(String.format("%s.location.second", this.getName()), cuboid.getSecond());
        TrainingKt.getPLUGIN().saveConfig();
    }

    public void loadFromConfig() {
        TrainingKt.getPLUGIN().reloadConfig();
        FileConfiguration config = TrainingKt.getPLUGIN().getConfig();
        spawn = config.getLocation(String.format("%s.mlgPlatform.spawn", this.getName()), spawn);
        Location warpEntityLocation = config.getLocation(String.format("%s.warpEntity.location", this.getName()), warpEntity.getLocation());
        warpEntity.teleport(warpEntityLocation);
        Location firstLoc = config.getLocation(String.format("%s.location.first", this.getName()), cuboid.getFirst());
        Location secondLoc = config.getLocation(String.format("%s.location.second", this.getName()), cuboid.getSecond());
        if (firstLoc != null && secondLoc != null) {
            cuboid = new Cuboid(firstLoc, secondLoc);
        }
    }

    @EventHandler
    public void onRightClickWarpEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity rightClicked = event.getRightClicked();
        if (rightClicked.equals(warpEntity)) {
            player.teleport(getDefaultSpawn());
            SoundUtils.playTeleportSound(player);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (isInChallenge(player)) {
            onFailure(player);
            for (ItemStack drop : event.getDrops()) {
                drop.setType(Material.AIR);
                drop.setAmount(0);
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (isInChallenge(player))
            if (player.getWorld().equals(spawn.getWorld()))
            if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                Block landedBlock = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
                if ((Arrays.stream(bottomMaterials).anyMatch((b) -> b.equals(landedBlock.getType())) || (getMlgItems() != null && getMlgItems().stream().anyMatch(i -> i.getType().equals(landedBlock.getType())))
                        || landedBlock.getType().equals(Material.AIR)) && !isBorderedBy(landedBlock, platformMaterial)) {
                    player.setHealth(0.0); // Kill player
                }
                // Cancel the event so the player doesn't get killed twice
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onSettingChanged(SettingChangedEvent event) {
        Player player = Bukkit.getPlayer(event.getPlayerUUID());
        if (player == null) return;
        if (!isInChallenge(player)) return;

        if (!event.getTargetValue()) switch (event.getSetting()) {
            case LEVITATOR_SHEEP:
                // Hide all levitator sheep for the player
                platforms.forEach(platform -> {
                    for (Sheep sheep : platform.getLevitatorSheep()) {
                        PacketSender.INSTANCE.hideEntities(player, sheep);
                    }
                });
                break;
            case TOP_BOTTOM_PHANTOMS:
                // Hide all top/bottom phantoms for the player
                platforms.forEach(platform -> {
                    for (Phantom phantom : platform.getPhantoms()) {
                        PacketSender.INSTANCE.hideEntities(player, phantom);
                    }
                });
                break;
            case SUPPLY_PANDAS:
                // Hide all supply pandas for the player
                platforms.forEach(platform -> {
                    for (Panda panda : platform.getSupplyPandas()) {
                        PacketSender.INSTANCE.hideEntities(player, panda);
                    }
                });
        }
        else switch (event.getSetting()) {
            case LEVITATOR_SHEEP:
                // Show all levitator sheep for the player
                platforms.forEach(platform -> {
                    for (Sheep sheep : platform.getLevitatorSheep()) {
                        PacketSender.INSTANCE.showEntities(player, sheep);
                    }
                });
                break;
            case TOP_BOTTOM_PHANTOMS:
                // Show all top/bottom phantoms for the player
                platforms.forEach(platform -> {
                    for (Phantom phantom : platform.getPhantoms()) {
                        PacketSender.INSTANCE.showEntities(player, phantom);
                    }
                });
                break;
            case SUPPLY_PANDAS:
                // Show all supply pandas for the player
                platforms.forEach(platform -> {
                    for (Panda panda : platform.getSupplyPandas()) {
                        PacketSender.INSTANCE.showEntities(player, panda);
                    }
                });
        }
    }

    /** Must be called when the player has placed the mlg item e.g. water bucket or block such as cobweb */
    protected void handleMlg(Player player, long checkDelay) {
        User user = getUser(player);
        MlgInfo info = user.getChallengeInfoOrDefault(this, new MlgInfo());
        if (info.hasDoneAction()) {
            // Ignore more placements before player hasn't died so he can't spam multiple blocks for multiple "successful mlg"s
            return;
        }
        info.setHasDied(false);
        info.setHasDoneAction(true);
        Bukkit.getScheduler().runTaskLater(TrainingKt.getPLUGIN(), () -> {
            MlgInfo mlgInfo = user.getChallengeInfoOrDefault(this, new MlgInfo());
            if (!mlgInfo.hasDied()) {
                onComplete(player);
            }
            Bukkit.getScheduler().runTaskLater(TrainingKt.getPLUGIN(), () -> handleReset(player), 10L);
        }, checkDelay);
    }

    /** Must be called when the player has placed the mlg item e.g. water bucket or block such as cobweb */
    protected void handleMlg(Player player) {
        handleMlg(player, 10L);
    }

    /** is called in {@link Mlg#onFailure} by default */
    protected void handleMlgDeath(Player player) {
        User user = getUser(player);

        MlgInfo mlgInfo = user.getChallengeInfoOrDefault(this, new MlgInfo());
        mlgInfo.setHasDied(true);
        user.addChallengeInfo(this, mlgInfo);
    }

    /** is called in {@link Mlg#onEnter} by default */
    protected void handleMlgSetup(Player player) {
        User user = getUser(player);
        MlgInfo mlgInfo = new MlgInfo();
        mlgInfo.setHasDoneAction(user.getChallengeInfoOrDefault(this, new MlgInfo()).hasDoneAction());
        user.addChallengeInfo(this, mlgInfo);
    }
    
    /** remove a block after a given amount of time */
    protected void removeBlockLater(Block block, long delay) {
        Bukkit.getScheduler().runTaskLater(TrainingKt.getPLUGIN(), () -> block.setType(Material.AIR), delay);
    }

    /** remove an entity after a given amount of time */
    protected void removeEntityLater(Entity entity, long delay) {
        Bukkit.getScheduler().runTaskLater(TrainingKt.getPLUGIN(), entity::remove, delay);
    }

    protected void setMaxHealth(Player player) {
        player.setHealth(player.getHealthScale());
    }

    /** check if a block is bordered by a material */
    public boolean isBorderedBy(Block block, Material material) {
        for (BlockFace blockFace : new BlockFace[] {BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST,
                BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST}) {
            if (block.getRelative(blockFace).getType().equals(material)) {
                return true;
            }
        }
        return false;
    }

    protected void mainInventorySetup(Player player) {
        player.getInventory().clear();
        player.getInventory().setItem(0, WarpItems.WARP_SELECTOR);
        player.getInventory().setItem(7, WarpItems.HUB);
        player.getInventory().setItem(8, WarpItems.RESPAWN_ANCHOR);
        player.getInventory().setItem(17, WarpItems.SETTINGS);
    }

    protected void inventorySetup(Player player) {
        // By default the first and only mlg item in the 4th slot. When not this must be overridden
        player.getInventory().setItem(4, getMlgItems().get(0));
    }

    protected void handlePlayerSetup(Player player) {
        setMaxHealth(player);
        player.setFoodLevel(100);
        player.setCollidable(false);
        mainInventorySetup(player);
        inventorySetup(player);
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
    }

    protected void handleReset(Player player) {
        // By default set MlgInfo.hasDoneAction to false again
        getUser(player).getChallengeInfoOrDefault(this, new MlgInfo()).setHasDoneAction(false);
    }

    protected boolean isAllowedToBuild(Player player) {
        return player.getGameMode().equals(GameMode.CREATIVE);
    }
}
