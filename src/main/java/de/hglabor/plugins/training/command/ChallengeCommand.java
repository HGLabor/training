package de.hglabor.plugins.training.command;

import de.hglabor.plugins.training.main.TrainingKt;
import de.hglabor.plugins.training.challenges.Challenge;
import de.hglabor.plugins.training.challenges.ChallengeManager;
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

public class ChallengeCommand implements Listener {
    private final Map<UUID, Challenge> challengeMap;

    public ChallengeCommand() {
        challengeMap = new HashMap<>();
        new CommandAPICommand("challenge")
                .withPermission("group.admin")
                .withArguments(createChallengeArgument())
                .withArguments(new GreedyStringArgument("action").overrideSuggestions("start", "stop", "restart", "relocate", "confirm"))
                .executesPlayer((player, objects) -> {
                    Challenge challenge = (Challenge) objects[0];
                    String action = (String) objects[1];
                    switch (action.toLowerCase()) {
                        case "start":
                            challenge.start();
                            break;
                        case "stop":
                            challenge.stop();
                            break;
                        case "restart":
                            challenge.restart();
                            break;
                        case "relocate":
                            challengeMap.put(player.getUniqueId(), challenge);
                            player.sendMessage("Challenge: " + challenge.getName());
                            player.sendMessage("Left click to set first location");
                            player.sendMessage("Right click to set second location");
                            break;
                        case "confirm":
                            challengeMap.remove(player.getUniqueId());
                            player.sendMessage("You successfully relocated " + challenge.getName());
                            challenge.safeToConfig();
                            break;
                    }
                    player.sendMessage(challenge.getName() + ": " + action);
                }).register();
        Bukkit.getPluginManager().registerEvents(this, TrainingKt.getPLUGIN());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getClickedBlock() == null) {
            return;
        }
        if (!challengeMap.containsKey(player.getUniqueId())) {
            return;
        }
        event.setCancelled(true);
        Challenge challenge = challengeMap.get(player.getUniqueId());
        Location location = event.getClickedBlock().getLocation();
        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            player.sendMessage("First location: " + location.toString());
            challenge.getArea().setFirstLoc(location);
            return;
        }
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            player.sendMessage("Second location: " + location.toString());
            challenge.getArea().setSecondLoc(location);
        }
    }

    private Argument createChallengeArgument() {
        return new CustomArgument<>("challenge", input -> {
            Optional<Challenge> challenge = ChallengeManager.INSTANCE.getChallenges()
                    .stream()
                    .filter(c -> c.getName().equalsIgnoreCase(input)).findFirst();
            if (challenge.isEmpty()) {
                throw new CustomArgument.CustomArgumentException(new CustomArgument.MessageBuilder("Unknown Challenge: ").appendArgInput());
            } else {
                return challenge.get();
            }
        }).overrideSuggestions(sender -> ChallengeManager.INSTANCE.getChallenges().stream().map(Challenge::getName).toArray(String[]::new));
    }
}
