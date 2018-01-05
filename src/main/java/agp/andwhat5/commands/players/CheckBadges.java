package agp.andwhat5.commands.players;

import agp.andwhat5.commands.Command;
import agp.andwhat5.Utils;
import agp.andwhat5.ui.EnumGUIType;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import java.util.List;

import com.google.common.collect.Lists;

public class CheckBadges extends Command
{
	public CheckBadges()
	{
		super("checkbadges", "/checkbadges <opt-player>", 0);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		EntityPlayerMP user = requireEntityPlayer(sender);
		if (args.length == 0)
		{
			Utils.openGUI(user, user, EnumGUIType.CheckBadges);
		} else if (args.length == 1)
		{
			if (sender.canUseCommand(4, "agp.checkbadges.other") || Utils.isAnyLeader(user) || sender.canUseCommand(2, "agp.headleader"))
			{
				EntityPlayerMP player = requireEntityPlayer(args[0]);
				Utils.openGUI(player, user, EnumGUIType.CheckBadges);
			} else
			{
				sender.sendMessage(Utils.toText("&7You don't have permission to access another player's badges!", true));
			}
		} else
		{
			super.sendUsage(sender);
		}
	}

	@Override
	public List<String> getAliases() {
		return Lists.newArrayList("cb", "badges");
	}
	
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
	{
		if (args.length == 1 && (Utils.isAnyLeader((EntityPlayerMP) sender) || sender.canUseCommand(4, "agp.headleader") || sender.canUseCommand(1, "agp.checkbadges.other")))
		{
			return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
		}
		return null;
	}
}
