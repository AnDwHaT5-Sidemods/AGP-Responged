package agp.andwhat5.commands.gyms;

import agp.andwhat5.AGP;
import agp.andwhat5.Utils;
import agp.andwhat5.commands.Command;
import agp.andwhat5.config.structs.GymStruc;
import net.minecraft.command.CommandException;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.api.command.CommandSource;

import java.util.List;

import static net.minecraft.command.CommandBase.getListOfStringsMatchingLastWord;

public class DeleteGym extends Command {

    public DeleteGym() {
        super("Deletes the specified gym.");
    }

    @Override
    public void execute(MinecraftServer server, CommandSource sender, String[] args) throws CommandException {
        if (args.length != 1) {
        	sender.sendMessage(Utils.toText("&7Incorrect usage: &b/DelGym <gym>&7.", true));
            return;
        }
        if (!Utils.gymExists(args[0])) {
            sender.sendMessage(Utils.toText("&7The &b" + args[0] + " &7Gym does not exist!", true));
            return;
        }

        GymStruc gs = Utils.getGym(args[0]);
        Utils.removeGym(gs);
        AGP.getInstance().getStorage().updateAllBadges(gs);
        Utils.saveAGPData();
        sender.sendMessage(Utils.toText("&7Successfully deleted the &b" + gs.Name + " &7Gym!", true));

    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, CommandSource sender, String[] args) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, Utils.getGymNames(true));
        }
        return null;
    }

}
