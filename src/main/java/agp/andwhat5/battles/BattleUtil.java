package agp.andwhat5.battles;

import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.battles.controller.BattleControllerBase;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.enums.EnumBossMode;
import com.pixelmonmod.pixelmon.enums.items.EnumPokeballs;
import com.pixelmonmod.pixelmon.storage.PixelmonStorage;
import com.pixelmonmod.pixelmon.storage.PlayerStorage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")
public class BattleUtil {

    //TODO sanity checks
    //Starts a battle between players with two temp teams
    public static void startPlayerBattleWithTempTeams(Player player1, List<EntityPixelmon> player1TempTeam, Player player2, List<EntityPixelmon> player2TempTeam) {

        TempTeamedParticipant player1Participant = new TempTeamedParticipant((EntityPlayerMP) player1);
        player1Participant.allPokemon = new PixelmonWrapper[player1TempTeam.size()];
        player1Participant.controlledPokemon = new ArrayList<>(1);
        for (int i = 0; i < player1TempTeam.size(); i++) {
            player1Participant.allPokemon[i] = new PixelmonWrapper(player1Participant, player1TempTeam.get(i), i);
        }
        player1Participant.controlledPokemon.add(player1Participant.allPokemon[0]);//Only the first one needs to be controlled!!

        TempTeamedParticipant player2Participant = new TempTeamedParticipant((EntityPlayerMP) player2);
        player2Participant.allPokemon = new PixelmonWrapper[player2TempTeam.size()];
        player2Participant.controlledPokemon = new ArrayList<>(1);
        for (int i = 0; i < player2TempTeam.size(); i++) {
            player2Participant.allPokemon[i] = new PixelmonWrapper(player2Participant, player2TempTeam.get(i), i);
        }

        player2Participant.controlledPokemon.add(player2Participant.allPokemon[0]);//Only the first one needs to be controlled!!

        new BattleControllerBase(player1Participant, player2Participant);
    }

    public static void startLeaderBattleWithTempTeam(Player challenger, Player leader, List<EntityPixelmon> leadersTempTeam) {
        PlayerStorage challengerStorage = PixelmonStorage.pokeBallManager.getPlayerStorage((EntityPlayerMP) challenger).get();
        EntityPixelmon challengerFirstPokemon = challengerStorage.getFirstAblePokemon((World) challenger.getWorld());
        if(challengerFirstPokemon == null) {
            challenger.sendMessage(Text.of(TextColors.RED, "You have no pokemon to battle with"));
            leader.sendMessage(Text.of(TextColors.RED, "Challenger has no pokemon to battle with"));
            return;
        }
        PlayerParticipant challengerParticipant = new PlayerParticipant((EntityPlayerMP) challenger, challengerFirstPokemon);


        TempTeamedParticipant leaderParticipant = new TempTeamedParticipant((EntityPlayerMP) leader);
        leaderParticipant.allPokemon = new PixelmonWrapper[leadersTempTeam.size()];
        leaderParticipant.controlledPokemon = new ArrayList<>(1);

        for (int i = 0; i < leadersTempTeam.size(); i++) {
            leaderParticipant.allPokemon[i] = new PixelmonWrapper(leaderParticipant, leadersTempTeam.get(i), i);
        }

        leaderParticipant.controlledPokemon.add(leaderParticipant.allPokemon[0]);//Only the first one needs to be controlled!!

        new BattleControllerBase(leaderParticipant, challengerParticipant);
    }

    //If you break this there is a special place in hell for you
    public static EntityPixelmon getTempBattlePokemon(PokemonSpec spec, Player player) {
        PlayerStorage playerStorage = PixelmonStorage.pokeBallManager.getPlayerStorage((EntityPlayerMP) player).get();
        EntityPixelmon pokemon = spec.create((World) player.getWorld());
        pokemon.setPosition(player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ());//TODO
        pokemon.caughtBall = pokemon.caughtBall == null ? EnumPokeballs.PokeBall : pokemon.caughtBall;
        pokemon.friendship.initFromCapture();
        pokemon.setOwnerId(player.getUniqueId());
        pokemon.playerOwned = true;
        pokemon.loadMoveset();
        pokemon.setBoss(EnumBossMode.NotBoss);
        pokemon.initializeBaseStatsIfNull();
        pokemon.setPokemonId(playerStorage.getNewPokemonID());
        return pokemon;
    }

}
