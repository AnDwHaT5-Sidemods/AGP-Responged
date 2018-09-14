package agp.andwhat5.commands.gyms;

import agp.andwhat5.AGP;
import agp.andwhat5.Utils;
import agp.andwhat5.config.structs.GymStruc;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

public class DeleteGym implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        GymStruc gs = args.<GymStruc>getOne("GymName").get();
        Utils.removeGym(gs);
        AGP.getInstance().getStorage().updateAllBadges(gs);
        Utils.saveAGPData();
        src.sendMessage(Utils.toText("&7Successfully deleted the &b" + gs.Name + " &7Gym!", true));
        return CommandResult.success();
    }

}
