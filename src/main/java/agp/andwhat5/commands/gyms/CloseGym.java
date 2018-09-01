package agp.andwhat5.commands.gyms;

import agp.andwhat5.Utils;
import agp.andwhat5.commands.Command;
import agp.andwhat5.config.AGPConfig;
import agp.andwhat5.config.structs.GymStruc;
import net.minecraft.command.CommandException;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

import java.util.List;
import java.util.UUID;

import static agp.andwhat5.config.structs.GymStruc.EnumStatus.*;
import static net.minecraft.command.CommandBase.getListOfStringsMatchingLastWord;

public class CloseGym extends Command {

    public CloseGym() {
        super("Closes the specified gym.");
    }

    @Override
    public void execute(MinecraftServer server, CommandSource sender, String[] args) throws CommandException {
        if (args.length < 1) {
        	sender.sendMessage(Utils.toText("&7Incorrect usage: &b/CloseGym [-f] <gym>&7.", true));
            return;
        }
        boolean force = false;
        int gymArg = 0;

        if (args.length == 2 && args[0].equalsIgnoreCase("-f")) {
            force = true;
            gymArg = 1;
        }

        if (!Utils.gymExists(args[gymArg])) {
            sender.sendMessage(Utils.toText("&7The &b" + args[gymArg] + " &7Gym does not exist!", true));
            return;
        }
        GymStruc gs = Utils.getGym(args[gymArg]);
        if (!Utils.isGymLeader((Player) sender, gs) && !sender.hasPermission("agp.headleader")) {
            sender.sendMessage(Utils.toText("&7You must be a leader of the &b" + gs.Name + " &7Gym to close it!", true));
            return;
        }
        if (gs.Status != OPEN && gs.Status != NPC) {
            sender.sendMessage(Utils.toText("&7The &b" + gs.Name + " &7Gym is not open!", true));
            return;
        }

        if (force) {
            gs.Status = CLOSED;
        } else {
            if (gs.NPCAmount > 0) {
                gs.Status = NPC;
            } else {
                gs.Status = CLOSED;
            }
        }

        for (UUID queued : Utils.getQueuedPlayers(gs)) {
            Sponge.getServer().getPlayer(queued).ifPresent(player -> player.sendMessage(Utils.toText("&7The &b" + gs.Name + " &7Gym is closing! Removing you from the queue...", true)));
        }

        gs.Queue.clear();
        if (AGPConfig.Announcements.closeAnnouncement) {
            for (Player player : Utils.getAllPlayers())
                player.sendMessage(Utils.toText(AGPConfig.Announcements.closeMessage
                        .replace("{gym}", gs.Name)
                        .replace("{leader}", sender.getName()), false));
        } else {
            for (UUID leader : Utils.getGymLeaders(gs)) {
                Sponge.getServer().getPlayer(leader).ifPresent(player -> player.sendMessage(Utils.toText("&7Leader &b" + sender.getName() + " &7has closed the &b" + gs.Name + " &7Gym!", true)));
            }
        }


    }


    @Override
    public List<String> getTabCompletions(MinecraftServer server, CommandSource sender, String[] args) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, Utils.getGymNames(true));
        }
        return null;
    }

}
