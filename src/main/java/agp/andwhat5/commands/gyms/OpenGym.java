package agp.andwhat5.commands.gyms;

import agp.andwhat5.Utils;
import agp.andwhat5.commands.Command;
import agp.andwhat5.config.AGPConfig;
import agp.andwhat5.config.structs.GymStruc;
import net.minecraft.command.CommandException;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

import java.util.List;

import static agp.andwhat5.config.structs.GymStruc.EnumStatus.OPEN;
import static net.minecraft.command.CommandBase.getListOfStringsMatchingLastWord;

public class OpenGym extends Command {

    public OpenGym() {
        super("Opens the specified gym.");
    }

    @Override
    public void execute(MinecraftServer server, CommandSource sender, String[] args) throws CommandException {
        if (args.length != 1) {
        	sender.sendMessage(Utils.toText("&7Incorrect usage: &b/OpenGym <gym>&7.", true));
        	return;
        }

        String gymName = args[0];
        if (!Utils.gymExists(gymName)) {
            sender.sendMessage(Utils.toText("&7The &b" + gymName + " &7Gym does not exist!", true));
            return;
        }
        GymStruc gs = Utils.getGym(gymName);
        if (!Utils.isGymLeader((Player) sender, gs) && !sender.hasPermission("agp.headleader")) {
            sender.sendMessage(Utils.toText("&7You are not a leader of the &b" + gs.Name + " &7Gym!", true));
            return;
        }

        if (gs.Status == OPEN) {
            sender.sendMessage(Utils.toText("&7The &b" + gs.Name + " &7Gym is already open!", true));
            return;
        }

        gs.Status = OPEN;
        sender.sendMessage(Utils.toText("&7Successfully opened the &b" + gs.Name + " &7Gym!", true));
        if (AGPConfig.Announcements.openAnnouncement) {
            for (Player player : Utils.getAllPlayers())
                player.sendMessage(Utils.toText(AGPConfig.Announcements.openMessage
                        .replace("{gym}", gs.Name).replace("{leader}", sender.getName()), false));

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
