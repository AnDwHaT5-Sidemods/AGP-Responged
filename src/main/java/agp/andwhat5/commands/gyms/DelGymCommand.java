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

public class DelGymCommand extends Command{

	public DelGymCommand() {
		super("/DelGymCommand Gym Command");
	}

	@Override
	public void execute(MinecraftServer server, CommandSource sender, String[] args) throws CommandException {
		if(args.length >= 2)
		{
			if(Utils.gymExists(args[0]))
			{
				GymStruc gym = Utils.getGym(args[0]);
				String command = "";
				for(int i = 1; i < args.length; i++)
				{
					command += args[i] += " ";
				}
				if(gym.Commands.contains(command))
				{
					gym.Commands.remove(command);
					sender.sendMessage(Utils.toText("&7Successfully removed that command.", true));
			        if (AGPConfig.Storage.storageType.equalsIgnoreCase("flatfile")) {
			            Utils.editGym(gym);
			            AGP.getInstance().getStorage().saveData(DataStruc.gcon);
			        } else {
			            Utils.addGym(gym);
			        }
			        return;
				}
				else
				{
					sender.sendMessage(Utils.toText("&7That command is not in this gyms rewards pool.", true));
					return;
				}
			}
		}
		else
		{
			sender.sendMessage(Utils.toText("&7Incorrect usage: &b/DelGymCommand Gym Command&7.", true));
			return;
		}
		
	}

}
