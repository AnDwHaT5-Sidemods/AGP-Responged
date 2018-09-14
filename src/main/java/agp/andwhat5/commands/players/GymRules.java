package agp.andwhat5.commands.players;

import agp.andwhat5.Utils;
import agp.andwhat5.config.structs.GymStruc;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

public class GymRules implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        GymStruc gym = args.<GymStruc>getOne("GymName").get();
        if (gym.Rules.isEmpty()) {
            src.sendMessage(Utils.toText("&7This gym does not have any rules!", true));
            return CommandResult.success();
        }
        String[] msg = gym.Rules.split("/n");
        for (String s : msg) {
            src.sendMessage(Utils.toText("&7" + s, true));
        }

        return CommandResult.success();
    }

}
