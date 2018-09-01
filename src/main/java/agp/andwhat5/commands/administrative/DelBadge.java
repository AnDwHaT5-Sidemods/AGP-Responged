package agp.andwhat5.commands.administrative;

import agp.andwhat5.AGP;
import agp.andwhat5.Utils;
import agp.andwhat5.commands.Command;
import agp.andwhat5.config.structs.BadgeStruc;
import agp.andwhat5.config.structs.DataStruc;
import agp.andwhat5.config.structs.GymStruc;
import agp.andwhat5.config.structs.PlayerStruc;
import net.minecraft.command.CommandException;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

import java.util.List;

import static net.minecraft.command.CommandBase.getListOfStringsMatchingLastWord;

public class DelBadge extends Command {
    public DelBadge() {
        super("Deletes a badge from a players userfiles.");
    }

    @Override
    public void execute(MinecraftServer server, CommandSource sender, String[] args) throws CommandException {
        if (args.length == 2) {
            String playerName = args[0];
            String gymName = args[1];

            if (!Utils.gymExists(gymName)) {
                sender.sendMessage(Utils.toText("&7The &b" + gymName + " &7Gym does not exist!", true));
                return;
            }

            GymStruc gs = Utils.getGym(gymName);
            Player player = requireEntityPlayer(playerName);
            PlayerStruc ps = DataStruc.gcon.PlayerData.getOrDefault(player.getUniqueId(), null);

            if (ps == null) {
                sender.sendMessage(Utils.toText("&b" + player.getName() + " &7does not have any badges!", true));
                return;
            }

            if (!ps.Badges.stream().anyMatch(b -> b.Gym.equalsIgnoreCase(gs.Name))) {
                sender.sendMessage(Utils.toText("&b" + player.getName() + " &7does not have the &b" + gs.Name + " &7badge!", true));
                return;
            }

            BadgeStruc bs = ps.Badges.stream().filter(b -> b.Gym.equalsIgnoreCase(gs.Name)).findAny().get();
            AGP.getInstance().getStorage().updateObtainedBadges(player.getUniqueId(), player.getName(), bs, false);
            AGP.getInstance().getStorage().saveData(DataStruc.gcon);
            sender.sendMessage(Utils.toText("&7Successfully removed &b" + player.getName() + "&7's &b" + gs.Name + " &7badge!", true));
            player.sendMessage(Utils.toText("&b" + sender.getName() + " &7has taken away your &b" + gs.Name + " &7badge!", true));

        } else {
            sender.sendMessage(Utils.toText("&7Incorrect Usage: &b/DelBadge <player> <gym>&7.", true));
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, CommandSource sender, String[] args) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        } else if (args.length == 2) {
            return getListOfStringsMatchingLastWord(args, Utils.getGymNames(true));
        }
        return null;
    }


}
