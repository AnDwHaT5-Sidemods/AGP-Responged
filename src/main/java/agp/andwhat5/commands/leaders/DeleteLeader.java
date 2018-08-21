package agp.andwhat5.commands.leaders;

import agp.andwhat5.Utils;
import agp.andwhat5.commands.Command;
import agp.andwhat5.config.structs.GymStruc;
import net.minecraft.command.CommandException;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

import java.util.List;

import static net.minecraft.command.CommandBase.getListOfStringsMatchingLastWord;

public class DeleteLeader extends Command {
    public DeleteLeader() {
        super("/delleader <player> <gym>");
    }

    @Override
    public void execute(MinecraftServer server, CommandSource sender, String[] args) throws CommandException {
        if (args.length != 2) {
            super.sendUsage(sender);
            return;
        }

        if (!Utils.gymExists(args[1])) {
            sender.sendMessage(Utils.toText("&7The &b" + args[1] + " &7Gym does not exist!", true));
            return;
        }

        GymStruc gs = Utils.getGym(args[1]);
        if (args[0].equalsIgnoreCase("npc")) {
            gs.PlayerLeaders.remove("npc");
            Utils.editGym(gs);
            Utils.saveAGPData();
            sender.sendMessage(Utils.toText("&7Successfully removed the &bNPC &7as a leader of the &b" + gs.Name + " &7Gym!", true));
            return;
        }

        Player player = requireEntityPlayer(args[0]);
        String pName = player != null ? player.getName() : args[0];
        if (!gs.PlayerLeaders.stream().anyMatch(l -> l.equals(Sponge.getServer().getPlayer(args[0]).get().getUniqueId()))) {
            sender.sendMessage(Utils.toText("&b" + pName + " &7is not a leader of the &b" + gs.Name + " &7Gym!", true));
            return;
        }

        gs.PlayerLeaders.remove(gs.PlayerLeaders.stream().filter(p -> p.equals(Sponge.getServer().getPlayer(args[0]).get().getUniqueId())).findAny().get());
        Utils.editGym(gs);
        Utils.saveAGPData();
        sender.sendMessage(Utils.toText("&7Successfully removed &b" + pName + " &7as a leader of the &b" + gs.Name + " &7Gym!", true));
        if (player != null) {
            Utils.getGym(gs.Name).OnlineLeaders.remove(player.getUniqueId());
            player.sendMessage(Utils.toText("&7You are no longer a leader of the &b" + gs.Name + " " +
                    "&7Gym!", true));
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, CommandSource sender, String[] args) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        } else if (args.length == 2) {
            return getListOfStringsMatchingLastWord(args, Utils.getGymNames(true));
        }
        return null;
    }

}
