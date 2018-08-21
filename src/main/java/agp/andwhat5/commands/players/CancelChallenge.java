package agp.andwhat5.commands.players;

import agp.andwhat5.Utils;
import agp.andwhat5.commands.Command;
import agp.andwhat5.config.structs.GymStruc;
import net.minecraft.command.CommandException;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

public class CancelChallenge extends Command {

    public CancelChallenge() {
        super("/cancelchallenge");
    }

    @Override
    public void execute(MinecraftServer server, CommandSource sender, String[] args) throws CommandException {
        Player user = Command.requireEntityPlayer(sender);
        if (!Utils.isInAnyQueue(user)) {
            sender.sendMessage(Utils.toText("&7You are not currently challenging any gyms!", true));
            return;
        }

        for (GymStruc gs : Utils.getGymStrucs(false)) {
            if (gs.Queue.contains(user.getUniqueId())) {
                gs.Queue.remove(user.getUniqueId());
                sender.sendMessage(Utils.toText("&7Canceled your challenge to the &b" + gs.Name + " &7Gym!", true));
                return;
            }
        }

        sender.sendMessage(Utils.toText("&7Your challenge couldn't be found please show this to AGP developers!", true));
    }

}
