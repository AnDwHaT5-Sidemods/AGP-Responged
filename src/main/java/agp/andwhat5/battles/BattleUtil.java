package agp.andwhat5.battles;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.battles.controller.BattleControllerBase;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.battles.rules.BattleRules;
import com.pixelmonmod.pixelmon.config.PixelmonEntityList;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.enums.EnumBossMode;
import com.pixelmonmod.pixelmon.enums.battle.EnumBattleType;
import com.pixelmonmod.pixelmon.enums.items.EnumPokeballs;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

        new BattleControllerBase(new TempTeamedParticipant[]{player1Participant}, new TempTeamedParticipant[]{player2Participant}, new BattleRules(EnumBattleType.Single));
    }

    public static void startLeaderBattleWithTempTeam(Player challenger, Player leader, List<EntityPixelmon> leadersTempTeam) {
        PlayerPartyStorage challengerStorage = Pixelmon.storageManager.getParty((EntityPlayerMP) challenger);
        if(challengerStorage.countAblePokemon() == 0) {
            challenger.sendMessage(Text.of(TextColors.RED, "You have no pokemon to battle with"));
            leader.sendMessage(Text.of(TextColors.RED, "Challenger has no pokemon to battle with"));
            return;
        }
        EntityPixelmon firstAble = challengerStorage.getAndSendOutFirstAblePokemon(null);
        PlayerParticipant challengerParticipant = new PlayerParticipant((EntityPlayerMP) challenger, firstAble);


        TempTeamedParticipant leaderParticipant = new TempTeamedParticipant((EntityPlayerMP) leader);
        leaderParticipant.allPokemon = new PixelmonWrapper[leadersTempTeam.size()];
        leaderParticipant.controlledPokemon = new ArrayList<>(1);

        for (int i = 0; i < leadersTempTeam.size(); i++) {
            leaderParticipant.allPokemon[i] = new PixelmonWrapper(leaderParticipant, leadersTempTeam.get(i), i);
        }

        leaderParticipant.controlledPokemon.add(leaderParticipant.allPokemon[0]);//Only the first one needs to be controlled!!

        new BattleControllerBase(new TempTeamedParticipant[]{leaderParticipant}, new PlayerParticipant[]{challengerParticipant}, new BattleRules(EnumBattleType.Single));
    }

    //If you break this there is a special place in hell for you
    public static EntityPixelmon getTempBattlePokemon(PokemonSpec spec, Player player) {
        //PlayerStorage playerStorage = PixelmonStorage.pokeBallManager.getPlayerStorage((EntityPlayerMP) player).get();
        EntityPixelmon pokemon = spec.create((World) player.getWorld());
        pokemon.setPosition(player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ());//TODO
        pokemon.getPokemonData().setCaughtBall(pokemon.getPokemonData().getCaughtBall() == null ? EnumPokeballs.PokeBall : pokemon.getPokemonData().getCaughtBall());
//        pokemon.getPokemonData().getFriendship().initFromCapture();//TODO
        pokemon.setOwnerId(player.getUniqueId());
        //pokemon.playerOwned = true;
        pokemon.getPokemonData().getMoveset();//TODO is this needed?
        pokemon.setBoss(EnumBossMode.NotBoss);
        //pokemon.initializeBaseStatsIfNull();//TODO
        pokemon.setUniqueId(UUID.randomUUID());
        return pokemon;
    }

    /**
     * A custom converter for PixelmonData to EntityPixelmon.
     * @param data The PixelmonData of the Pokemon you would like to convert.
     * @param player The player the pokemon will belong to.
     * @return An EntityPixelmon value of PixelmonData
     */
    public static Optional<EntityPixelmon> pixelmonDataToTempBattlePokemon(Player player, Pokemon data)
    {
        if(data.getSpecies() != null) {
            EntityPixelmon pixelmon = (EntityPixelmon) PixelmonEntityList.createEntityByName(data.getSpecies().name, (World) player.getWorld());
            if(!data.getNickname().isEmpty())
                pixelmon.getPokemonData().setNickname(data.getNickname());
            if(data.getLevel() > 0 && data.getLevel() <= 100)
                pixelmon.getLvl().setLevel(data.getLevel());
            pixelmon.getPokemonData().setHealth(data.getHealth());
            pixelmon.getPokemonData().setFriendship(data.getFriendship());
            if(data.getGender() != null)
                pixelmon.getPokemonData().setGender(data.getGender());
            pixelmon.getPokemonData().setShiny(data.isShiny());
            //if(data.getHeldItem() != null)
                pixelmon.getPokemonData().setHeldItem(data.getHeldItem());
            pixelmon.getLvl().setExp(data.getExperience());
            if(data.getNature() != null)
                pixelmon.getPokemonData().setNature(data.getNature());
            if(data.getGrowth() != null)
                pixelmon.getPokemonData().setGrowth(data.getGrowth());
            if(data.getCaughtBall() != null)
                pixelmon.getPokemonData().setCaughtBall(data.getCaughtBall());
            pixelmon.setForm(data.getForm());
            if(data.getMoveset() != null)
            {
                for (int i = 0; i < 4; i++) {
                    pixelmon.getPokemonData().getMoveset().set(i, data.getMoveset().get(i));
                }
            }
            if(data.getAbility() != null)
                pixelmon.getPokemonData().setAbility(data.getAbility());
            if (data.getEVs() != null) {
                pixelmon.getPokemonData().getEVs().set(StatsType.Speed, data.getEVs().get(StatsType.Speed));
                pixelmon.getPokemonData().getEVs().set(StatsType.SpecialAttack, data.getEVs().get(StatsType.SpecialAttack));
                pixelmon.getPokemonData().getEVs().set(StatsType.SpecialDefence, data.getEVs().get(StatsType.SpecialDefence));
                pixelmon.getPokemonData().getEVs().set(StatsType.Defence, data.getEVs().get(StatsType.Defence));
                pixelmon.getPokemonData().getEVs().set(StatsType.Attack, data.getEVs().get(StatsType.Attack));
                pixelmon.getPokemonData().getEVs().set(StatsType.HP, data.getEVs().get(StatsType.HP));
            }
            if (data.getIVs() != null) {
                pixelmon.getPokemonData().getIVs().set(StatsType.Speed, data.getIVs().get(StatsType.Speed));
                pixelmon.getPokemonData().getIVs().set(StatsType.SpecialAttack, data.getIVs().get(StatsType.SpecialAttack));
                pixelmon.getPokemonData().getIVs().set(StatsType.SpecialDefence, data.getIVs().get(StatsType.SpecialDefence));
                pixelmon.getPokemonData().getIVs().set(StatsType.Defence, data.getIVs().get(StatsType.Defence));
                pixelmon.getPokemonData().getIVs().set(StatsType.Attack, data.getIVs().get(StatsType.Attack));
                pixelmon.getPokemonData().getIVs().set(StatsType.HP, data.getIVs().get(StatsType.HP));
            }

            //PlayerStorage playerStorage = PixelmonStorage.pokeBallManager.getPlayerStorage((EntityPlayerMP) player).get();
            pixelmon.setPosition(player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ());//TODO
            //pixelmon.caughtBall = pixelmon.caughtBall == null ? EnumPokeballs.PokeBall : pixelmon.caughtBall;
            //pixelmon.friendship.initFromCapture();
            pixelmon.setOwnerId(player.getUniqueId());
            //pixelmon.playerOwned = true;
            //pixelmon.loadMoveset();
            //pixelmon.setBoss(EnumBossMode.NotBoss);
            //pixelmon.initializeBaseStatsIfNull();
            pixelmon.setUniqueId(UUID.randomUUID());

            //Apply EV/IV changes
            pixelmon.updateStats();

            pixelmon.setHealth(pixelmon.getMaxHealth());

            return Optional.of(pixelmon);
        }
        return Optional.empty();
    }



}
