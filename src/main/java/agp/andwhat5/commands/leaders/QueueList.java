package agp.andwhat5.commands.leaders;

import agp.andwhat5.Utils;
import agp.andwhat5.config.structs.GymStruc;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.user.UserStorageService;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class QueueList implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        GymStruc gym = args.<GymStruc>getOne("GymName").get();
        Optional<Player> target = args.getOne("Target");

        if (!Utils.isGymLeader((Player) src, gym) && !src.hasPermission("agp.headleader")) {//TODO assumes player
            src.sendMessage(Utils.toText("&7You must be a leader of the &b" + gym.Name + " &7Gym to view it's queue!", true));
            return CommandResult.success();
        }

        if (!target.isPresent()) {
            //Target whole list
            src.sendMessage(Utils.toText("&f--==[&dAGP - " + gym.Name + " Queue&f]==--", false));
            src.sendMessage(Utils.toText("&bChallengers: &7(&aOnline&7) &7(&cOffline&7)", false));
            src.sendMessage(Utils.toText("", false));
            AtomicInteger i = new AtomicInteger();
            if (gym.Queue.isEmpty()) {
                src.sendMessage(Utils.toText("&7There are no challengers in the queue!", false));
            } else {
                UserStorageService userStorageService = Sponge.getServiceManager().provide(UserStorageService.class).get();
                gym.Queue.forEach(uuid -> userStorageService.get(uuid).ifPresent(user -> src.sendMessage(Utils.toText("&b" + i.incrementAndGet() + ": " + (Sponge.getServer().getPlayer(uuid).isPresent() ? "&a" : "&c") + user.getName(), false))));
            }
        } else {
            //Target specific player
            Player playerTarget = target.get();
            String pName = playerTarget.getName();
            if (gym.Queue.contains(playerTarget.getUniqueId())) {
                src.sendMessage(Utils.toText("&7Challenger &b" + pName + " &7is currently in the &b" + gym.Name + " &7Gym queue!", true));
            } else {
                src.sendMessage(Utils.toText("&b" + pName + " &7is not in the &b" + gym.Name + " &7Gym queue!", true));
            }
        }
        return CommandResult.success();
    }


}
