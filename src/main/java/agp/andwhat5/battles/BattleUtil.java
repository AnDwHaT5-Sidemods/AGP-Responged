package agp.andwhat5.battles;

import com.google.common.collect.Maps;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("Duplicates")
public class BattleUtil {

    public static void startLeaderBattleWithTempTeam(Player challenger, Player leader, List<Pokemon> leadersTempTeam) {
        PlayerPartyStorage challengerStorage = Pixelmon.storageManager.getParty((EntityPlayerMP) challenger);
        PlayerPartyStorage leaderStorage = Pixelmon.storageManager.getParty(leader.getUniqueId());
        challengerStorage.heal();
        if (challengerStorage.countAblePokemon() == 0) {
            challenger.sendMessage(Text.of(TextColors.RED, "You have no pokemon to battle with"));
            leader.sendMessage(Text.of(TextColors.RED, "Challenger has no pokemon to battle with"));
            return;
        }
        EntityPixelmon firstAble = challengerStorage.getAndSendOutFirstAblePokemon(null);
        PlayerParticipant challengerParticipant = new PlayerParticipant((EntityPlayerMP) challenger, firstAble);
        File file = new File("./config/agp/temp-teams/"+leader.getUniqueId()+".party");
        if (!file.getParentFile().exists())
            file.getParentFile().mkdir();
        try {
            CompressedStreamTools.write(leaderStorage.writeToNBT(new NBTTagCompound()), file);
            for (int i = 0; i < 6; i++) {
                leaderStorage.set(i, leadersTempTeam.size() > i ? leadersTempTeam.get(i) : null);
            }
            PlayerParticipant pla = new PlayerParticipant((EntityPlayerMP) leader, leaderStorage.getAndSendOutFirstAblePokemon(null));
            BattleRegistry.startBattle(pla, challengerParticipant);
        } catch (IOException e) {
            e.printStackTrace();
            challenger.sendMessage(Text.of(TextColors.RED, "An error has occurred"));
            leader.sendMessage(Text.of(TextColors.RED, "An error has occurred"));
        }
    }

    /**
     * A custom converter for PixelmonData to EntityPixelmon.
     *
     * @param data   The PixelmonData of the Pokemon you would like to convert.
     * @param player The player the pokemon will belong to.
     * @return An EntityPixelmon value of PixelmonData
     */
    public static Optional<EntityPixelmon> pixelmonDataToTempBattlePokemon(Player player, Pokemon data) {
        if (data.getSpecies() != null) {
            EntityPixelmon pixelmon = new EntityPixelmon((World) player.getWorld());
            pixelmon.setPokemon(data);
            return Optional.of(pixelmon);
        }
        return Optional.empty();
    }

    public static void restoreOriginalTeam(Player player) {
        File file = new File("./config/agp/temp-teams/"+player.getUniqueId()+".party");
        if (file.exists()) {
            PlayerPartyStorage storage = Pixelmon.storageManager.getParty(player.getUniqueId());
            try {
                storage.readFromNBT(CompressedStreamTools.read(file));
                file.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}