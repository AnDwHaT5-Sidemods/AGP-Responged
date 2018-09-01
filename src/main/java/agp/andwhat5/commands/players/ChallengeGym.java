package agp.andwhat5.commands.players;

import agp.andwhat5.Utils;
import agp.andwhat5.commands.Command;
import agp.andwhat5.config.structs.GymStruc;
import net.minecraft.command.CommandException;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

import java.util.List;
import java.util.UUID;

import static agp.andwhat5.config.structs.GymStruc.EnumStatus.OPEN;
import static net.minecraft.command.CommandBase.getListOfStringsMatchingLastWord;

public class ChallengeGym extends Command {

    public ChallengeGym() {
        super("Challenges the specified gym to a battle.");
    }

    @Override
    public void execute(MinecraftServer server, CommandSource sender, String[] args) throws CommandException {
        Player user = Command.requireEntityPlayer(sender);

        if (args.length != 1) {
            sender.sendMessage(Utils.toText("&7Incorrect usage: &b/ChallengeGym <gym>&7.", true));
            return;
        }

        if (!Utils.gymExists(args[0])) {
            sender.sendMessage(Utils.toText("&7The &b" + args[0] + " &7Gym does not exist!", true));
            return;
        }

        GymStruc gs = Utils.getGym(args[0]);
        if (Utils.hasBadge(user, gs)) {
            sender.sendMessage(Utils.toText("&7You have already beaten the &b" + gs.Name + " &7Gym!", true));
            return;
        }
        if (!gs.Requirement.isEmpty() && !gs.Requirement.equalsIgnoreCase("null") && !Utils.hasBadge(user, Utils.getGym(gs.Requirement))) {
            sender.sendMessage(Utils.toText("&7You can not challenge this gym until you have the badge from the &b" + gs.Requirement + " &7gym!", true));
            return;
        }
        if (Utils.isGymLeader(user, gs)) {
            sender.sendMessage(Utils.toText("&7You may not challenge the &b" + gs.Name + " &7Gym as you are it's leader!", true));
            return;
        }

        if (gs.Status != OPEN) {
            sender.sendMessage(Utils.toText("&7You may not challenge the &b" + gs.Name + " &7Gym as it is currently closed!", true));
            return;
        }
        if (Utils.isInGymBattle(user)) {
            sender.sendMessage(Utils.toText("&7You may not challenge the &b" + gs.Name + " &7Gym as you are currently in a gym battle!", true));
            return;
        }
        if (Utils.isInAnyQueue(user)) {
            sender.sendMessage(Utils.toText("&7You may not challenge the &b" + gs.Name + " &7Gym as you are already in a queue!", true));
            return;
        }

        gs.Queue.add(user.getUniqueId());
        sender.sendMessage(Utils.toText("&7Successfully joined the &b" + gs.Name + " &7Gym queue!", true));
        sender.sendMessage(Utils.toText("&7Your position is &b" + Utils.getQueuedPlayers(gs).size() + "&7.", true));

        for (UUID leader : Utils.getGymLeaders(gs)) {
            Sponge.getServer().getPlayer(leader).ifPresent(leader2 -> leader2.sendMessage(Utils.toText("&7Challenger &b" + user.getName() + " &7has joined the &b" + gs.Name + " &7Gym queue!", true)));
        }

    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, CommandSource sender, String[] args) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, Utils.getGymNames(true));
        }
        return null;
    }

}
