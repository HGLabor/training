package de.hglabor.plugins.training.command;

import de.hglabor.plugins.training.Training;
import de.hglabor.plugins.training.challenges.Challenge;
import de.hglabor.plugins.training.challenges.ChallengeManager;
import de.hglabor.plugins.training.challenges.damager.Damager;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class DamagerCommand implements Listener {
    private final Map<UUID, Damager> damagerMap;

    public DamagerCommand() {
        this.damagerMap = new HashMap<>();
        new CommandAPICommand("damager")
                .withPermission("group.admin")
                .withArguments(createDamagerArgument())
                .withArguments(new GreedyStringArgument("action").overrideSuggestions("relocate", "hologram", "confirm"))
                .executesPlayer((player, objects) -> {
                    String action = (String) objects[1];
                    Damager damager = (Damager) objects[0];
                    switch (action.toLowerCase()) {
                        case "hologram":
                            Location location = player.getLocation();
                            player.sendMessage("New Hologram Location for " + damager.getName() + ": " + location.toString());
                            damager.setHologramOrigin(location);
                            damager.safeToConfig();
                            break;
                        case "relocate":
                            damagerMap.put(player.getUniqueId(), damager);
                            player.sendMessage("Damager: " + damager.getName());
                            player.sendMessage("Left click to set first location");
                            player.sendMessage("Right click to set second location");
                            break;
                        case "confirm":
                            damagerMap.remove(player.getUniqueId());
                            player.sendMessage("You successfully relocated " + damager.getName());
                            damager.safeToConfig();
                            break;
                    }
                }).register();
        Bukkit.getPluginManager().registerEvents(this, Training.getInstance());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getClickedBlock() == null) {
            return;
        }
        if (!damagerMap.containsKey(player.getUniqueId())) {
            return;
        }
        event.setCancelled(true);
        Damager damager = damagerMap.get(player.getUniqueId());
        Location location = event.getClickedBlock().getLocation();
        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            player.sendMessage("First location: " + location.toString());
            damager.getArea().setFirstLoc(location);
            return;
        }
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            player.sendMessage("Second location: " + location.toString());
            damager.getArea().setSecondLoc(location);
        }
    }

    private Argument createDamagerArgument() {
        return new CustomArgument<>("damager", input -> {
            Optional<Challenge> damager = ChallengeManager.INSTANCE.getChallenges()
                    .stream()
                    .filter(challenge -> challenge instanceof Damager)
                    .filter(challenge -> challenge.getName().equalsIgnoreCase(input)).findFirst();
            if (damager.isEmpty()) {
                throw new CustomArgument.CustomArgumentException(new CustomArgument.MessageBuilder("Unknown Damager: ").appendArgInput());
            } else {
                return (Damager) damager.get();
            }
        }).overrideSuggestions(sender -> ChallengeManager.INSTANCE.getChallenges().stream().filter(challenge -> challenge instanceof Damager).map(Challenge::getName).toArray(String[]::new));
    }
}
