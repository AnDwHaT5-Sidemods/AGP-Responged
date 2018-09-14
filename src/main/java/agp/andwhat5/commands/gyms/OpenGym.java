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

import static agp.andwhat5.config.structs.GymStruc.EnumStatus.OPEN;

public class OpenGym implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {

        GymStruc gs = args.<GymStruc>getOne("GymName").get();
        if (!Utils.isGymLeader((Player) src, gs) && !src.hasPermission("agp.headleader")) {
            src.sendMessage(Utils.toText("&7You are not a leader of the &b" + gs.Name + " &7Gym!", true));
            return CommandResult.success();
        }

        if (gs.Status == OPEN) {
            src.sendMessage(Utils.toText("&7The &b" + gs.Name + " &7Gym is already open!", true));
            return CommandResult.success();
        }

        gs.Status = OPEN;
        src.sendMessage(Utils.toText("&7Successfully opened the &b" + gs.Name + " &7Gym!", true));
        if (AGPConfig.Announcements.openAnnouncement) {
            Sponge.getServer().getBroadcastChannel().send(Utils.toText(AGPConfig.Announcements.openMessage
                    .replace("{gym}", gs.Name).replace("{leader}", src.getName()), true));
        }

        return CommandResult.success();

    }


}
