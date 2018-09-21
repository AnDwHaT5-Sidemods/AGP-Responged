package agp.andwhat5.commands.gyms;

import agp.andwhat5.commands.utils.PlayerOnlyCommand;
import agp.andwhat5.config.structs.GymStruc;
import agp.andwhat5.gui.GymPokemonGui;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

public class GymPokemon extends PlayerOnlyCommand {

    @Override
    protected CommandResult execute(Player player, CommandContext args) {
        GymStruc gs = args.<GymStruc>getOne("GymName").get();
        //GymPokemonGui.openGymPokemonGui(player, gs);
        GymPokemonGui.openGymPokemonGui(player, gs);
        return CommandResult.success();
    }
}
