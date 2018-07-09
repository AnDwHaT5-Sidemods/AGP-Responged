package agp.andwhat5.commands.administrative;

import java.util.List;
import java.util.Optional;

import agp.andwhat5.commands.Command;
import agp.andwhat5.Utils;
import agp.andwhat5.config.AGPConfig;
import agp.andwhat5.config.structs.GymStruc;

import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.entities.pixelmon.drops.DropItemHelper;
import com.pixelmonmod.pixelmon.storage.PixelmonStorage;
import com.pixelmonmod.pixelmon.storage.PlayerStorage;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class GiveBadge extends Command
{

	public GiveBadge()
	{
		super("givebadge", "/givebadge <player> <gym>", 2);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (args.length == 2)
		{
			if (Utils.gymExists(args[1]))
			{
				GymStruc gs = Utils.getGym(args[1]);
				if (Utils.isGymLeader((EntityPlayerMP) sender, gs) || sender.canUseCommand(2, "agp.headleader"))
				{
					EntityPlayerMP player = requireEntityPlayer(args[0]);
					Utils.getPlayerData(player);
					if (!Utils.hasBadge(player, gs))
					{
						Utils.giveBadge(player, gs, sender.getName());
						if (gs.Money != 0)
						{
							Utils.addCurrency(player, gs.Money);
						}
						if (AGPConfig.General.physicalBadge)
						{
							ItemStack item = new ItemStack(getItemByText(sender, gs.Badge), 1);
							DropItemHelper.giveItemStackToPlayer(player, item);
						}
						if (!gs.Items.get(0).equals("null"))
						{
							ItemStack item1 = new ItemStack(getItemByText(sender, gs.Items.get(0)), 1);
							DropItemHelper.giveItemStackToPlayer(player, item1);
						}
						if (!gs.Items.get(1).equals("null"))
						{
							ItemStack item2 = new ItemStack(getItemByText(sender, gs.Items.get(1)), 1);
							DropItemHelper.giveItemStackToPlayer(player, item2);
						}

						sender.sendMessage(Utils.toText("&7Successfully gave &b" + player.getName() + " &7the &b" + gs.Name + " &7Gym's badge!", true));
						sender.sendMessage(Utils.toText("&7Congratulations, you defeated the &b" + gs.Name + " &7Gym!", true));
						if (AGPConfig.Announcements.winAnnouncement)
						{
							for(EntityPlayerMP pl : Utils.getAllPlayers())
								pl.sendMessage(Utils.toText(AGPConfig.Announcements.winMessage
									.replace("{gym}", gs.Name).replace("{challenger}", player.getName()).replace("{leader}", sender.getName()), false));
						}
					} else
					{
						sender.sendMessage(Utils.toText("&b" + player.getName() + " &7has already beaten the &b" + gs.Name + " &7Gym!", true));
					}

				} else
				{
					sender.sendMessage(Utils.toText("&7You must be a leader of the &b" + gs.Name + " &7Gym to give its badge!", true));
				}

			} else
			{
				sender.sendMessage(Utils.toText("&7The &b" + args[1] + " &7Gym does not exist!", true));
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
			return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
		} else if (args.length == 2)
		{
			return getListOfStringsMatchingLastWord(args, Utils.getGymNames(true));
		}
		return null;
	}
}
