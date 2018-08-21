package agp.andwhat5.commands.players;

import agp.andwhat5.Utils;
import agp.andwhat5.commands.Command;
import agp.andwhat5.ui.EnumGUIType;
import net.minecraft.command.CommandException;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

import java.util.List;

import static net.minecraft.command.CommandBase.getListOfStringsMatchingLastWord;

public class CheckBadges extends Command {
    public CheckBadges() {
        super("/checkbadges <opt-player>");
    }

    @Override
    public void execute(MinecraftServer server, CommandSource sender, String[] args) throws CommandException {
        Player user = requireEntityPlayer(sender);
        if (args.length == 0) {
            Utils.openGUI(user, user, EnumGUIType.CheckBadges);
        } else if (args.length == 1) {
            if (sender.hasPermission("agp.checkbadges.other") || Utils.isAnyLeader(user) || sender.hasPermission("agp.headleader")) {
                Player player = requireEntityPlayer(args[0]);
                Utils.openGUI(player, user, EnumGUIType.CheckBadges);
            } else {
                sender.sendMessage(Utils.toText("&7You don't have permission to access another player's badges!", true));
            }
        } else {
            super.sendUsage(sender);
        }
    }


    @Override
    public List<String> getTabCompletions(MinecraftServer server, CommandSource sender, String[] args) {
        if (args.length == 1 && (Utils.isAnyLeader((Player) sender) || sender.hasPermission("agp.headleader") || sender.hasPermission("agp.checkbadges.other"))) {
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        }
        return null;
    }

}
