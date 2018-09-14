package agp.andwhat5.commands.leaders;

import agp.andwhat5.Utils;
import agp.andwhat5.config.AGPConfig;
import agp.andwhat5.config.structs.GymStruc;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;
import java.util.UUID;

import static agp.andwhat5.config.structs.GymStruc.EnumStatus.OPEN;

public class AddLeader implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        String target = args.<String>getOne("player").get();//TODO kill the psyduck
        GymStruc gym = args.<GymStruc>getOne("GymName").get();

        if (target.equalsIgnoreCase("npc")) {
            gym.NPCAmount += 1;
            Utils.editGym(gym);
            Utils.saveAGPData();
            src.sendMessage(Utils.toText("&7Successfully added an NPC to the list of leaders.", true));
            return CommandResult.success();
        }

        Optional<Player> playerMaybe = Sponge.getServer().getPlayer(target);
        if (!playerMaybe.isPresent()) {
            src.sendMessage(Utils.toText("&7The player you specified is not currently online.", true));
            return CommandResult.success();
        }

        Player player = playerMaybe.get();
        UUID pUUID = player.getUniqueId();

        if (gym.PlayerLeaders.stream().anyMatch(l -> l.equals(pUUID))) {
            src.sendMessage(Utils.toText("&b" + target + " &7is already a leader of the &b" + gym.Name + " &7Gym!", true));
            return CommandResult.success();
        }

        Utils.addLeader(pUUID, gym);
        if (AGPConfig.General.autoOpen) {
            gym.Status = OPEN;
        }

        Utils.saveAGPData();
        src.sendMessage(Utils.toText("&7Successfully made &b" + target + " &7a leader of the &b" + gym.Name + " &7Gym!", true));
        gym.OnlineLeaders.add(pUUID);
        player.sendMessage(Utils.toText("&7You are now a leader of the &b" + gym.Name + " &7Gym!", true));

        Sponge.getServer().getBroadcastChannel().send(Utils.toText("&b" + target + " &7is now a leader of the &b" + gym.Name + " &7Gym!", true));

        return CommandResult.success();
    }


}
