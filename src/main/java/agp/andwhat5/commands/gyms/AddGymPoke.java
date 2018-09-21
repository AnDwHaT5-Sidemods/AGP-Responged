package agp.andwhat5.commands.gyms;

import agp.andwhat5.Utils;
import agp.andwhat5.commands.utils.PlayerOnlyCommand;
import agp.andwhat5.config.structs.GymStruc;
import agp.andwhat5.config.structs.ShowdownStruc;
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

import java.util.UUID;

public class AddGymPoke extends PlayerOnlyCommand {
    @Override
    protected CommandResult execute(Player player, CommandContext args) {
        int slot = args.<Integer>getOne("slot").get();
        GymStruc gym = args.<GymStruc>getOne("GymName").get();
        if(slot >= 1 && slot <= 6) {
            slot = slot - 1;
            PlayerStorage storage = PixelmonStorage.pokeBallManager.getPlayerStorage((EntityPlayerMP) player).get();
            if (storage.partyPokemon[slot] == null) {
                player.sendMessage(Utils.toText("&7There is no Pokemon in the specified slot.", true));
                return CommandResult.success();
            }
            EntityPixelmon pixelmon = (EntityPixelmon) PixelmonEntityList.createEntityFromNBT(storage.partyPokemon[slot], (World) player.getWorld());
            ShowdownStruc struc = new ShowdownStruc();
            struc.showdownCode = ImportExportConverter.getExportText(Utils.entityPixelmonToPixelmonData(pixelmon));
            struc.uuid = UUID.randomUUID();

            gym.Pokemon.add(struc);
            Utils.saveAGPData();
            player.sendMessage(Utils.toText("&7Successfully added that Pokemon to the gym pool.", true));
            return CommandResult.success();
        }
        else
        {
            player.sendMessage(Utils.toText("&7Enter a slot between 1 and 6.", true));
            return CommandResult.success();

        }
    }
}
