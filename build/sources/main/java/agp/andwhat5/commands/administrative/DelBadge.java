package agp.andwhat5.commands.administrative;

import java.util.List;

import com.google.common.collect.Lists;

import agp.andwhat5.AGP;
import agp.andwhat5.commands.Command;
import agp.andwhat5.Utils;
import agp.andwhat5.config.structs.BadgeStruc;
import agp.andwhat5.config.structs.DataStruc;
import agp.andwhat5.config.structs.GymStruc;
import agp.andwhat5.config.structs.PlayerStruc;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class DelBadge extends Command
{
	public DelBadge()
	{
		super("delbadge", "/delbadge <player> <gym>");
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (args.length == 2)
		{
			if (Utils.gymExists(args[1]))
			{
				GymStruc gs = Utils.getGym(args[1]);
				EntityPlayerMP player = requireEntityPlayer(args[0]);
				PlayerStruc ps = DataStruc.gcon.PlayerData.getOrDefault(player.getUniqueID(), null);
				if (ps != null)
				{
					if (ps.Badges.stream().anyMatch(b -> b.Gym.equalsIgnoreCase(gs.Name)))
					{
						BadgeStruc bs = ps.Badges.stream().filter(b -> b.Gym.equalsIgnoreCase(gs.Name)).findAny().get();
						AGP.getInstance().getStorage().updateObtainedBadges(player.getUniqueID(), player.getName(), bs, false);
						AGP.getInstance().getStorage().saveData(DataStruc.gcon);
						sender.sendMessage(Utils.toText("&7Successfully removed &b" + player.getName() + "&7's &b" + gs.Name + " &7badge!", true));
						player.sendMessage(Utils.toText("&b" + sender.getName() + " &7has taken away your &b"
																  + gs.Name + " &7badge!", true));
					} else
					{
						sender.sendMessage(Utils.toText("&b" + player.getName() + " &7does not have the &b" + gs.Name + " &7badge!", true));
					}
				} else
				{
					sender.sendMessage(Utils.toText("&b" + player.getName() + " &7does not have any badges!", true));
				}
			} else
			{
				sender.sendMessage(Utils.toText("&7The &b" + args[1] + " &7Gym does not exist!", true));
			}
		} else
		{
			super.sendUsage(sender);
		}
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
	{
		if (args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
		} else if (args.length == 2)
		{
			return getListOfStringsMatchingLastWord(args, Utils.getGymNames(true));
		}
		return null;
	}
}
