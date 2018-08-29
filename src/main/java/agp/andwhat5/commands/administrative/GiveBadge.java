package agp.andwhat5.commands.administrative;

import agp.andwhat5.Utils;
import agp.andwhat5.commands.Command;
import agp.andwhat5.config.AGPConfig;
import agp.andwhat5.config.structs.GymStruc;
import com.pixelmonmod.pixelmon.entities.pixelmon.drops.DropItemHelper;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

import java.util.List;

import static net.minecraft.command.CommandBase.getItemByText;
import static net.minecraft.command.CommandBase.getListOfStringsMatchingLastWord;

public class GiveBadge extends Command {

    public GiveBadge() {
        super("/givebadge <player> <gym>");
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
            if (!Utils.isGymLeader((Player) sender, gs) || !sender.hasPermission("agp.headleader")) {
                sender.sendMessage(Utils.toText("&7You must be a leader of the &b" + gs.Name + " &7Gym to give its badge!", true));
                return;
            }

            Player player = requireEntityPlayer(playerName);

            if (Utils.hasBadge(player, gs)) {
                sender.sendMessage(Utils.toText("&b" + player.getName() + " &7has already beaten the &b" + gs.Name + " &7Gym!", true));
                return;
            }

            Utils.giveBadge(player, gs, sender.getName());
            if (gs.Money != 0) {
                Utils.addCurrency(player, gs.Money);
            }
            if (AGPConfig.General.physicalBadge) {
                ItemStack item = new ItemStack(getItemByText((ICommandSender) sender, gs.Badge), 1);
                DropItemHelper.giveItemStackToPlayer((EntityPlayer) player, item);
            }
            if(!gs.Commands.isEmpty())
            {
            	gs.Commands.stream().forEach(i -> Sponge.getCommandManager().process((CommandSource) Sponge.getServer(), i.trim()));
            }

            sender.sendMessage(Utils.toText("&7Successfully gave &b" + player.getName() + " &7the &b" + gs.Name + " &7Gym's badge!", true));
            sender.sendMessage(Utils.toText("&7Congratulations, you defeated the &b" + gs.Name + " &7Gym!", true));
            if (AGPConfig.Announcements.winAnnouncement) {
                for (Player pl : Utils.getAllPlayers())
                    pl.sendMessage(Utils.toText(AGPConfig.Announcements.winMessage
                            .replace("{gym}", gs.Name).replace("{challenger}", player.getName()).replace("{leader}", sender.getName()), false));
            }


        } else {
            super.sendUsage(sender);

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
