package agp.andwhat5.commands.gyms;

import agp.andwhat5.AGP;
import agp.andwhat5.commands.Command;
import agp.andwhat5.Utils;
import agp.andwhat5.config.structs.GymStruc;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import java.util.List;

import com.google.common.collect.Lists;

public class DeleteGym extends Command
{

	public DeleteGym()
	{
		super("delgym", "/delgym <gym>");
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (args.length == 1)
		{
			if (Utils.gymExists(args[0]))
			{
				GymStruc gs = Utils.getGym(args[0]);
				Utils.removeGym(gs);
				AGP.getInstance().getStorage().updateAllBadges(gs);
				Utils.saveAGPData();
				sender.sendMessage(Utils.toText("&7Successfully deleted the &b" + gs.Name + " &7Gym!", true));
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
