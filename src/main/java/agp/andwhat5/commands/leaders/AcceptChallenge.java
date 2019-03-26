package agp.andwhat5.commands.leaders;

import agp.andwhat5.Utils;
import agp.andwhat5.commands.utils.PlayerOnlyCommand;
import agp.andwhat5.config.structs.ArenaStruc;
import agp.andwhat5.config.structs.GymStruc;
import agp.andwhat5.gui.ChooseTeamGui;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;
import com.pixelmonmod.pixelmon.battles.controller.BattleControllerBase;
import com.pixelmonmod.pixelmon.enums.battle.EnumBattleEndCause;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;
import java.util.UUID;

public class AcceptChallenge extends PlayerOnlyCommand {

    @Override
    public CommandResult execute(Player sender, CommandContext args) {

        GymStruc gym = args.<GymStruc>getOne("GymName").get();
        Optional<ArenaStruc> optGymArena = args.getOne("GymArena");

        if (!Utils.isGymLeader(sender, gym) && !sender.hasPermission("agp.headleader")) {
            sender.sendMessage(Utils.toText("&7You are not a leader of the &b" + gym.Name + " &7Gym!", true));
            return CommandResult.success();
        }

        if (gym.Queue.isEmpty()) {
            sender.sendMessage(Utils.toText("&7The &b" + gym.Name + " &7Gym queue is empty!", true));
            return CommandResult.success();
        }
        if (!Utils.checkLevels(sender, gym.LevelCap)) {
            sender.sendMessage(Utils.toText("&7Your team is above the level cap for the &b" + gym.Name + " &7Gym!", true));
            return CommandResult.success();
        }

        UUID cUUID = gym.Queue.get(0);
        gym.Queue.remove(0);
        Optional<Player> optChallenger = Sponge.getServer().getPlayer(cUUID);
        if (!optChallenger.isPresent()) {
            sender.sendMessage(Utils.toText("&7Player &b" + Utils.getNameFromUUID(cUUID) + " &7was not found on the server!", true));
            return CommandResult.success();
        }

        Player challenger = optChallenger.get();
        if (!Utils.checkLevels(challenger, gym.LevelCap)) {
            sender.sendMessage(Utils.toText("&7Player &b" + Utils.getNameFromUUID(cUUID) + "&7's team is above the level cap for the &b" + gym.Name + " &7Gym!", true));
            challenger.sendMessage(Utils.toText("&7Your team is above the level cap for the &b" + gym.Name + " &7Gym!", true));
            return CommandResult.success();
        }

        if (BattleRegistry.getBattle((EntityPlayer) challenger) != null) {
            BattleControllerBase battlecontroller = BattleRegistry.getBattle((EntityPlayer) challenger);
            battlecontroller.endBattle(EnumBattleEndCause.NORMAL);
        }

        if (BattleRegistry.getBattle((EntityPlayer) sender) != null) {
            BattleControllerBase battlecontroller = BattleRegistry.getBattle((EntityPlayer) sender);
            battlecontroller.endBattle(EnumBattleEndCause.NORMAL);
        }

        if (challenger.getName().equalsIgnoreCase(sender.getName())) {
            sender.sendMessage(Utils.toText("&7You can not accept a challenge from yourself!", true));
            gym.Queue.remove(0);
            return CommandResult.success();
        }
        ChooseTeamGui gui = new ChooseTeamGui();
        gui.openChooseTeamGui(sender, cUUID, gym, optGymArena.orElse(null));
        return CommandResult.success();
    }

}
