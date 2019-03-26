package agp.andwhat5.commands.players;

import agp.andwhat5.Utils;
import agp.andwhat5.config.structs.GymStruc;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.UUID;

import static agp.andwhat5.config.structs.GymStruc.EnumStatus.OPEN;

public class ChallengeGym implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        if (!(src instanceof Player)) {
            src.sendMessage(Text.of(TextColors.RED, "This command can only be ran by players"));
            return CommandResult.success();
        }

        Player user = (Player) src;
        GymStruc gs = args.<GymStruc>getOne("GymName").get();
        if (Utils.hasBadge(user, gs)) {
            src.sendMessage(Utils.toText("&7You have already beaten the &b" + gs.Name + " &7Gym!", true));
            return CommandResult.success();
        }
        if (!gs.Requirement.isEmpty() && !gs.Requirement.equalsIgnoreCase("null") && !Utils.hasBadge(user, Utils.getGym(gs.Requirement))) {
            src.sendMessage(Utils.toText("&7You can not challenge this gym until you have the badge from the &b" + gs.Requirement + " &7gym!", true));
            return CommandResult.success();
        }
        if (Utils.isGymLeader(user, gs)) {
            src.sendMessage(Utils.toText("&7You may not challenge the &b" + gs.Name + " &7Gym as you are it's leader!", true));
            return CommandResult.success();
        }

        if (gs.Status != OPEN) {
            src.sendMessage(Utils.toText("&7You may not challenge the &b" + gs.Name + " &7Gym as it is currently closed!", true));
            return CommandResult.success();
        }
        if (Utils.isInGymBattle(user)) {
            src.sendMessage(Utils.toText("&7You may not challenge the &b" + gs.Name + " &7Gym as you are currently in a gym battle!", true));
            return CommandResult.success();
        }
        if (Utils.isInAnyQueue(user)) {
            src.sendMessage(Utils.toText("&7You may not challenge the &b" + gs.Name + " &7Gym as you are already in a queue!", true));
            return CommandResult.success();
        }

        gs.Queue.add(user.getUniqueId());
        src.sendMessage(Utils.toText("&7Successfully joined the &b" + gs.Name + " &7Gym queue!", true));
        src.sendMessage(Utils.toText("&7Your position is &b" + Utils.getQueuedPlayers(gs).size() + "&7.", true));

        for (UUID leader : Utils.getGymLeaders(gs)) {
            Sponge.getServer().getPlayer(leader).ifPresent(leader2 -> leader2.sendMessage(Utils.toText("&7Challenger &b" + user.getName() + " &7has joined the &b" + gs.Name + " &7Gym queue!", true)));
        }
        return CommandResult.success();
    }

}
