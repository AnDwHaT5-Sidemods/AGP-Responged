package agp.andwhat5.listeners.forge;

import agp.andwhat5.Utils;
import agp.andwhat5.config.AGPConfig;
import agp.andwhat5.config.structs.DataStruc;
import agp.andwhat5.config.structs.GymStruc;
import com.pixelmonmod.pixelmon.api.events.BattleStartedEvent;
import com.pixelmonmod.pixelmon.api.events.BeatTrainerEvent;
import com.pixelmonmod.pixelmon.entities.npcs.NPCTrainer;
import com.pixelmonmod.pixelmon.entities.pixelmon.drops.DropItemHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

public class GymNPCDefeatListener {

    @SubscribeEvent
    public void onBattleStarted(BattleStartedEvent event) {
        if ((event.participant1[0].getEntity()) instanceof NPCTrainer) {
            NPCTrainer trainer = (NPCTrainer) event.participant1[0].getEntity();
            EntityPlayerMP player = (EntityPlayerMP) (event.participant2[0].getEntity());
            battleStartHelper(event, trainer, player);
        } else if ((event.participant2[0].getEntity()) instanceof NPCTrainer) {
            NPCTrainer trainer = (NPCTrainer) event.participant2[0].getEntity();
            EntityPlayerMP player = (EntityPlayerMP) (event.participant1[0].getEntity());
            battleStartHelper(event, trainer, player);
        }
    }

    /**
     * Takes a {@link BattleStartedEvent} along with the two entity possibilities for this mod,
     * {@link NPCTrainer} & {@link EntityPlayerMP}, and will attempt to ensure the passed
     * player object is able to battle the passed {@link NPCTrainer}
     *
     * @param event   The battle started event obtained from Pixelmon
     * @param trainer The {@link NPCTrainer} being battled
     * @param player  The {@link EntityPlayerMP} initiating a battle with the {@link NPCTrainer}
     */
    private void battleStartHelper(BattleStartedEvent event, NPCTrainer trainer, EntityPlayerMP player) {
        if (trainer.getEntityData().hasKey("GymLeader")) {
            String gymName = trainer.getEntityData().getString("GymLeader");
            if (!Utils.hasBadge((Player) player, Utils.getGym(gymName))) {
                GymStruc gym = DataStruc.gcon.GymData.stream().filter(g -> g.Name.equalsIgnoreCase(gymName)).findAny().get();
                if (!gym.Requirement.equalsIgnoreCase("null") && !gym.Requirement.isEmpty()) {
                    if (!Utils.hasBadge((Player) player, Utils.getGym(gym.Requirement))) {
                        ((Player) player).sendMessage(Utils.toText("&7You must have the badge from the &b" + gym.Requirement + " &7gym before challenging this one!", true));
                        player.closeScreen();
                        event.setCanceled(true);
                        return;
                    }
                }
                if (!Utils.checkLevels((Player) player, gym.LevelCap)) {
                    ((Player) player).sendMessage(Utils.toText("&7One or more of your Pixelmon exceed the level cap of this gym!", true));
                    player.closeScreen();

                    event.setCanceled(true);
                }
            } else {
                ((Player) player).sendMessage(Utils.toText("&7You have already won against the &b" + trainer.getEntityData().getString("GymLeader") + " &7gym!", true));
                player.closeScreen();
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onBeatTrainer(BeatTrainerEvent e) throws NumberInvalidException {
        if (e.trainer.getEntityData().hasKey("GymLeader")) {
            if (!Utils.gymExists(e.trainer.getEntityData().getString("GymLeader"))) {
                return;
            }
            GymStruc gs = Utils.getGym(e.trainer.getEntityData().getString("GymLeader"));
            if (!Utils.hasBadge((Player) e.player, gs)) {

                Utils.giveBadge((Player) e.player, gs, "NPC");
                if (gs.Money != 0) {
                    Utils.addCurrency((Player) e.player, gs.Money);
                }
                if (AGPConfig.General.physicalBadge) {
                    ItemStack item = new ItemStack(CommandBase.getItemByText(e.player, gs.Badge), 1);
                    DropItemHelper.giveItemStackToPlayer(e.player, item);
                }
                if (!gs.Commands.isEmpty()) {
                    gs.Commands.forEach(i -> Sponge.getCommandManager().process((CommandSource) Sponge.getServer(), i.trim().replace("%player%", e.player.getName()).replace("%leader%", "NPC")));
                }
                ((Player) e.player).sendMessage(Utils.toText("&7Congratulations, you defeated the &b" + gs.Name + " &7Gym!", true));
                if (AGPConfig.Announcements.winAnnouncement) {
                    Sponge.getServer().getBroadcastChannel().send(Utils.toText(AGPConfig.Announcements.winMessage
                            .replace("{gym}", gs.Name).replace("{challenger}", e.player.getName()).replace("{leader}", e.trainer.getName()), true));
                }
            }
        }
    }
}

