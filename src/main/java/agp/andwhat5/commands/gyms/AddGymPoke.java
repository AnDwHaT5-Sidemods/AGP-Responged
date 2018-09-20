package agp.andwhat5.commands.gyms;

import agp.andwhat5.Utils;
import agp.andwhat5.commands.utils.PlayerOnlyCommand;
import agp.andwhat5.config.structs.GymStruc;
import com.pixelmonmod.pixelmon.client.gui.pokemoneditor.ImportExportConverter;
import com.pixelmonmod.pixelmon.config.PixelmonEntityList;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.storage.PixelmonStorage;
import com.pixelmonmod.pixelmon.storage.PlayerStorage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

public class AddGymPoke extends PlayerOnlyCommand {
    @Override
    protected CommandResult execute(Player player, CommandContext args) {
        int slot = args.<Integer>getOne("slot").get() + 1;
        GymStruc gym = args.<GymStruc>getOne("GymName").get();

        PlayerStorage storage = PixelmonStorage.pokeBallManager.getPlayerStorage((EntityPlayerMP) player).get();
        if(storage.partyPokemon[slot] == null)
        {
            player.sendMessage(Utils.toText("&7There is no Pokemon in the specified slot.", true));
            return CommandResult.success();
        }
        EntityPixelmon pixelmon = (EntityPixelmon)PixelmonEntityList.createEntityFromNBT(storage.partyPokemon[slot], (World) player.getWorld());
        gym.pokemon.add(ImportExportConverter.getExportText(Utils.entityPixelmonToPixelmonData(pixelmon)));
        Utils.saveAGPData();
        player.sendMessage(Utils.toText("&7Successfully added that Pokemon to the gym pool.", true));
        return CommandResult.success();
    }
}
