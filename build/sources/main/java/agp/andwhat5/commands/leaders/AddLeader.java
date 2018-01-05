package agp.andwhat5.commands.leaders;

import agp.andwhat5.commands.Command;
import agp.andwhat5.Utils;
import agp.andwhat5.config.AGPConfig;
import agp.andwhat5.config.structs.DataStruc;
import agp.andwhat5.config.structs.GymStruc;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import java.util.List;

import com.google.common.collect.Lists;

public class AddLeader extends Command
{
	public AddLeader()
	{
		super("addleader", "/addleader <player> <gym|all>");
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (args.length == 2)
		{
			EntityPlayerMP player;
			try
			{
				player = requireEntityPlayer(args[0]);
			} catch (CommandException e){
				player = null;
			}
			String pName = player != null ? player.getName() : args[0];
			if (Utils.gymExists(args[1]))
			{
				GymStruc gs = Utils.getGym(args[1]);
				if (gs.Leaders.stream().noneMatch(l -> l.equalsIgnoreCase(pName)))
				{
					Utils.addLeader(pName, gs);
					if(AGPConfig.General.autoOpen)
					{
						gs.Status = 0;
					}
					Utils.saveAGPData();
					sender.sendMessage(Utils.toText("&7Successfully made &b" + pName + " &7a leader of the &b" + gs.Name + " &7Gym!", true));
					if (player != null)
					{
						Utils.getGym(gs.Name).OnlineLeaders.add(pName);
						player.sendMessage(Utils.toText("&7You are now a leader of the &b" + gs.Name + " &7Gym!",
														   true));

						for(EntityPlayerMP pl : Utils.getAllPlayers())
							pl.sendMessage(Utils.toText("&b" + pName + " &7is now a " +
																					   "leader of " +
																		   "the &b" + gs.Name + " &7Gym!", true));
					}
				} else
				{
					sender.sendMessage(Utils.toText("&b" + pName + " &7is already a leader of the &b" + gs.Name + " &7Gym!", true));
				}
			} else if (args[1].equalsIgnoreCase("all"))
			{
				List<GymStruc> gyms = DataStruc.gcon.GymData;
				final EntityPlayerMP pl = player;
				gyms.forEach(gs -> {
					if (gs.Leaders.stream().noneMatch(l -> l.equalsIgnoreCase(args[0])))
					{
						if (pl != null)
						{
							gs.Leaders.add(pl.getName());
						} else
						{
							gs.Leaders.add(args[0]);
						}
					}
				});

				Utils.sortGyms();
				Utils.saveAGPData();
				sender.sendMessage(Utils.toText("&7Successfully made &b" + pName + " &7a leader of all Gyms!", true));
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
