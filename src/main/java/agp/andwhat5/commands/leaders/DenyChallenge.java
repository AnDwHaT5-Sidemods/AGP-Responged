package agp.andwhat5.commands.leaders;

import agp.andwhat5.Utils;
import agp.andwhat5.commands.Command;
import agp.andwhat5.config.structs.GymStruc;
import net.minecraft.command.CommandException;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

import java.util.List;
import java.util.UUID;

import static net.minecraft.command.CommandBase.getListOfStringsMatchingLastWord;

public class DenyChallenge extends Command {

    public DenyChallenge() {
        super("/denychallenge <gym> <opt-challenger>");
    }

    @Override
    public void execute(MinecraftServer server, CommandSource sender, String[] args) throws CommandException {
        if (args.length != 1 && args.length != 2) {
            super.sendUsage(sender);
            return;
        }

        if (!Utils.gymExists(args[0])) {
            sender.sendMessage(Utils.toText("&7The &b" + args[1] + " &7Gym does not exist!", true));
            return;
        }

        GymStruc gs = Utils.getGym(args[0]);
        if (!Utils.isGymLeader((Player) sender, gs) && !sender.hasPermission("agp.headleader")) {
            sender.sendMessage(Utils.toText("&7You are not a leader of the &b" + gs.Name + " &7Gym!", true));
            return;
        }

        if (Utils.getQueuedPlayers(gs).isEmpty()) {
            sender.sendMessage(Utils.toText("&7The &b" + gs.Name + " &7Gym's queue is empty!", true));
            return;
        }

        if (args.length == 1) {
            UUID pUUID = gs.Queue.poll();
            sender.sendMessage(Utils.toText("&7Challenger &b" + pUUID + " &7has been removed from the &b" + gs.Name + " &7Gym queue!", true));
            Player player = Sponge.getServer().getPlayer(pUUID).orElse(null);
            if (player != null) {
                player.sendMessage(Utils.toText("&7Your challenge to the &b" + gs.Name + " &7Gym " +
                        "was denied!", true));
            }
        } else {
            Player player = Sponge.getServer().getPlayer(args[1]).orElse(null);

            UUID pUUID = player != null ? player.getUniqueId() : Sponge.getServer().getPlayer(args[1]).get().getUniqueId();
            if (!gs.Queue.contains(pUUID)) {
                sender.sendMessage(Utils.toText("&7Challenger &b" + pUUID + " &7is not in the &b" + gs.Name + " &7Gym queue!", true));
                return;
            }
            gs.Queue.remove(pUUID);
            sender.sendMessage(Utils.toText("&7Challenger &b" + pUUID + " &7has been removed from the &b" + gs.Name + " &7Gym queue!", true));
            if (player != null) {
                player.sendMessage(Utils.toText("&7Your challenge to the &b" + gs.Name + " " +
                        "&7Gym was denied!", true));
            }
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, CommandSource sender, String[] args) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, Utils.getGymNames(true));
        } else if (args.length == 2 && Utils.gymExists(args[0])) {
            return getListOfStringsMatchingLastWord(args, Utils.getGym(args[0]).Queue);
        }
        return null;
    }

}
