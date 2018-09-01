package agp.andwhat5.commands.gyms;

import org.spongepowered.api.command.CommandSource;

import agp.andwhat5.Utils;
import agp.andwhat5.commands.Command;
import agp.andwhat5.config.structs.GymStruc;
import net.minecraft.command.CommandException;
import net.minecraft.server.MinecraftServer;

public class ListGymCommands extends Command{

	public ListGymCommands() {
		super("Displays all of the commands in the gyms rewards pool.");
	}
	
	@Override
	public void execute(MinecraftServer server, CommandSource sender, String[] args) throws CommandException {
		if(args.length == 1)
		{
			if(Utils.gymExists(args[0]))
			{
				GymStruc gym = Utils.getGym(args[0]);
				sender.sendMessage(Utils.toText("&7This gyms commands are as follows:", true));
				gym.Commands.stream().forEach(c -> sender.sendMessage(Utils.toText("&b" + c, true)));
				return;
			}
			else
			{
				sender.sendMessage(Utils.toText("&7This gym does not exist.", true));
				return;
			}
		}
		else
		{
			sender.sendMessage(Utils.toText("&7Incorrect usage: &b/ListGymCommands <gym>&7.", true));
			return;
		}
	}
}
