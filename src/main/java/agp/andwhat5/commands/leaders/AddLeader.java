package agp.andwhat5.commands.leaders;

import agp.andwhat5.Utils;
import agp.andwhat5.commands.Command;
import agp.andwhat5.config.AGPConfig;
import agp.andwhat5.config.structs.DataStruc;
import agp.andwhat5.config.structs.GymStruc;
import net.minecraft.command.CommandException;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

import java.util.List;
import java.util.UUID;

import static agp.andwhat5.config.structs.GymStruc.EnumStatus.*;
import static net.minecraft.command.CommandBase.getListOfStringsMatchingLastWord;

public class AddLeader extends Command {
    public AddLeader() {
        super("Adds the specified player to the specified gym.");
    }

    @Override
    public void execute(MinecraftServer server, CommandSource sender, String[] args) throws CommandException {
        if (args.length != 2) {
            sender.sendMessage(Utils.toText("&7Incorrect usage: &b/AddLeader <player:npc> <gym>&7.", true));
            return;
        }

        String leaderName = args[0];
        String gymName = args[1];

        if(leaderName.equalsIgnoreCase("npc"))
        {
            if (!Utils.gymExists(gymName)) {
            	sender.sendMessage(Utils.toText("&7The specified gym does not exist.", true));
            	return;
            }
            GymStruc gs = Utils.getGym(gymName);
            gs.NPCAmount += 1;
            Utils.editGym(gs);
            Utils.saveAGPData();
            sender.sendMessage(Utils.toText("&7Successfully added an NPC to the list of leaders.", true));
            return;

        }
        Player player = requireEntityPlayer(leaderName);
        if(player == null)
        {
        	sender.sendMessage(Utils.toText("&7The player you specified is not currently online.", true));
        	return;
        }
        UUID pUUID = player.getUniqueId();

        if (Utils.gymExists(gymName)) {
            GymStruc gs = Utils.getGym(gymName);
            if (!gs.PlayerLeaders.stream().noneMatch(l -> l.equals(pUUID))) {
                sender.sendMessage(Utils.toText("&b" + leaderName + " &7is already a leader of the &b" + gs.Name + " &7Gym!", true));
                return;
            }

            Utils.addLeader(pUUID, gs);
            if (AGPConfig.General.autoOpen) {
                gs.Status = OPEN;
            }

            Utils.saveAGPData();
            sender.sendMessage(Utils.toText("&7Successfully made &b" + leaderName + " &7a leader of the &b" + gs.Name + " &7Gym!", true));
            if (player != null) {
                Utils.getGym(gs.Name).OnlineLeaders.add(pUUID);
                player.sendMessage(Utils.toText("&7You are now a leader of the &b" + gs.Name + " &7Gym!",
                        true));

                for (Player pl : Utils.getAllPlayers())
                    pl.sendMessage(Utils.toText("&b" + leaderName + " &7is now a " +
                            "leader of " +
                            "the &b" + gs.Name + " &7Gym!", true));
            }
        } else if (gymName.equalsIgnoreCase("all")) {
            List<GymStruc> gyms = DataStruc.gcon.GymData;
            final Player pl = player;
            gyms.forEach(gs -> {
                if (gs.PlayerLeaders.stream().noneMatch(l -> l.equals(pUUID))) {
                    if (pl != null) {
                        gs.PlayerLeaders.add(pl.getUniqueId());
                    } else {
                        gs.PlayerLeaders.add(Sponge.getServer().getPlayer(pUUID).get().getUniqueId());
                    }
                }
            });

            Utils.sortGyms();
            Utils.saveAGPData();
            sender.sendMessage(Utils.toText("&7Successfully made &b" + leaderName + " &7a leader of all Gyms!", true));
        } else {
            sender.sendMessage(Utils.toText("&7The &b" + gymName + " &7Gym does not exist!", true));
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
