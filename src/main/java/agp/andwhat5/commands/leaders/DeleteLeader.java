package agp.andwhat5.commands.leaders;

import java.util.List;

import com.google.common.collect.Lists;

import agp.andwhat5.commands.Command;
import agp.andwhat5.Utils;
import agp.andwhat5.config.structs.GymStruc;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class DeleteLeader extends Command
{
	public DeleteLeader()
	{
		super("delleader", "/delleader <player> <gym>");
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (args.length == 2)
		{
			if (Utils.gymExists(args[1]))
			{
				GymStruc gs = Utils.getGym(args[1]);
				if(args[0].equalsIgnoreCase("npc"))
				{
					gs.Leaders.remove("npc");
					Utils.editGym(gs);
					Utils.saveAGPData();
					sender.sendMessage(Utils.toText("&7Successfully removed the &bNPC &7as a leader of the &b" + gs.Name + " &7Gym!", true));
					return;
				}
				EntityPlayerMP player = requireEntityPlayer(args[0]);
				String pName = player != null ? player.getName() : args[0];
				if (gs.Leaders.stream().anyMatch(l -> l.equalsIgnoreCase(args[0])))
				{
					gs.Leaders.remove(gs.Leaders.stream().filter(p -> p.equalsIgnoreCase(args[0])).findAny().get());
					Utils.editGym(gs);
					Utils.saveAGPData();
					sender.sendMessage(Utils.toText("&7Successfully removed &b" + pName + " &7as a leader of the &b" + gs.Name + " &7Gym!", true));
					if (player != null)
					{
						Utils.getGym(gs.Name).OnlineLeaders.remove(player.getName());
						player.sendMessage(Utils.toText("&7You are no longer a leader of the &b" + gs.Name + " " +
																 "&7Gym!", true));
					}
				} else
				{
					sender.sendMessage(Utils.toText("&b" + pName + " &7is not a leader of the &b" + gs.Name + " &7Gym!", true));
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
