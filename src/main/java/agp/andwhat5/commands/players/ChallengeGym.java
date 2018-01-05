package agp.andwhat5.commands.players;

import agp.andwhat5.commands.Command;
import agp.andwhat5.Utils;
import agp.andwhat5.config.structs.GymStruc;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import java.util.List;

import com.google.common.collect.Lists;

public class ChallengeGym extends Command
{

	public ChallengeGym()
	{
		super("challengegym", "/challengegym <gym>");
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		EntityPlayerMP user = Command.requireEntityPlayer(sender);
		if (args.length == 1)
		{
			if (Utils.gymExists(args[0]))
			{
				GymStruc gs = Utils.getGym(args[0]);
				if (!Utils.hasBadge(user, gs))
				{
					if (gs.Requirement.isEmpty() || gs.Requirement.equalsIgnoreCase("null") || Utils.hasBadge(user, Utils.getGym(gs.Requirement)))
					{
						if (!Utils.isGymLeader(user, gs))
						{
							if (gs.Status == 0)
							{
								if (!Utils.isInAnyBattle(user))
								{
									if (!Utils.isInAnyQueue(user))
									{
										gs.Queue.add(sender.getName());
										sender.sendMessage(Utils.toText("&7Successfully joined the &b" + gs.Name + " &7Gym queue!", true));
										sender.sendMessage(Utils.toText("&7Your position is &b" + Utils.getQueuedPlayers(gs).size() + "&7.", true));
	
										for(EntityPlayerMP leader : Utils.getGymLeaders(gs))
										leader.sendMessage(Utils.toText("&7Challenger &b" + user.getName() + " &7has joined the &b"
																	+ gs.Name + " &7Gym queue!", true));
									} else
									{
										sender.sendMessage(Utils.toText("&7You may not challenge the &b" + gs.Name + " &7Gym as you are already in a queue!", true));
									}
								} else
								{
									sender.sendMessage(Utils.toText("&7You may not challenge the &b" + gs.Name + " &7Gym as you are currently in a gym battle!", true));
								}
							} else
							{
								sender.sendMessage(Utils.toText("&7You may not challenge the &b" + gs.Name + " &7Gym as it is currently closed!", true));
							}
						} else
						{
							sender.sendMessage(Utils.toText("&7You may not challenge the &b" + gs.Name + " &7Gym as you are it's leader!", true));
						}
					}
					else
					{
						sender.sendMessage(Utils.toText("&7You can not challenge this gym until you have the badge from the &b"+gs.Requirement+" &7gym!", true));
					}
				} else
				{
					sender.sendMessage(Utils.toText("&7You have already beaten the &b" + gs.Name + " &7Gym!", true));
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
	public List<String> getAliases() {
		return Lists.newArrayList("chalgym");
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
