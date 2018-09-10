package agp.andwhat5.commands.leaders;

import agp.andwhat5.Utils;
import agp.andwhat5.config.structs.GymStruc;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

import java.util.UUID;

public class DeleteLeader implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String target = args.<String>getOne("player").get();//TODO kill the psyduck
        GymStruc gym = args.<GymStruc>getOne("GymName").get();

        if (target.equalsIgnoreCase("npc")) {
        	if(gym.NPCAmount <= 0)
        	{
                src.sendMessage(Utils.toText("&7This gym has no NPC leaders.", true));
        		return CommandResult.success();
        	}
            gym.NPCAmount -= 1;
            Utils.editGym(gym);
            Utils.saveAGPData();
            src.sendMessage(Utils.toText("&7Successfully removed an &bNPC &7as a leader of the &b" + gym.Name + " &7Gym!", true));
            return CommandResult.success();
        }

        UUID pUUID = Utils.getUUIDFromName(target);
        if(pUUID == null)
        {
            src.sendMessage(Utils.toText("&7Could not find specified player.", true));
            return CommandResult.success();
        }
        if (gym.PlayerLeaders.stream().noneMatch(l -> l.equals(pUUID))) {
            src.sendMessage(Utils.toText("&b" + target + " &7is not a leader of the &b" + gym.Name + " &7Gym!", true));
            return CommandResult.success();
        }

        gym.PlayerLeaders.remove(gym.PlayerLeaders.stream().filter(p -> p.equals(pUUID)).findAny().get());
        Utils.editGym(gym);
        Utils.saveAGPData();
        src.sendMessage(Utils.toText("&7Successfully removed &b" + target + " &7as a leader of the &b" + gym.Name + " &7Gym!", true));
        Player player = Utils.getAllPlayers().stream().filter(p -> p.getUniqueId().equals(pUUID)).findAny().orElse(null);

        if(player != null)
        {
            Utils.getGym(gym.Name).OnlineLeaders.remove(player.getUniqueId());
            player.sendMessage(Utils.toText("&7You are no longer a leader of the &b" + gym.Name + "&7Gym!", true));
        }
        return CommandResult.success();

    }

}
