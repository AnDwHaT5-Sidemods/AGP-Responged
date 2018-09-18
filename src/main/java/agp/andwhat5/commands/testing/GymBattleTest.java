package agp.andwhat5.commands.testing;

import agp.andwhat5.battles.BattleUtil;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

import java.util.ArrayList;
import java.util.List;

import static agp.andwhat5.battles.BattleUtil.getTempBattlePokemon;

public class GymBattleTest implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {

        Player challenger = args.<Player>getOne("player").get();
        Player leader = (Player) src;

        List<EntityPixelmon> party = new ArrayList<>();
        party.add(getTempBattlePokemon(PokemonSpec.from("pikachu"), leader));
        party.add(getTempBattlePokemon(PokemonSpec.from("psyduck"), leader));

        BattleUtil.startLeaderBattleWithTempTeam(challenger, leader, party);

        return CommandResult.success();
    }

}
