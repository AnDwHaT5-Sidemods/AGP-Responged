package agp.andwhat5.commands.gyms;

import org.spongepowered.api.command.CommandSource;

import agp.andwhat5.AGP;
import agp.andwhat5.Utils;
import agp.andwhat5.commands.Command;
import agp.andwhat5.config.AGPConfig;
import agp.andwhat5.config.structs.DataStruc;
import agp.andwhat5.config.structs.GymStruc;
import net.minecraft.command.CommandException;
import net.minecraft.server.MinecraftServer;

public class AddGymCommand extends Command{

	public AddGymCommand() {
		super("/AddGymCommand Gym Command");
	}

	@Override
	public void execute(MinecraftServer server, CommandSource sender, String[] args) throws CommandException {
		if(args.length >= 2)
		{
			if(Utils.gymExists(args[0]))
			{
				String command = "";
				for(int i = 1; i < args.length; i++)
				{
					if(i == args.length - 1)
						command += args[i];
					else
						command += args[i] + " ";
				}
				GymStruc gym = Utils.getGym(args[0]);
				gym.Commands.add(command);
		        if (AGPConfig.Storage.storageType.equalsIgnoreCase("flatfile")) {
		            Utils.editGym(gym);
		            AGP.getInstance().getStorage().saveData(DataStruc.gcon);
		        } else {
		            Utils.addGym(gym);
		        }
		        sender.sendMessage(Utils.toText("&7You have successfully added that command as a reward for this gym.", true));
			}
			else
			{
				sender.sendMessage(Utils.toText("&7You have specified an incorrect gym.", true));
				return;
			}
		}
		else
		{
			sender.sendMessage(Utils.toText("&7Incorrect usage: &b/AddGymCommand Gym Command&7.", true));
		}
		
	}
}
