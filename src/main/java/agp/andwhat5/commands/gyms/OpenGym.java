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

public class OpenGym extends Command
{

	public OpenGym()
	{
		super("opengym", "/opengym <gym>");
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (args.length == 1)
		{
			if (Utils.gymExists(args[0]))
			{
				GymStruc gs = Utils.getGym(args[0]);
				if (Utils.isGymLeader((EntityPlayerMP) sender, gs) || sender.canUseCommand(2, "agp.headleader"))
				{
					if (gs.Status != 0)
					{
						gs.Status = 0;
						sender.sendMessage(Utils.toText("&7Successfully opened the &b" + gs.Name + " &7Gym!", true));
						if (AGPConfig.Announcements.openAnnouncement)
						{
							for(EntityPlayerMP player : Utils.getAllPlayers())
								player.sendMessage(Utils.toText(AGPConfig.Announcements.openMessage
									.replace("{gym}", gs.Name).replace("{leader}", sender.getName()), false));
						}
					} else
					{
						sender.sendMessage(Utils.toText("&7The &b" + gs.Name + " &7Gym is already open!", true));
					}
				} else
				{
					sender.sendMessage(Utils.toText("&7You are not a leader of the &b" + gs.Name + " &7Gym!", true));
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
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
	{
		if (args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, Utils.getGymNames(true));
		}
		return null;
	}
}
