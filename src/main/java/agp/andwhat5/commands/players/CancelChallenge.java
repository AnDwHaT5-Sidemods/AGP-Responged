package agp.andwhat5.commands.players;

import agp.andwhat5.Utils;
import agp.andwhat5.config.structs.GymStruc;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class CancelChallenge implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        if (!(src instanceof Player)) {
            src.sendMessage(Text.of(TextColors.RED, "This command can only be ran by players"));
            return CommandResult.success();
        }

        Player user = (Player) src;
        if (!Utils.isInAnyQueue(user)) {
            user.sendMessage(Utils.toText("&7You are not currently challenging any gyms!", true));
            return CommandResult.success();
        }

        for (GymStruc gs : Utils.getGymStrucs(false)) {
            if (gs.Queue.contains(user.getUniqueId())) {
                gs.Queue.remove(user.getUniqueId());
                user.sendMessage(Utils.toText("&7Canceled your challenge to the &b" + gs.Name + " &7Gym!", true));
                return CommandResult.success();
            }
        }

        user.sendMessage(Utils.toText("&7Your challenge couldn't be found please show this to AGP developers!", true));
        return CommandResult.success();
    }

}
