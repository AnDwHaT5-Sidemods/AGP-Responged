package agp.andwhat5.commands.leaders;

import agp.andwhat5.commands.Command;
import agp.andwhat5.Utils;
import agp.andwhat5.config.structs.ArenaStruc;
import agp.andwhat5.config.structs.BattleStruc;
import agp.andwhat5.config.structs.DataStruc;
import agp.andwhat5.config.structs.GymStruc;

import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;
import com.pixelmonmod.pixelmon.battles.controller.BattleControllerBase;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.storage.PixelmonStorage;
import com.pixelmonmod.pixelmon.storage.PlayerStorage;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Optional;

public class AcceptChallenge extends Command
{
	public AcceptChallenge()
	{
		super("acceptchallenge", "/acceptchallenge <gym> <opt-arena>");
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		EntityPlayerMP user = requireEntityPlayer(sender);
		if (args.length == 1 || args.length == 2)
		{
			if (Utils.gymExists(args[0]))
			{
				GymStruc gs = Utils.getGym(args[0]);
				if (Utils.isGymLeader(user, gs) || sender.canUseCommand(2, "agp.headleader"))
				{
					if (!gs.Queue.isEmpty())
					{
						if (Utils.checkLevels(user, gs.LevelCap))
						{
							String cName = gs.Queue.poll();
							EntityPlayerMP challenger = getEntityPlayer(cName);
							if (challenger != null)
							{
								if (Utils.checkLevels(challenger, gs.LevelCap))
								{
									if (BattleRegistry.getBattle(challenger) != null)
									{
										BattleControllerBase battlecontroller = BattleRegistry.getBattle(challenger);
										battlecontroller.endBattle();
									}
									if (BattleRegistry.getBattle(user) != null)
									{
										BattleControllerBase battlecontroller = BattleRegistry.getBattle(user);
										battlecontroller.endBattle();
									}
									ArenaStruc as = args.length == 2 ? Utils.getArena(gs, args[1]) : (gs.Arenas.size() > 0 ? gs.Arenas.get(0) : null);
									if (as != null && as.Leader!= null && as.Challenger != null)
									{
										Utils.setPosition((EntityPlayerMP) sender, as.Leader);
										Utils.setPosition(challenger, as.Challenger);
									} else if (args.length == 2) {
										user.sendMessage(Utils.toText("&7The &b" + args[2] + " &7Arena has not " +
																				"been set up!", true));
									}

									Optional<PlayerStorage> leaderTeam = PixelmonStorage.pokeBallManager.getPlayerStorage(user);
									Optional<PlayerStorage> challengerTeam = PixelmonStorage.pokeBallManager.getPlayerStorage(challenger);

									if (leaderTeam.isPresent() && challengerTeam.isPresent())
									{
										leaderTeam.get().healAllPokemon(user.getEntityWorld());
										challengerTeam.get().healAllPokemon(challenger.getEntityWorld());

										BattleStruc bs = new BattleStruc(gs, user, challenger);
										DataStruc.gcon.GymBattlers.add(bs);
										user.sendMessage(Utils.toText("&7Initiating battle against &b" + cName +
																			 "&7!", true));
										challenger.sendMessage(Utils.toText("&7Gym Leader &b" + user.getName() + " &7has " +
																			 "accepted your challenge against the &b" + gs.Name + " &bGym!", true));

										EntityPixelmon leaderPkmn1 = leaderTeam.get().getFirstAblePokemon(user.getEntityWorld());
										BattleParticipant ldr = new PlayerParticipant(user, leaderPkmn1);
										EntityPixelmon challengerPkmn1 = challengerTeam.get().getFirstAblePokemon(challenger.getEntityWorld());
										BattleParticipant chal = new PlayerParticipant(challenger, challengerPkmn1);

										ldr.startedBattle = true;
										BattleParticipant[] team1 = new BattleParticipant[]{ldr};
										BattleParticipant[] team2 = new BattleParticipant[]{chal};
										new BattleControllerBase(team1, team2);
									} else
									{
										user.sendMessage(Utils.toText("&7An error occurred starting the gym battle!",
																		 true));
										challenger.sendMessage(Utils.toText("&7An error occurred starting the gym battle!",
																			   true));
									}
								} else
								{
									sender.sendMessage(Utils.toText("&7Player &b" + cName + "&7's team is above the level cap for the &b" + gs.Name + " &7Gym!", true));
									challenger.sendMessage(Utils.toText("&7Your team is above the level cap for the &b" +
																		 gs.Name + " &7Gym!", true));
								}
							} else
							{
								sender.sendMessage(Utils.toText("&7Player &b" + cName + " &7was not found on the server!", true));
							}
						} else
						{
							sender.sendMessage(Utils.toText("&7Your team is above the level cap for the &b" + gs.Name + " &7Gym!", true));
						}
					} else
					{
						sender.sendMessage(Utils.toText("&7The &b" + gs.Name + " &7Gym queue is empty!", true));
					}
				} else
				{
					sender.sendMessage(Utils.toText("&7You are not a leader of the &b" + gs.Name + " &7Gym!", true));
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
		return Lists.newArrayList("ac");
	}
	
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
	{
		if (args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, Utils.getGymNames(true));
		} else if (args.length == 2 && Utils.gymExists(args[0]))
		{
			return getListOfStringsMatchingLastWord(args, Utils.getArenaNames(Utils.getGym(args[0]), true));
		}
		return null;
	}
}
