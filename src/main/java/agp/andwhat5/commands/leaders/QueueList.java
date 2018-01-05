package agp.andwhat5.commands.leaders;

import agp.andwhat5.commands.Command;
import agp.andwhat5.Utils;
import agp.andwhat5.config.structs.GymStruc;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import java.util.List;

import com.google.common.collect.Lists;

public class QueueList extends Command
{

	public QueueList()
	{
		super("queuelist", "/queuelist <gym> <opt-player>");
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (args.length == 1 || args.length == 2)
		{
			if (Utils.gymExists(args[0]))
			{
				GymStruc gs = Utils.getGym(args[0]);
				if (Utils.isGymLeader((EntityPlayerMP) sender, gs) || sender.canUseCommand(2, "agp.headleader"))
				{
					if (args.length == 1)
					{
						sender.sendMessage(Utils.toText("&f--==[&dAGP - " + gs.Name + " Queue&f]==--", false));
						sender.sendMessage(Utils.toText("&bChallengers: &7(&aOnline&7) &7(&cOffline&7)", false));
						sender.sendMessage(Utils.toText("", false));
						int i = 0;
						if (gs.Queue.isEmpty())
						{
							sender.sendMessage(Utils.toText("&7There are no challengers in the queue!", false));
						} else
						{
							for (String str : gs.Queue)
							{
								sender.sendMessage(Utils.toText("&b" + ++i + ": " + (getEntityPlayer(str) != null ? "&a" : "&c") + str, false));
							}
						}
					} else
					{
						EntityPlayerMP player = getEntityPlayer(args[1]);
						String pName = player != null ? player.getName() : args[1];
						if (gs.Queue.contains(pName))
						{
							sender.sendMessage(Utils.toText("&7Challenger &b" + pName + " &7is currently in the &b" + gs.Name + " &7Gym queue!", true));
						} else
						{
							sender.sendMessage(Utils.toText("&b" + pName + " &7is not in the &b" + gs.Name + " &7Gym queue!", true));
						}
					}
				} else
				{
					sender.sendMessage(Utils.toText("&7You must be a leader of the &b" + gs.Name + " &7Gym to view it's queue!", true));
				}
			} else
			{
				sender.sendMessage(Utils.toText("&7The &b" + args[0] + " &7Gym does not exist!", true));
			}
		} else
		{
			super.sendUsage(sender);
		}
	}

	@Override
	public List<String> getAliases() {
		return Lists.newArrayList("ql");
	}
	
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
	{
		if (args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, Utils.getGymNames(true));
		} else if (args.length == 2)
		{
			return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
		}
		return null;
	}
}
