package agp.andwhat5.commands.players;

import agp.andwhat5.commands.Command;
import agp.andwhat5.Utils;
import agp.andwhat5.config.structs.ArenaStruc;
import agp.andwhat5.config.structs.GymStruc;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

public class GymWarp extends Command
{

	public GymWarp()
	{
		super("gymwarp", "/gymwarp <gym> <lobby|home|arena> [(if arena) <name> <stands|challenger|leader>]");
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		EntityPlayerMP user = requireEntityPlayer(sender);
		String loc = args.length >= 2 ? args[1] : "lobby";
		if (args.length == 1 || args.length == 2 || args.length == 4)
		{
			if (Utils.gymExists(args[0]))
			{
				GymStruc gs = Utils.getGym(args[0]);
				switch (loc.toLowerCase())
				{
					case "lobby":
						if (gs.Lobby != null)
						{
							Utils.setPosition(user, gs.Lobby);
							sender.sendMessage(Utils.toText("&7Teleported to the &b" + gs.Name + " &7Gym lobby!", true));
						} else
						{
							sender.sendMessage(Utils.toText("&7The &b" + gs.Name + " &7Gym's lobby has not been set!", true));
						}
						break;
					case "arena":
						ArenaStruc as;
						if(args.length > 2)
						{
							if ((as = Utils.getArena(gs, args[2])) != null)
							{
								switch (args[3].toLowerCase())
								{
									case "stands":
										if (as.Stands != null)
										{
											Utils.setPosition(user, as.Stands);
											sender.sendMessage(Utils.toText("&7Teleported to the &b" + gs.Name + " &7Gym's &b" + as.Name + " stands&7!", true));
										}
										else
										{
											sender.sendMessage(Utils.toText("&7The &b" + gs.Name + " &7Gym's &b" + as.Name + " &7stands has not been set!", true));
										}
										break;
									case "challenger":
										if (as.Challenger != null)
										{
											Utils.setPosition(user, as.Challenger);
											sender.sendMessage(Utils.toText("&7Teleported to the &b" + gs.Name + " &7Gym's &b" + as.Name + " &7challenger stage!", true));
										}
										else
										{
											sender.sendMessage(Utils.toText("&7The &b" + gs.Name + " &7Gym's &b" + as.Name + " &7challenger stage has not been set!", true));
										}
										break;
									case "leader":
										if (as.Leader != null)
										{
											Utils.setPosition(user, as.Leader);
											sender.sendMessage(Utils.toText("&7Teleported to the &b" + gs.Name + " &7Gym's &b" + as.Name + " &7leader stage!", true));
										}
										else
										{
											sender.sendMessage(Utils.toText("&7The &b" + gs.Name + " &7Gym's &b" + as.Name + " &7leader stage has not been set!", true));
										}
										break;
									default:
										sender.sendMessage(Utils.toText("&7The location &b" + args[3] + " &7was not recognized!", true));
								}
							}
							else
							{
								sender.sendMessage(Utils.toText("&7The Arena &b" + args[2] + " &7could not be found!", true));
							}
						} else {
							sender.sendMessage(Utils.toText("&7You must specify a valid arena teleport location",
															   true));
						}

						break;
					default:
						sender.sendMessage(Utils.toText("&7The location &b" + args[1] + " &7was not recognized!", true));
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
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
	{
		if (args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, Utils.getGymNames(true));
		} else if (args.length == 2)
		{
			if (Utils.gymExists(args[0]) && Utils.isGymLeader((EntityPlayerMP) sender, Utils.getGym(args[1])))
			{
				return getListOfStringsMatchingLastWord(args, Arrays.asList("arena", "home", "lobby"));
			} else
			{
				return getListOfStringsMatchingLastWord(args, Arrays.asList("arena", "lobby"));
			}
		} else if (args.length == 3 && args[1].equalsIgnoreCase("arena") && Utils.gymExists(args[0]))
		{
			return getListOfStringsMatchingLastWord(args, Utils.getArenaNames(Utils.getGym(args[0]), true));
		} else if (args.length == 4)
		{
			if (Utils.gymExists(args[0]) && Utils.isGymLeader((EntityPlayerMP) sender, Utils.getGym(args[1])))
			{
				return getListOfStringsMatchingLastWord(args, Arrays.asList("challenger", "leader", "stands"));
			} else
			{
				return getListOfStringsMatchingLastWord(args, Arrays.asList("stands"));
			}
		}
		return null;
	}
}
