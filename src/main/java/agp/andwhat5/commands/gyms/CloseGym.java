package agp.andwhat5.commands.gyms;

import agp.andwhat5.Utils;
import agp.andwhat5.config.AGPConfig;
import agp.andwhat5.config.structs.GymStruc;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static agp.andwhat5.config.structs.GymStruc.EnumStatus.*;

public class CloseGym implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {

        AtomicBoolean force = new AtomicBoolean(false);
        args.<String>getOne("-f").ifPresent(consumer -> {
            if (consumer.equalsIgnoreCase("-f")) {
                force.set(true);
            }
        });

        GymStruc gs = args.<GymStruc>getOne("GymName").get();
        if (!Utils.isGymLeader((Player) src, gs) && !src.hasPermission("agp.headleader")) {
            src.sendMessage(Utils.toText("&7You must be a leader of the &b" + gs.Name + " &7Gym to close it!", true));
            return CommandResult.success();
        }
        if (gs.Status != OPEN && gs.Status != NPC) {
            src.sendMessage(Utils.toText("&7The &b" + gs.Name + " &7Gym is not open!", true));
            return CommandResult.success();
        }

        if (force.get()) {
            gs.Status = CLOSED;
        } else {
            if (gs.NPCAmount > 0) {
                gs.Status = NPC;
            } else {
                gs.Status = CLOSED;
            }
        }

        for (UUID queued : Utils.getQueuedPlayers(gs)) {
            Sponge.getServer().getPlayer(queued).ifPresent(player -> player.sendMessage(Utils.toText("&7The &b" + gs.Name + " &7Gym is closing! Removing you from the queue...", true)));
        }

        gs.Queue.clear();
        if (AGPConfig.Announcements.closeAnnouncement) {
            Sponge.getServer().getBroadcastChannel().send(Utils.toText(AGPConfig.Announcements.closeMessage
                    .replace("{gym}", gs.Name)
                    .replace("{leader}", src.getName()), true));
        } else {
            for (UUID leader : Utils.getGymLeaders(gs)) {
                Sponge.getServer().getPlayer(leader).ifPresent(player -> player.sendMessage(Utils.toText("&7Leader &b" + src.getName() + " &7has closed the &b" + gs.Name + " &7Gym!", true)));
            }
        }
        return CommandResult.success();
    }


}
