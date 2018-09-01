package agp.andwhat5.commands.leaders;

import agp.andwhat5.Utils;
import agp.andwhat5.commands.Command;
import agp.andwhat5.config.structs.DataStruc;
import agp.andwhat5.config.structs.GymStruc;
import net.minecraft.command.CommandException;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

import java.util.List;
import java.util.UUID;

import static net.minecraft.command.CommandBase.getListOfStringsMatchingLastWord;

public class DeleteLeader extends Command {
    public DeleteLeader() {
        super("Removed a leader from the specified gym.");
    }

    @Override
    public void execute(MinecraftServer server, CommandSource sender, String[] args) throws CommandException {
        if (args.length != 2) {
            sender.sendMessage(Utils.toText("&7Incorrect usage: /DelLeader <player> <gym>&7.", true));
            return;
        }

        if (!Utils.gymExists(args[1])) {
            sender.sendMessage(Utils.toText("&7The &b" + args[1] + " &7Gym does not exist!", true));
            return;
        }

        GymStruc gs = Utils.getGym(args[1]);
        if (args[0].equalsIgnoreCase("npc")) {
        	if(gs.NPCAmount <= 0)
        	{
        		sender.sendMessage(Utils.toText("&7This gym has no NPC leaders.", true));
        		return;
        	}
            gs.NPCAmount -= 1;
            Utils.editGym(gs);
            Utils.saveAGPData();
            sender.sendMessage(Utils.toText("&7Successfully removed an &bNPC &7as a leader of the &b" + gs.Name + " &7Gym!", true));
            return;
        }

        UUID pUUID = Utils.getUUIDFromName(args[0]);
        if(pUUID == null)
        {
        	sender.sendMessage(Utils.toText("&7Could not find specified player.", true));
        	return;
        }
        if (!gs.PlayerLeaders.stream().anyMatch(l -> l.equals(pUUID))) {
            sender.sendMessage(Utils.toText("&b" + args[1] + " &7is not a leader of the &b" + gs.Name + " &7Gym!", true));
            return;
        }

        gs.PlayerLeaders.remove(gs.PlayerLeaders.stream().filter(p -> p.equals(pUUID)).findAny().get());
        Utils.editGym(gs);
        Utils.saveAGPData();
        sender.sendMessage(Utils.toText("&7Successfully removed &b" + args[0] + " &7as a leader of the &b" + gs.Name + " &7Gym!", true));
        Player player = Utils.getAllPlayers().stream().filter(p -> p.getUniqueId().equals(pUUID)).findAny().orElse(null);
        if(player != null)
        {
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
