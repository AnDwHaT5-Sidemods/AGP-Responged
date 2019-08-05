package agp.andwhat5.listeners.forge;

import agp.andwhat5.Utils;
import agp.andwhat5.battles.BattleUtil;
import agp.andwhat5.config.AGPConfig;
import agp.andwhat5.config.structs.BattleStruc;
import agp.andwhat5.config.structs.DataStruc;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.events.battles.BattleEndEvent;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.comm.CommandChatHandler;
import com.pixelmonmod.pixelmon.entities.pixelmon.drops.DropItemHelper;
import com.pixelmonmod.pixelmon.enums.battle.BattleResults;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
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

                BattleStruc bts = DataStruc.gcon.GymBattlers.stream().filter(c -> c.leader.equals(playerParticipant.player.getUniqueID())).findAny().orElse(null);
                if (bts != null) {
                    Player challenger = Sponge.getServer().getPlayer(bts.challenger).get();

                    if (!bts.challenger.equals(challenger.getUniqueId())) {
                        //Return if the player is the gym leader
                        return;
                    }

                    Player leader = Sponge.getServer().getPlayer(bts.leader).get();

                    if (result.equals(BattleResults.FLEE) || result.equals(BattleResults.DRAW) || result.equals(BattleResults.VICTORY)) {
                        if (bts.arena != null)
                            bts.arena.inUse = false;
                        DataStruc.gcon.GymBattlers.remove(bts);
                        return;
                    }
                    if (result.equals(BattleResults.DEFEAT)) {
                        if (Pixelmon.storageManager.getParty((EntityPlayerMP) challenger).countAblePokemon() == 0) {
                            if (bts.arena != null)
                                bts.arena.inUse = false;
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
                                DropItemHelper.giveItemStack((EntityPlayerMP) challenger, item, false);
                            }
                            if (!bts.gym.Commands.isEmpty()) {
                                bts.gym.Commands.forEach(i -> Sponge.getCommandManager().process((CommandSource) Sponge.getServer(), i.trim().replace("%player%", challenger.getName()).replace("%leader%", leader.getName())));
                            }

                            challenger.sendMessage(Utils.toText("&7Congratulations, you defeated the &b" + bts.gym.Name + " &7gym! ", true));

                            if (AGPConfig.Announcements.winAnnouncement) {
                                Sponge.getServer().getBroadcastChannel().send(Utils.toText(AGPConfig.Announcements.winMessage
                                        .replace("{gym}", bts.gym.Name).replace("{challenger}", challenger.getName()).replace("{leader}", leader.getName()), true));
                            }
                            Utils.saveAGPData();
                        }
                    }
                    if (bts.arena != null)
                        bts.arena.inUse = false;
                    DataStruc.gcon.GymBattlers.remove(bts);
                    Utils.saveAGPData();

                    //Clean up any temp teams
                    //Sponge.getServer().getPlayer(leader.getUniqueId()).ifPresent(BattleUtil::restoreOriginalTeam);
                }
            }
            else
            	return;
        }

    }

    /*@SubscribeEvent
    public void onAbruptEnd(BattleEndEvent e) {
        if (e.abnormal) {
            if (e.getPlayers().isEmpty()) {
                return;
            }

            for (BattleParticipant s : e.bc.participants) {
                Optional<BattleStruc> bts = DataStruc.gcon.GymBattlers.stream().filter(b -> b.challenger.equals(s.getEntity().getUniqueID())).findAny();

                bts.ifPresent(battleStruc -> {
                    //Clean up any temp teams
                    Sponge.getServer().getPlayer(battleStruc.leader).ifPresent(BattleUtil::restoreOriginalTeam);

                    Sponge.getServer().getPlayer(battleStruc.challenger).ifPresent(player -> CommandChatHandler.sendChat((ICommandSender) player, TextFormatting.RED + "It appears the gym battle ended abnormally..."));//The extra . was needed. Adds to the aw fuck effect.
                    Sponge.getServer().getPlayer(battleStruc.leader).ifPresent(player -> CommandChatHandler.sendChat((ICommandSender) player, TextFormatting.RED + "It appears the gym battle ended abnormally..."));//The extra . was needed. Adds to the aw fuck effect.
                    if (battleStruc.arena != null)
                        battleStruc.arena.inUse = false;
                    DataStruc.gcon.GymBattlers.remove(battleStruc);
                });
            }
        }
    }*/

    /**
     * This should never do anything as teams should be restored thru the above methods
     * but I'm going to add it just in case
     */
    /*@SubscribeEvent
    public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        BattleUtil.restoreOriginalTeam((Player) event.player);
    }
    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        BattleUtil.restoreOriginalTeam((Player) event.player);
    }*/
}
