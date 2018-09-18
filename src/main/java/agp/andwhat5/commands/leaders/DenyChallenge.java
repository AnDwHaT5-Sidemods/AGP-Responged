package agp.andwhat5.commands.leaders;

import agp.andwhat5.Utils;
import agp.andwhat5.config.structs.GymStruc;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;
import java.util.UUID;

public class DenyChallenge implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {

        GymStruc gym = args.<GymStruc>getOne("GymName").get();
        Optional<Player> target = args.getOne("player");

        if (!Utils.isGymLeader((Player) src, gym) && !src.hasPermission("agp.headleader")) {
            src.sendMessage(Utils.toText("&7You are not a leader of the &b" + gym.Name + " &7Gym!", true));
            return CommandResult.success();
        }

        if (gym.Queue.isEmpty()) {
            src.sendMessage(Utils.toText("&7The &b" + gym.Name + " &7Gym's queue is empty!", true));
            return CommandResult.success();
        }

        if (!target.isPresent()) {
            //Target first in the queue
            UUID pUUID = gym.Queue.get(0);
            src.sendMessage(Utils.toText("&7Challenger &b" + Utils.getNameFromUUID(pUUID) + " &7has been removed from the &b" + gym.Name + " &7Gym queue!", true));
            Sponge.getServer().getPlayer(pUUID).ifPresent(player1 -> player1.sendMessage(Utils.toText("&7Your challenge to the &b" + gym.Name + " &7Gym was denied!", true)));
            gym.Queue.remove(0);
        } else {
            //Target specific player
            Player player = target.get();

            UUID pUUID = player.getUniqueId();
            if (!gym.Queue.contains(pUUID)) {
                src.sendMessage(Utils.toText("&7Challenger &b" + Utils.getNameFromUUID(pUUID) + " &7is not in the &b" + gym.Name + " &7Gym queue!", true));
                return CommandResult.success();
            }
            gym.Queue.remove(pUUID);
            src.sendMessage(Utils.toText("&7Challenger &b" + Utils.getNameFromUUID(pUUID) + " &7has been removed from the &b" + gym.Name + " &7Gym queue!", true));
            player.sendMessage(Utils.toText("&7Your challenge to the &b" + gym.Name + " &7Gym was denied!", true));
        }

        return CommandResult.success();
    }

}
