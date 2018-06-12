package agp.andwhat5.listeners;

import agp.andwhat5.commands.Command;
import agp.andwhat5.config.AGPConfig;
import agp.andwhat5.Utils;
import agp.andwhat5.config.structs.BattleStruc;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.events.PlayerBattleEndedAbnormalEvent;
import com.pixelmonmod.pixelmon.api.events.PlayerBattleEndedEvent;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;

import agp.andwhat5.config.structs.DataStruc;
import com.pixelmonmod.pixelmon.comm.CommandChatHandler;
import com.pixelmonmod.pixelmon.entities.pixelmon.drops.DropItemHelper;
import com.pixelmonmod.pixelmon.enums.battle.BattleResults;
import com.pixelmonmod.pixelmon.storage.PixelmonStorage;
import com.pixelmonmod.pixelmon.storage.PlayerStorage;
import net.minecraft.command.CommandBase;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;
import java.util.Optional;

public class GymPlayerDefeatListener
{

	@SubscribeEvent
	public void onBattleEnded(PlayerBattleEndedEvent e) throws NumberInvalidException
	{
		BattleStruc bts = DataStruc.gcon.GymBattlers.stream().filter(c -> c.challenger.getName().equalsIgnoreCase(e.player.getName())).findAny().orElse(null);
		if (bts != null)
		{
			if (e.result.equals(BattleResults.FLEE) || e.result.equals(BattleResults.DRAW))
			{
				DataStruc.gcon.GymBattlers.remove(bts);
				return;
			}
			if (e.result.equals(BattleResults.VICTORY))
			{
				if (PixelmonStorage.pokeBallManager.getPlayerStorage(bts.challenger).get().getFirstAblePokemon(bts.challenger.getEntityWorld()) != null)
					if (PixelmonStorage.pokeBallManager.getPlayerStorage(Command.getEntityPlayer(bts.leader.getName())).get().getFirstAblePokemon(Command.getEntityPlayer(bts.leader.getName()).getEntityWorld()) != null)
					{
						DataStruc.gcon.GymBattlers.remove(bts);
						return;
					}
				if (!Utils.hasBadge(bts.challenger, bts.gym))
				{
					Utils.giveBadge(bts.challenger, bts.gym, bts.leader.getName());
					if (bts.gym.Money != 0)
					{
						Pixelmon.moneyManager.getBankAccount(bts.challenger).get().changeMoney(bts.gym.Money);
					}
					if (AGPConfig.General.physicalBadge)
					{
						ItemStack item = new ItemStack(CommandBase.getItemByText(bts.leader, bts.gym.Badge), 1);
						DropItemHelper.giveItemStackToPlayer(bts.challenger, item);
					}
					if (!bts.gym.Items.get(0).equals("null"))
					{
						ItemStack item1 = new ItemStack(CommandBase.getItemByText(bts.leader, bts.gym.Items.get(0)), 1);
						DropItemHelper.giveItemStackToPlayer(bts.challenger, item1);
					}
					if (!bts.gym.Items.get(1).equals("null"))
					{
						ItemStack item2 = new ItemStack(CommandBase.getItemByText(bts.leader, bts.gym.Items.get(1)), 1);
						DropItemHelper.giveItemStackToPlayer(bts.challenger, item2);
					}

					bts.challenger.sendMessage(Utils.toText("&7Congratulations, you defeated the &b" + bts.gym.Name + " &7gym! ", true));

					if (AGPConfig.Announcements.winAnnouncement)
					{
						for(EntityPlayerMP player : Utils.getAllPlayers())
							player.sendMessage(Utils.toText(AGPConfig.Announcements.winMessage
								.replace("{gym}", bts.gym.Name).replace("{challenger}", bts.challenger.getName()).replace("{leader}", bts.leader.getName()), false));
					}
					Utils.saveAGPData();
				}
			}
			DataStruc.gcon.GymBattlers.remove(bts);
			Utils.saveAGPData();
		}
	}

	@SubscribeEvent
	public void onAbruptEnd(PlayerBattleEndedAbnormalEvent e)
	{
		for (BattleParticipant s : e.battleController.participants)
		{
			Optional<BattleStruc> bts = DataStruc.gcon.GymBattlers.stream()
					.filter(b -> b.challenger.getName().equals(s.getName().toString())).findAny();
			if (bts.isPresent())
			{
				if (Arrays.stream(FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList()
						.getOnlinePlayerNames()).anyMatch(n -> n.equals(bts.get().challenger.getName())))
					CommandChatHandler.sendChat(bts.get().challenger, TextFormatting.RED + "It appears the gym " +
							"battle ended abnormally...");//The extra . was needed. Adds to the aw fuck effect.
				DataStruc.gcon.GymBattlers.remove(bts.get());
			}
		}
	}
}
