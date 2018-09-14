package agp.andwhat5.commands.gyms;

import agp.andwhat5.AGP;
import agp.andwhat5.Utils;
import agp.andwhat5.config.AGPConfig;
import agp.andwhat5.config.structs.DataStruc;
import agp.andwhat5.config.structs.GymStruc;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

public class DelGymCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        String command = args.<String>getOne("command").get();

        GymStruc gym = args.<GymStruc>getOne("GymName").get();
        if (gym.Commands.contains(command)) {
            gym.Commands.remove(command);
            src.sendMessage(Utils.toText("&7Successfully removed that command.", true));
            if (AGPConfig.Storage.storageType.equalsIgnoreCase("flatfile")) {
                Utils.editGym(gym);
                AGP.getInstance().getStorage().saveData(DataStruc.gcon);
            } else {
                Utils.addGym(gym);
            }
            return CommandResult.success();
        } else {
            src.sendMessage(Utils.toText("&7That command is not in this gyms rewards pool.", true));
            return CommandResult.success();
        }
    }

}
