package agp.andwhat5.commands.players;

import agp.andwhat5.Utils;
import agp.andwhat5.commands.utils.PlayerOnlyCommand;
import agp.andwhat5.config.structs.ArenaStruc;
import agp.andwhat5.config.structs.GymStruc;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;

public class GymWarp extends PlayerOnlyCommand {

    @Override
    public CommandResult execute(Player player, CommandContext args) {
        GymStruc gym = args.<GymStruc>getOne("GymName").get();
        String location = args.<String>getOne("location").get();

        switch (location.toLowerCase()) {
            case "lobby":
                if (gym.Lobby == null) {
                    player.sendMessage(Utils.toText("&7The &b" + gym.Name + " &7Gym's lobby has not been set!", true));
                    return CommandResult.success();
                }
                Utils.setPosition(player, gym.Lobby, gym.worldUUID);
                player.sendMessage(Utils.toText("&7Teleported to the &b" + gym.Name + " &7Gym lobby!", true));
                break;

            case "arena":
                Optional<ArenaStruc> optArena = args.getOne("GymArena");
                Optional<String> optArenaSubLocationName = args.getOne("arenaSubLocation");

                if (!optArena.isPresent() || !optArenaSubLocationName.isPresent()) {
                    player.sendMessage(Utils.toText("&7Incorrect usage: &b/GymWarp <gym> <lobby|arena> [(if arena) <name> <stands|challenger|leader>]&7.", true));
                    return CommandResult.success();
                }

                ArenaStruc arena = optArena.get();
                String arenaSubLocationName = optArenaSubLocationName.get();

                switch (arenaSubLocationName) {
                    case "stands":
                        if (arena.Stands == null) {
                            player.sendMessage(Utils.toText("&7The &b" + gym.Name + " &7Gym's &b" + arena.Name + " &7stands has not been set!", true));
                            return CommandResult.success();
                        }

                        Utils.setPosition(player, arena.Stands, gym.worldUUID);
                        player.sendMessage(Utils.toText("&7Teleported to the &b" + gym.Name + " &7Gym's &b" + arena.Name + " stands&7!", true));
                        break;

                    case "challenger":
                        if (arena.Challenger == null) {
                            player.sendMessage(Utils.toText("&7The &b" + gym.Name + " &7Gym's &b" + arena.Name + " &challenger stage has not been set!", true));
                            return CommandResult.success();
                        }

                        Utils.setPosition(player, arena.Challenger, gym.worldUUID);
                        player.sendMessage(Utils.toText("&7Teleported to the &b" + gym.Name + " &7Gym's &b" + arena.Name + " challenger stage&7!", true));
                        break;

                    case "leader":
                        if (arena.Leader == null) {
                            player.sendMessage(Utils.toText("&7The &b" + gym.Name + " &7Gym's &b" + arena.Name + " &7leader stage has not been set!", true));
                            return CommandResult.success();
                        }
                        Utils.setPosition(player, arena.Leader, gym.worldUUID);
                        player.sendMessage(Utils.toText("&7Teleported to the &b" + gym.Name + " &7Gym's &b" + arena.Name + " &7leader stage!", true));
                        break;
                    default:
                        player.sendMessage(Utils.toText("&7The location &b" + arenaSubLocationName + " &7was not recognized!", true));
                        break;
                }
                break;

            default:
                player.sendMessage(Utils.toText("&7The location &b" + location + " &7was not recognized!", true));
                return CommandResult.success();
        }

        return CommandResult.success();
    }

}
