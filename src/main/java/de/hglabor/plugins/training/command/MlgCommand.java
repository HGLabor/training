package de.hglabor.plugins.training.command;

import de.hglabor.plugins.training.challenges.Challenge;
import de.hglabor.plugins.training.challenges.ChallengeManager;
import de.hglabor.plugins.training.challenges.mlg.Mlg;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import org.bukkit.Location;

import java.util.Optional;

public class MlgCommand {
    private final static String WARP_ENTITY = "warpentity";

    public MlgCommand() {
        new CommandAPICommand("mlg")
                .withPermission("group.admin")
                .withArguments(createMlgArgument())
                .withArguments(new GreedyStringArgument("action").overrideSuggestions(WARP_ENTITY))
                .executesPlayer((player, objects) -> {
                    String action = (String) objects[1];
                    Mlg mlg = (Mlg) objects[0];
                    switch (action.toLowerCase()) {
                        case WARP_ENTITY:
                            Location location = player.getLocation();
                            player.sendMessage("New WarpEntity Location for " + mlg.getName() + ": " + location.toString());
                            mlg.getWarpEntity().teleport(location);
                            mlg.safeToConfig();
                            break;
                    }
                }).register();
    }

    private Argument createMlgArgument() {
        return new CustomArgument<>("mlg", input -> {
            Optional<Challenge> mlg = ChallengeManager.INSTANCE.getChallenges()
                    .stream()
                    .filter(challenge -> challenge instanceof Mlg)
                    .filter(challenge -> challenge.getName().equalsIgnoreCase(input)).findFirst();
            if (mlg.isEmpty()) {
                throw new CustomArgument.CustomArgumentException(new CustomArgument.MessageBuilder("Unknown Mlg: ").appendArgInput());
            } else {
                return (Mlg) mlg.get();
            }
        }).overrideSuggestions(sender -> ChallengeManager.INSTANCE.getChallenges().stream().filter(challenge -> challenge instanceof Mlg).map(Challenge::getName).toArray(String[]::new));
    }
}
