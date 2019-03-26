package agp.andwhat5.commands.gyms;

import agp.andwhat5.Utils;
import agp.andwhat5.commands.utils.PlayerOnlyCommand;
import agp.andwhat5.config.structs.ArenaStruc;
import agp.andwhat5.config.structs.GymStruc;
import agp.andwhat5.config.structs.Vec3dStruc;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;

public class SetGymWarp extends PlayerOnlyCommand {

    @Override
    public CommandResult execute(Player player, CommandContext args) {
        // /SetGymWarp <gym> <lobby|home|arena> [(if arena) <name> <stands|challenger|leader> <opt-(-delete)>

        GymStruc gym = args.<GymStruc>getOne("GymName").get();
        String location = args.<String>getOne("location").get();

        Vector3d pos = player.getPosition();
        Vector3d rotation = player.getRotation();
        Vec3dStruc loc = new Vec3dStruc(pos.getX(), pos.getY(), pos.getZ(), rotation.getX(), rotation.getY());
        String lName;

        switch (location.toLowerCase()) {
            case "lobby":
                gym.Lobby = loc;
                gym.worldUUID = player.getWorldUniqueId().get();
                lName = "lobby";
                player.sendMessage(Utils.toText("&7The &b" + gym.Name + " &7Gym's &b" + lName + " &7has been set to your current location!", true));
                break;
            case "arena":
                Optional<String> optArenaName = args.getOne("GymArena");
                Optional<String> optArenaSubLocationName = args.getOne("arenaSubLocation");
                Optional<String> optDelete = args.getOne("-delete");
                boolean deleteMode = optDelete.map(s -> s.equals("-delete")).orElse(false);

                if (!optArenaName.isPresent() || !optArenaSubLocationName.isPresent()) {
                    player.sendMessage(Utils.toText("&7Incorrect usage: &b/SetGymWarp <gym> <lobby|arena> [(if arena) <name> <stands|challenger|leader> <opt-(-delete)>]&7.", true));
                    return CommandResult.success();
                }

                String arenaName = optArenaName.get();
                String arenaSubLocationName = optArenaSubLocationName.get();

                ArenaStruc as = Utils.getArena(gym, arenaName);
                if (as == null) {
                    as = new ArenaStruc(arenaName);
                    gym.Arenas.add(as);
                }

                switch (arenaSubLocationName.toLowerCase()) {

                    case "stands":
                        as.Stands = deleteMode ? null : loc;
                        gym.worldUUID = player.getWorldUniqueId().get();
                        lName = arenaName + " stands";
                        break;

                    case "challenger":
                        as.Challenger = deleteMode ? null : loc;
                        gym.worldUUID = player.getWorldUniqueId().get();
                        lName = arenaName + " challenger stage";
                        break;

                    case "leader":
                        as.Leader = deleteMode ? null : loc;
                        gym.worldUUID = player.getWorldUniqueId().get();
                        lName = arenaName + " leader stage";
                        break;

                    default:
                        player.sendMessage(Utils.toText("&7The location &b" + arenaSubLocationName + " &7was not recognized!", true));
                        if (Utils.isArenaEmpty(as)) {
                            gym.Arenas.remove(as);
                        }
                        return CommandResult.success();
                }

                if (deleteMode) {
                    player.sendMessage(Utils.toText("&7The &b" + gym.Name + " &7Gym's &b" + lName + " &7has been deleted!", true));
                } else {
                    player.sendMessage(Utils.toText("&7The &b" + gym.Name + " &7Gym's &b" + lName + " &7has been set to your current location!", true));
                }

                if (Utils.isArenaEmpty(as)) {
                    gym.Arenas.remove(as);
                    player.sendMessage(Utils.toText("&7The &b" + as.Name + " &7Arena is empty and has been deleted!", true));
                }
                break;

            default:
                player.sendMessage(Utils.toText("&7Incorrect usage: &b/SetGymWarp <gym> <lobby|arena> [(if arena) <name> <stands|challenger|leader> <opt-(-delete)>]&7.", true));
                break;
        }

        Utils.editGym(gym);
        Utils.saveAGPData();

        return CommandResult.success();
    }


}
