package de.hglabor.plugins.training.command;

import de.hglabor.plugins.training.challenges.Challenge;
import de.hglabor.plugins.training.challenges.ChallengeManager;
import de.hglabor.plugins.training.challenges.damager.Damager;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import org.bukkit.Location;

import java.util.Optional;

public class DamagerCommand  {
    public DamagerCommand() {
        new CommandAPICommand("damager")
                .withPermission("group.admin")
                .withArguments(createDamagerArgument())
                .withArguments(new GreedyStringArgument("action").overrideSuggestions("hologram"))
                .executesPlayer((player, objects) -> {
                    String action = (String) objects[1];
                    Damager damager = (Damager) objects[0];
                    switch (action.toLowerCase()) {
                        case "hologram":
                            Location location = player.getLocation();
                            player.sendMessage("New Hologram Location for " + damager.getName() + ": " + location.toString());
                            damager.setHologramOrigin(location);
                            damager.saveToConfig();
                            break;
                    }
                }).register();
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
