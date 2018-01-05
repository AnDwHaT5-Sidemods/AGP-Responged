package agp.andwhat5.commands.gyms;

import agp.andwhat5.commands.Command;
import agp.andwhat5.config.AGPConfig;
import agp.andwhat5.Utils;
import agp.andwhat5.config.structs.GymStruc;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import java.util.List;

import com.google.common.collect.Lists;

public class CloseGym extends Command
{

	public CloseGym()
	{
		super("closegym", "/closegym [-f] <gym>");
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (args.length >= 1)
		{
			boolean force = false;
			int gymArg = 0;

			if(args.length == 2 && args[0].equalsIgnoreCase("-f")){
				force = true;
				gymArg = 1;
			}
			if (Utils.gymExists(args[gymArg]))
			{
				GymStruc gs = Utils.getGym(args[gymArg]);
				if (Utils.isGymLeader((EntityPlayerMP) sender, gs) || sender.canUseCommand(2, "agp.headleader"))
				{
					if (gs.Status == 0 || gs.Status == 2)
					{
						if(force)
							gs.Status = 1;
						else
						{
							if(gs.Leaders.stream().anyMatch(l -> l.equalsIgnoreCase("NPC")))
							{
								gs.Status = 2;
							} else
							{
								gs.Status = 1;
							}
						}
						for(EntityPlayerMP queued : Utils.getQueuedPlayers(gs))
							queued.sendMessage(Utils.toText("&7The &b" + gs.Name + " &7Gym is closing! Removing you from the " +
													"queue...", true));
						gs.Queue.clear();
						if (AGPConfig.Announcements.closeAnnouncement)
						{
							for(EntityPlayerMP player : Utils.getAllPlayers())
								player.sendMessage(Utils.toText(AGPConfig.Announcements.closeMessage
									.replace("{gym}", gs.Name)
									.replace("{leader}", sender.getName()), false));
						} else
						{
							for(EntityPlayerMP leader : Utils.getGymLeaders(gs))
								leader.sendMessage(Utils.toText("&7Leader &b" + sender.getName() + " &7has closed " +
																		"the &b" + gs.Name + " &7Gym!", true));
						}
					} else
					{
						sender.sendMessage(Utils.toText("&7The &b" + gs.Name + " &7Gym is not open!", true));
					}
				} else
				{
					sender.sendMessage(Utils.toText("&7You must be a leader of the &b" + gs.Name + " &7Gym to close it!", true));
				}
			} else
			{
				sender.sendMessage(Utils.toText("&7The &b" + args[gymArg] + " &7Gym does not exist!", true));
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
			return getListOfStringsMatchingLastWord(args, Utils.getGymNames(true));
		}
		return null;
	}
}
