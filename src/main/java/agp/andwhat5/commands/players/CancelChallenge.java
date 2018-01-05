package agp.andwhat5.commands.players;

import agp.andwhat5.commands.Command;

import java.util.List;

import com.google.common.collect.Lists;

import agp.andwhat5.Utils;
import agp.andwhat5.config.structs.GymStruc;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CancelChallenge extends Command
{

	public CancelChallenge()
	{
		super("cancelchallenge", "/cancelchallenge");
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		EntityPlayerMP user = Command.requireEntityPlayer(sender);
		if (Utils.isInAnyQueue(user))
		{
			for (GymStruc gs : Utils.getGymStrucs(false))
			{
				if (gs.Queue.contains(user.getName()))
				{
					gs.Queue.remove(user.getName());
					sender.sendMessage(Utils.toText("&7Canceled your challenge to the &b" + gs.Name + " &7Gym!", true));
					return;
				}
			}
			sender.sendMessage(Utils.toText("&7Your challenge couldn't be found please show this to AGP developers!", true));
		} else
		{
			sender.sendMessage(Utils.toText("&7You are not currently challenging any gyms!", true));
		}
	}
	@Override
	public List<String> getAliases() {
		return Lists.newArrayList("cc");
	}
}
