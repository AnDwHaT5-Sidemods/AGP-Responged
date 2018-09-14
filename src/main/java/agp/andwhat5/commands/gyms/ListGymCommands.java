package agp.andwhat5.commands.gyms;

import agp.andwhat5.Utils;
import agp.andwhat5.config.structs.GymStruc;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

public class ListGymCommands implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        GymStruc gym = args.<GymStruc>getOne("GymName").get();
        src.sendMessage(Utils.toText("&7This gyms commands are as follows:", true));
        gym.Commands.forEach(c -> src.sendMessage(Utils.toText("&b" + c, true)));

        return CommandResult.success();
    }

}
