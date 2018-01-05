package agp.andwhat5.commands.gyms;

import agp.andwhat5.commands.Command;
import agp.andwhat5.Utils;
import agp.andwhat5.config.structs.GymStruc;
import agp.andwhat5.ui.EnumGUIType;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

public class GymList extends Command
{

	public GymList()
	{
		super("gymlist", "/gymlist <opt-(-nogui)", 0);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (args.length == 0)
		{
			Utils.openGUI(requireEntityPlayer(sender), requireEntityPlayer(sender), EnumGUIType.GymList);
		} else if (args.length == 1 && args[0].equalsIgnoreCase("-nogui"))
		{
			sender.sendMessage(Utils.toText("&f--==[&dAGP - Gyms List&f]==--", false));
			sender.sendMessage(Utils.toText("&bGyms: &7(&aOpen&7) &7(&cClosed&7)", false));
			sender.sendMessage(Utils.toText("&bLeaders: &7(&aOnline&7) &7(&eNPC&7) &7(&cOffline&7)", false));
			sender.sendMessage(Utils.toText("", false));
			for (GymStruc gs : Utils.getGymStrucs(true))
			{
				StringBuilder msg = new StringBuilder((gs.Status == 0 ? "&a" : gs.Status == 1 ? "&c" : "&e") + gs.Name +
						"&7[&f");
				msg.append(gs.LevelCap == 0 ? "No Cap" : "lvl" + gs.LevelCap).append("&7]&8: ");
				boolean foundNPC = false;
				if (gs.Leaders.isEmpty())
				{
					msg.append("&8No leaders");
				} else
				{
					for (int l = 0; l < gs.Leaders.size(); l++)
					{
						if (gs.Leaders.get(l).equalsIgnoreCase("NPC"))
						{
							foundNPC = true;
						} else
						{
							msg.append(gs.OnlineLeaders.contains(gs.Leaders.get(l)) ? "&a" : "&c").append(gs.Leaders.get(l));
						}
						msg.append(gs.Status == 0 ? "&a" : gs.Status == 1 ? "&c" : "&e").append(l == gs.Leaders.size()
								- 1 ? (foundNPC ? "&eNPC" : "") : ", ");
					}
				}
				sender.sendMessage(Utils.toText(msg.toString(), false));
			}
		} else
		{
			super.sendUsage(sender);
		}
	}

	@Override
	public List<String> getAliases() {
		return Lists.newArrayList("gl", "gyms");
	}
	
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
	{
		if (args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, Arrays.asList("-nogui"));
		}
		return null;
	}
}
