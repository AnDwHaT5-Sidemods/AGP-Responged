package agp.andwhat5.battles;

import java.util.UUID;

import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;

public class TempPlayerStorage extends PlayerPartyStorage {

    public TempPlayerStorage(UUID uuid, boolean shouldSendUpdates, String name) {
        super(uuid, shouldSendUpdates);
        this.setPlayerName(name);
    }

    private void setPlayerName(String name) {
        this.playerName = name;
    }
}