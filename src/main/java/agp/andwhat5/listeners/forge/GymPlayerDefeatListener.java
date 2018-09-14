package agp.andwhat5.listeners.forge;

import agp.andwhat5.Utils;
import agp.andwhat5.config.AGPConfig;
import agp.andwhat5.config.structs.BattleStruc;
import agp.andwhat5.config.structs.DataStruc;
import com.pixelmonmod.pixelmon.api.events.battles.BattleEndEvent;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.comm.CommandChatHandler;
import com.pixelmonmod.pixelmon.entities.pixelmon.drops.DropItemHelper;
import com.pixelmonmod.pixelmon.enums.battle.BattleResults;
import com.pixelmonmod.pixelmon.storage.PixelmonStorage;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Map;
import java.util.Optional;

public class GymPlayerDefeatListener {

    @SubscribeEvent
    public void onBattleEnded(BattleEndEvent e) throws NumberInvalidException {
        if (e.getPlayers().isEmpty())
            return;

        for (Map.Entry<BattleParticipant, BattleResults> resultsEntry : e.results.entrySet()) {
            if (resultsEntry.getKey() instanceof PlayerParticipant) {
                PlayerParticipant playerParticipant = (PlayerParticipant) resultsEntry.getKey();
                BattleResults result = resultsEntry.getValue();

                BattleStruc bts = DataStruc.gcon.GymBattlers.stream().filter(c -> c.challenger.equals(playerParticipant.player.getUniqueID())).findAny().orElse(null);
                if (bts != null) {
                    Player challenger = Sponge.getServer().getPlayer(bts.challenger).get();
                    Player leader = Sponge.getServer().getPlayer(bts.leader).get();

                    if (result.equals(BattleResults.FLEE) || result.equals(BattleResults.DRAW)) {
                        DataStruc.gcon.GymBattlers.remove(bts);
                        return;
                    }
                    if (result.equals(BattleResults.VICTORY)) {
                        if (PixelmonStorage.pokeBallManager.getPlayerStorage((EntityPlayerMP) challenger).get().getFirstAblePokemon((World) challenger.getWorld()) != null)
                            if (PixelmonStorage.pokeBallManager.getPlayerStorage((EntityPlayerMP) leader).get().getFirstAblePokemon((World) leader.getWorld()) != null) {
                                DataStruc.gcon.GymBattlers.remove(bts);
                                return;
                            }
                        if (!Utils.hasBadge(bts.challenger, bts.gym)) {
                            Utils.giveBadge(challenger, bts.gym, leader.getName());
                            if (bts.gym.Money != 0) {
                                Utils.addCurrency(challenger, bts.gym.Money);
                            }
                            if (AGPConfig.General.physicalBadge) {
                                ItemStack item = new ItemStack(CommandBase.getItemByText((ICommandSender) leader, bts.gym.Badge), 1);
                                DropItemHelper.giveItemStackToPlayer((EntityPlayer) challenger, item);
                            }
                            if(!bts.gym.Commands.isEmpty())
                            {
                            	bts.gym.Commands.forEach(i -> Sponge.getCommandManager().process((CommandSource) Sponge.getServer(), i.trim()));
                            }

                            challenger.sendMessage(Utils.toText("&7Congratulations, you defeated the &b" + bts.gym.Name + " &7gym! ", true));

                            if (AGPConfig.Announcements.winAnnouncement) {
                                Sponge.getServer().getBroadcastChannel().send(Utils.toText(AGPConfig.Announcements.winMessage
                                        .replace("{gym}", bts.gym.Name).replace("{challenger}", challenger.getName()).replace("{leader}", leader.getName()), true));
                            }
                            Utils.saveAGPData();
                        }
                    }
                    DataStruc.gcon.GymBattlers.remove(bts);
                    Utils.saveAGPData();
                }
            }
        }
    }

    @SubscribeEvent
    public void onAbruptEnd(BattleEndEvent e) {
        if (e.abnormal) {
            if (e.getPlayers().isEmpty()) {
                return;
            }

            for (BattleParticipant s : e.bc.participants) {
                Optional<BattleStruc> bts = DataStruc.gcon.GymBattlers.stream().filter(b -> b.challenger.equals(s.getEntity().getUniqueID())).findAny();

                bts.ifPresent(battleStruc -> {
                    Sponge.getServer().getPlayer(battleStruc.challenger).ifPresent(player -> CommandChatHandler.sendChat((ICommandSender) player, TextFormatting.RED + "It appears the gym battle ended abnormally..."));//The extra . was needed. Adds to the aw fuck effect.
                    Sponge.getServer().getPlayer(battleStruc.leader).ifPresent(player -> CommandChatHandler.sendChat((ICommandSender) player, TextFormatting.RED + "It appears the gym battle ended abnormally..."));//The extra . was needed. Adds to the aw fuck effect.
                    DataStruc.gcon.GymBattlers.remove(battleStruc);
                });
            }
        }
    }

}
