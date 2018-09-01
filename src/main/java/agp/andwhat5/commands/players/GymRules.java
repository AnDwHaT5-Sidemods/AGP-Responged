package agp.andwhat5.commands.players;

import agp.andwhat5.Utils;
import agp.andwhat5.commands.Command;
import net.minecraft.command.CommandException;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.api.command.CommandSource;

public class GymRules extends Command {

    public GymRules() {
        super("Shows the rules for the specified gym.");
    }

    @Override
    public void execute(MinecraftServer server, CommandSource sender, String[] args) throws CommandException {
        if (args.length != 1) {
            sender.sendMessage(Utils.toText("&7Incorrect Usage: &b/GymRules <gym>&7.", true));
            return;
        }
        if (!Utils.gymExists(args[0])) {
            sender.sendMessage(Utils.toText("&7This gym does not exist!", true));
            return;
        }
        if (Utils.getGym(args[0]).Rules == "") {
            sender.sendMessage(Utils.toText("&7This gym does not have any rules!", true));
            return;
        }
        String[] msg = Utils.getGym(args[0]).Rules.split("/n");
        for (String s : msg) {
            sender.sendMessage(Utils.toText("&7" + s, true));
        }
    }

}
