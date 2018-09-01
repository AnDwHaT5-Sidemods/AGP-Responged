package agp.andwhat5.commands.leaders;

import agp.andwhat5.Utils;
import agp.andwhat5.commands.Command;
import agp.andwhat5.config.structs.GymStruc;
import net.minecraft.command.CommandException;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.user.UserStorageService;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static net.minecraft.command.CommandBase.getListOfStringsMatchingLastWord;

public class QueueList extends Command {

    public QueueList() {
        super("Shows the players waiting in the specified gyms queue.");
    }

    @Override
    public void execute(MinecraftServer server, CommandSource sender, String[] args) throws CommandException {
        if (args.length != 1 && args.length != 2) {
            sender.sendMessage(Utils.toText("&7Incorrect usage: /QueueList <gym> <opt-player>&7.", true));
            return;
        }

        if (!Utils.gymExists(args[0])) {
            sender.sendMessage(Utils.toText("&7The &b" + args[0] + " &7Gym does not exist!", true));
            return;
        }

        GymStruc gs = Utils.getGym(args[0]);
        if (!Utils.isGymLeader((Player) sender, gs) && !sender.hasPermission("agp.headleader")) {
            sender.sendMessage(Utils.toText("&7You must be a leader of the &b" + gs.Name + " &7Gym to view it's queue!", true));
            return;
        }

        if (args.length == 1) {
            sender.sendMessage(Utils.toText("&f--==[&dAGP - " + gs.Name + " Queue&f]==--", false));
            sender.sendMessage(Utils.toText("&bChallengers: &7(&aOnline&7) &7(&cOffline&7)", false));
            sender.sendMessage(Utils.toText("", false));
            AtomicInteger i = new AtomicInteger();
            if (gs.Queue.isEmpty()) {
                sender.sendMessage(Utils.toText("&7There are no challengers in the queue!", false));
            } else {
                UserStorageService userStorageService = Sponge.getServiceManager().provide(UserStorageService.class).get();
                gs.Queue.parallelStream().forEach(uuid -> userStorageService.get(uuid).ifPresent(user -> sender.sendMessage(Utils.toText("&b" + i.incrementAndGet() + ": " + (getEntityPlayer(uuid) != null ? "&a" : "&c") + user.getName(), false))));
            }
        } else {
            Player player = getEntityPlayer(args[1]);
            String pName = player != null ? player.getName() : args[1];
            if (gs.Queue.contains(Sponge.getServer().getPlayer(pName).get().getUniqueId())) {
                sender.sendMessage(Utils.toText("&7Challenger &b" + pName + " &7is currently in the &b" + gs.Name + " &7Gym queue!", true));
            } else {
                sender.sendMessage(Utils.toText("&b" + pName + " &7is not in the &b" + gs.Name + " &7Gym queue!", true));
            }
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, CommandSource sender, String[] args) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, Utils.getGymNames(true));
        } else if (args.length == 2) {
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        }
        return null;
    }

}
