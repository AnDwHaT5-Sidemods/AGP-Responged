package agp.andwhat5.commands.leaders;

import agp.andwhat5.Utils;
import agp.andwhat5.commands.Command;
import agp.andwhat5.config.structs.ArenaStruc;
import agp.andwhat5.config.structs.BattleStruc;
import agp.andwhat5.config.structs.DataStruc;
import agp.andwhat5.config.structs.GymStruc;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;
import com.pixelmonmod.pixelmon.battles.controller.BattleControllerBase;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.storage.PixelmonStorage;
import com.pixelmonmod.pixelmon.storage.PlayerStorage;
import net.minecraft.command.CommandException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static net.minecraft.command.CommandBase.getListOfStringsMatchingLastWord;

public class AcceptChallenge extends Command {
    public AcceptChallenge() {
        super("Accepts a challenge from a player in the specififed gym queue.");
    }

    @Override
    public void execute(MinecraftServer server, CommandSource sender, String[] args) throws CommandException {
        Player user = requireEntityPlayer(sender);
        if (args.length != 1 && args.length != 2) {
        	sender.sendMessage(Utils.toText("&7Incorrect usage: &b/AcceptChallenge <gym> <opt-arena>&7.", true));
        	return;
        }
        if (!Utils.gymExists(args[0])) {
            sender.sendMessage(Utils.toText("&7The &b" + args[0] + " &7Gym does not exist!", true));
            return;
        }

        GymStruc gs = Utils.getGym(args[0]);
        if (!Utils.isGymLeader(user, gs) && !sender.hasPermission("agp.headleader")) {
            sender.sendMessage(Utils.toText("&7You are not a leader of the &b" + gs.Name + " &7Gym!", true));
            return;
        }

        if (gs.Queue.isEmpty()) {
            sender.sendMessage(Utils.toText("&7The &b" + gs.Name + " &7Gym queue is empty!", true));
            return;
        }

        if (!Utils.checkLevels(user, gs.LevelCap)) {
            sender.sendMessage(Utils.toText("&7Your team is above the level cap for the &b" + gs.Name + " &7Gym!", true));
            return;
        }

        UUID cUUID = gs.Queue.poll();
        Player challenger = getEntityPlayer(cUUID);
        if (challenger == null) {
            sender.sendMessage(Utils.toText("&7Player &b" + Utils.getNameFromUUID(cUUID) + " &7was not found on the server!", true));
            return;
        }

        if (!Utils.checkLevels(challenger, gs.LevelCap)) {
            sender.sendMessage(Utils.toText("&7Player &b" + Utils.getNameFromUUID(cUUID) + "&7's team is above the level cap for the &b" + gs.Name + " &7Gym!", true));
            challenger.sendMessage(Utils.toText("&7Your team is above the level cap for the &b" + gs.Name + " &7Gym!", true));
            return;
        }

        if (BattleRegistry.getBattle((EntityPlayer) challenger) != null) {
            BattleControllerBase battlecontroller = BattleRegistry.getBattle((EntityPlayer) challenger);
            battlecontroller.endBattle();
        }

        if (BattleRegistry.getBattle((EntityPlayer) user) != null) {
            BattleControllerBase battlecontroller = BattleRegistry.getBattle((EntityPlayer) user);
            battlecontroller.endBattle();
        }

        ArenaStruc as = args.length == 2 ? Utils.getArena(gs, args[1]) : (gs.Arenas.size() > 0 ? gs.Arenas.get(0) : null);
        if (as != null && as.Leader != null && as.Challenger != null) {
            Utils.setPosition((Player) sender, as.Leader);
            Utils.setPosition(challenger, as.Challenger);
        } else if (args.length == 2) {
            user.sendMessage(Utils.toText("&7The &b" + args[2] + " &7Arena has not been set up!", true));
        }

        Optional<PlayerStorage> leaderTeam = PixelmonStorage.pokeBallManager.getPlayerStorage((EntityPlayerMP) user);
        Optional<PlayerStorage> challengerTeam = PixelmonStorage.pokeBallManager.getPlayerStorage((EntityPlayerMP) challenger);

        if (leaderTeam.isPresent() && challengerTeam.isPresent()) {
            leaderTeam.get().healAllPokemon((World) user.getWorld());
            challengerTeam.get().healAllPokemon((World) challenger.getWorld());

            BattleStruc bs = new BattleStruc(gs, user.getUniqueId(), challenger.getUniqueId());
            DataStruc.gcon.GymBattlers.add(bs);
            user.sendMessage(Utils.toText("&7Initiating battle against &b" + Utils.getNameFromUUID(cUUID) +
                    "&7!", true));
            challenger.sendMessage(Utils.toText("&7Gym Leader &b" + user.getName() + " &7has " +
                    "accepted your challenge against the &b" + gs.Name + " &bGym!", true));

            EntityPixelmon leaderPkmn1 = leaderTeam.get().getFirstAblePokemon((World) user.getWorld());
            BattleParticipant ldr = new PlayerParticipant((EntityPlayerMP) user, leaderPkmn1);
            EntityPixelmon challengerPkmn1 = challengerTeam.get().getFirstAblePokemon((World) challenger.getWorld());
            BattleParticipant chal = new PlayerParticipant((EntityPlayerMP) challenger, challengerPkmn1);

            ldr.startedBattle = true;
            BattleParticipant[] team1 = new BattleParticipant[]{ldr};
            BattleParticipant[] team2 = new BattleParticipant[]{chal};
            new BattleControllerBase(team1, team2);
        } else {
            user.sendMessage(Utils.toText("&7An error occurred starting the gym battle!",
                    true));
            challenger.sendMessage(Utils.toText("&7An error occurred starting the gym battle!",
                    true));
        }


    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, CommandSource sender, String[] args) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, Utils.getGymNames(true));
        } else if (args.length == 2 && Utils.gymExists(args[0])) {
            return getListOfStringsMatchingLastWord(args, Utils.getArenaNames(Utils.getGym(args[0]), true));
        }
        return null;
    }

}
