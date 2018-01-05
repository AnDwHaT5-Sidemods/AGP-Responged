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

public class DenyChallenge extends Command
{

	public DenyChallenge()
	{
		super("denychallenge", "/denychallenge <gym> <opt-challenger>");
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
					if (!Utils.getQueuedPlayers(gs).isEmpty())
					{
						if (args.length == 1)
						{
							String pName = gs.Queue.poll();
							sender.sendMessage(Utils.toText("&7Challenger &b" + pName + " &7has been removed from the &b" + gs.Name + " &7Gym queue!", true));
							EntityPlayerMP player = requireEntityPlayer(pName);
							if (player != null)
							{
								player.sendMessage(Utils.toText("&7Your challenge to the &b" + gs.Name + " &7Gym " +
																		 "was denied!", true));
							}
						} else
						{
							EntityPlayerMP player = requireEntityPlayer(args[1]);
							String pName = player != null ? player.getName() : args[1];
							if (gs.Queue.contains(pName))
							{
								gs.Queue.remove(pName);
								sender.sendMessage(Utils.toText("&7Challenger &b" + pName + " &7has been removed from the &b" + gs.Name + " &7Gym queue!", true));
								if (player != null)
								{
									player.sendMessage(Utils.toText("&7Your challenge to the &b" + gs.Name + " " +
																			 "&7Gym was denied!", true));
								}
							} else
							{
								sender.sendMessage(Utils.toText("&7Challenger &b" + pName + " &7is not in the &b" + gs.Name + " &7Gym queue!", true));
							}
						}
					} else
					{
						sender.sendMessage(Utils.toText("&7The &b" + gs.Name + " &7Gym's queue is empty!", true));
					}
				} else
				{
					sender.sendMessage(Utils.toText("&7You are not a leader of the &b" + gs.Name + " &7Gym!", true));
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
	public List<String> getAliases() {
		return Lists.newArrayList("dc");
	}
	
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
	{
		if (args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, Utils.getGymNames(true));
		} else if (args.length == 2 && Utils.gymExists(args[0]))
		{
			return getListOfStringsMatchingLastWord(args, Utils.getGym(args[0]).Queue);
		}
		return null;
	}
}
