package agp.andwhat5.commands.players;

import agp.andwhat5.Utils;
import agp.andwhat5.commands.Command;
import agp.andwhat5.config.structs.ArenaStruc;
import agp.andwhat5.config.structs.GymStruc;
import net.minecraft.command.CommandException;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static net.minecraft.command.CommandBase.getListOfStringsMatchingLastWord;

public class GymWarp extends Command {

    public GymWarp() {
        super("Warps you to the specified gym location.");
    }

    @Override
    public void execute(MinecraftServer server, CommandSource sender, String[] args) throws CommandException {
        Player user = requireEntityPlayer(sender);
        String loc = args.length >= 2 ? args[1] : "lobby";
        if (args.length != 1 && args.length != 2 && args.length != 4) {
            sender.sendMessage(Utils.toText("&7Incorrect usage: &b/GymWarp <gym> <lobby|home|arena> [(if arena) <name> <stands|challenger|leader>]&7.", true));
            return;
        }

        if (!Utils.gymExists(args[0])) {
            sender.sendMessage(Utils.toText("&7The &b" + args[0] + " &7Gym does not exist!", true));
            return;
        }

        GymStruc gs = Utils.getGym(args[0]);
        switch (loc.toLowerCase()) {
            case "lobby":
                if (gs.Lobby == null) {
                    sender.sendMessage(Utils.toText("&7The &b" + gs.Name + " &7Gym's lobby has not been set!", true));
                    return;
                }
                Utils.setPosition(user, gs.Lobby);
                sender.sendMessage(Utils.toText("&7Teleported to the &b" + gs.Name + " &7Gym lobby!", true));
                break;
            case "arena":
                ArenaStruc as;
                if (args.length <= 2) {
                    sender.sendMessage(Utils.toText("&7You must specify a valid arena teleport location", true));
                    return;
                }
                if ((as = Utils.getArena(gs, args[2])) == null) {
                    sender.sendMessage(Utils.toText("&7The Arena &b" + args[2] + " &7could not be found!", true));
                    return;
                }
                switch (args[3].toLowerCase()) {
                    case "stands":
                        if (as.Stands == null) {
                            sender.sendMessage(Utils.toText("&7The &b" + gs.Name + " &7Gym's &b" + as.Name + " &7stands has not been set!", true));
                            return;
                        }
                        Utils.setPosition(user, as.Stands);
                        sender.sendMessage(Utils.toText("&7Teleported to the &b" + gs.Name + " &7Gym's &b" + as.Name + " stands&7!", true));
                        break;
                    case "challenger":
                        if (as.Challenger == null) {
                            sender.sendMessage(Utils.toText("&7The &b" + gs.Name + " &7Gym's &b" + as.Name + " &7challenger stage has not been set!", true));
                            return;
                        }
                        Utils.setPosition(user, as.Challenger);
                        sender.sendMessage(Utils.toText("&7Teleported to the &b" + gs.Name + " &7Gym's &b" + as.Name + " &7challenger stage!", true));
                        break;
                    case "leader":
                        if (as.Leader == null) {
                            sender.sendMessage(Utils.toText("&7The &b" + gs.Name + " &7Gym's &b" + as.Name + " &7leader stage has not been set!", true));
                            return;
                        }
                        Utils.setPosition(user, as.Leader);
                        sender.sendMessage(Utils.toText("&7Teleported to the &b" + gs.Name + " &7Gym's &b" + as.Name + " &7leader stage!", true));
                        break;
                    default:
                        sender.sendMessage(Utils.toText("&7The location &b" + args[3] + " &7was not recognized!", true));
                }

                break;
            default:
                sender.sendMessage(Utils.toText("&7The location &b" + args[1] + " &7was not recognized!", true));
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, CommandSource sender, String[] args) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, Utils.getGymNames(true));
        } else if (args.length == 2) {
            if (Utils.gymExists(args[0]) && Utils.isGymLeader((Player) sender, Utils.getGym(args[1]))) {
                return getListOfStringsMatchingLastWord(args, Arrays.asList("arena", "home", "lobby"));
            } else {
                return getListOfStringsMatchingLastWord(args, Arrays.asList("arena", "lobby"));
            }
        } else if (args.length == 3 && args[1].equalsIgnoreCase("arena") && Utils.gymExists(args[0])) {
            return getListOfStringsMatchingLastWord(args, Utils.getArenaNames(Utils.getGym(args[0]), true));
        } else if (args.length == 4) {
            if (Utils.gymExists(args[0]) && Utils.isGymLeader((Player) sender, Utils.getGym(args[1]))) {
                return getListOfStringsMatchingLastWord(args, Arrays.asList("challenger", "leader", "stands"));
            } else {
                return getListOfStringsMatchingLastWord(args, Collections.singletonList("stands"));
            }
        }
        return null;
    }

}
