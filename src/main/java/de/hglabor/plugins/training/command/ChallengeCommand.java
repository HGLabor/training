package de.hglabor.plugins.training.command;

import de.hglabor.plugins.training.challenges.Challenge;
import de.hglabor.plugins.training.challenges.ChallengeManager;
import de.hglabor.plugins.training.challenges.damager.Damager;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.GreedyStringArgument;

import java.util.Optional;

public class ChallengeCommand {
    public ChallengeCommand() {
        new CommandAPICommand("challenge")
                .withPermission("group.admin")
                .withArguments(createChallengeArgument())
                .withArguments(new GreedyStringArgument("action").overrideSuggestions("start", "stop", "restart"))
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
                    }
                    player.sendMessage(challenge.getName() + ": " + action);
                }).register();
    }

    private Argument createChallengeArgument() {
        return new CustomArgument<>("challenge", input -> {
            Optional<Challenge> damager = ChallengeManager.INSTANCE.getChallenges()
                    .stream()
                    .filter(challenge -> challenge.getName().equalsIgnoreCase(input)).findFirst();
            if (damager.isEmpty()) {
                throw new CustomArgument.CustomArgumentException(new CustomArgument.MessageBuilder("Unknown Challenge: ").appendArgInput());
            } else {
                return (Damager) damager.get();
            }
        }).overrideSuggestions(sender -> ChallengeManager.INSTANCE.getChallenges().stream().map(Challenge::getName).toArray(String[]::new));
    }
}
