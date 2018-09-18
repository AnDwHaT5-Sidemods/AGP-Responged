package agp.andwhat5.commands.leaders;

import agp.andwhat5.Utils;
import agp.andwhat5.commands.utils.PlayerOnlyCommand;
import agp.andwhat5.config.structs.ArenaStruc;
import agp.andwhat5.config.structs.BattleStruc;
import agp.andwhat5.config.structs.DataStruc;
import agp.andwhat5.config.structs.GymStruc;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;
import com.pixelmonmod.pixelmon.battles.controller.BattleControllerBase;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.enums.battle.EnumBattleEndCause;
import com.pixelmonmod.pixelmon.storage.PixelmonStorage;
import com.pixelmonmod.pixelmon.storage.PlayerStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
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

        if(challenger.getName().equalsIgnoreCase(sender.getName()))
        {
            sender.sendMessage(Utils.toText("&7You can not accept a challenge from yourself!", true));
            gym.Queue.remove(0);
            return CommandResult.success();
        }
        ArenaStruc as = optGymArena.orElse(null);
        if(as == null)
        {
            for(ArenaStruc a : gym.Arenas)
            {
                if(a != null) {
                    if (!a.inUse && a.Leader != null && a.Challenger != null) {
                        Utils.setPosition(sender, a.Leader, gym.worldUUID);
                        Utils.setPosition(challenger, a.Challenger, gym.worldUUID);

                        a.inUse = true;
                        as = a;
                        break;
                    }
                }
            }
        }
        else {
            if (as.Leader != null && as.Challenger != null) {
                as.inUse = true;
                Utils.setPosition(sender, as.Leader, gym.worldUUID);
                Utils.setPosition(challenger, as.Challenger, gym.worldUUID);
            }
        }

        Optional<PlayerStorage> leaderTeam = PixelmonStorage.pokeBallManager.getPlayerStorage((EntityPlayerMP) sender);
        Optional<PlayerStorage> challengerTeam = PixelmonStorage.pokeBallManager.getPlayerStorage((EntityPlayerMP) challenger);

        if (leaderTeam.isPresent() && challengerTeam.isPresent()) {
            leaderTeam.get().healAllPokemon((World) sender.getWorld());
            challengerTeam.get().healAllPokemon((World) challenger.getWorld());

            BattleStruc bs = new BattleStruc(gym, as, sender.getUniqueId(), challenger.getUniqueId());
            DataStruc.gcon.GymBattlers.add(bs);
            sender.sendMessage(Utils.toText("&7Initiating battle against &b" + Utils.getNameFromUUID(cUUID) + "&7!", true));
            challenger.sendMessage(Utils.toText("&7Gym Leader &b" + sender.getName() + " &7has accepted your challenge against the &b" + gym.Name + " &bGym!", true));

            EntityPixelmon leaderPkmn1 = leaderTeam.get().getFirstAblePokemon((World) sender.getWorld());
            BattleParticipant ldr = new PlayerParticipant((EntityPlayerMP) sender, leaderPkmn1);
            EntityPixelmon challengerPkmn1 = challengerTeam.get().getFirstAblePokemon((World) challenger.getWorld());
            BattleParticipant chal = new PlayerParticipant((EntityPlayerMP) challenger, challengerPkmn1);

            ldr.startedBattle = true;
            BattleParticipant[] team1 = new BattleParticipant[]{ldr};
            BattleParticipant[] team2 = new BattleParticipant[]{chal};
            new BattleControllerBase(team1, team2);
        } else {
            sender.sendMessage(Utils.toText("&7An error occurred starting the gym battle!", true));
            challenger.sendMessage(Utils.toText("&7An error occurred starting the gym battle!", true));
        }

        return CommandResult.success();
    }

}
