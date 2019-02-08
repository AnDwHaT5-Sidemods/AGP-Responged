package agp.andwhat5.battles;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.listener.EntityPlayerExtension;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.spongepowered.api.entity.living.player.Player;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TempPlayerPartyStorage extends PlayerPartyStorage {

    static protected Map<UUID, TempPlayerPartyStorage> parties = new ConcurrentHashMap<>();

    private final UUID realPlayerUUID;

    public TempPlayerPartyStorage(UUID tempUuid, UUID realPlayerUUID) {
        super(tempUuid);
        this.realPlayerUUID = realPlayerUUID;
    }

    public static boolean checkFakeUUIDExists(UUID uuid) {
        for (Map.Entry<UUID, TempPlayerPartyStorage> uuidTempPlayerPartyStorageEntry : parties.entrySet()) {
            if (uuidTempPlayerPartyStorageEntry.getValue().uuid == uuid) {
                return true;
            }
        }
        return false;
    }

    public static void removeTempStorage(Player player) {
        parties.remove(player.getUniqueId());

        //Fix visual bug
        PlayerPartyStorage party = Pixelmon.storageManager.getParty((EntityPlayerMP) player);
        party.heal();
    }

    @Override
    public UUID getPlayerUUID() {
        return super.getPlayerUUID();
    }

    @Nullable
    @Override
    public EntityPlayerMP getPlayer() {
        return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(this.realPlayerUUID);
    }

}
