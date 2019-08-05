package agp.andwhat5.battles;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;

import net.minecraft.entity.player.EntityPlayerMP;

public class TempTeamParticipant extends PlayerParticipant {

    public TempTeamParticipant(EntityPlayerMP p, EntityPixelmon... startingPixelmon) {
        super(p, startingPixelmon);
    }

    public TempTeamParticipant(EntityPlayerMP player, List<Pokemon> team, int starting) {
        super(player, team.stream().peek(pokemon -> pokemon.setDoesLevel(false)).collect(Collectors.toList()), starting);
    }

    @Override
    public PlayerPartyStorage getStorage() {
        if(this.party != null) {
            return this.party;
        }
        
        PlayerPartyStorage storage = new TempPlayerStorage(UUID.randomUUID(), true, "PseudoPsyduckPlaceholder");
        for(Pokemon pokemon : Arrays.stream(this.allPokemon).map(pw -> pw.pokemon).collect(Collectors.toList())) {
            storage.add(pokemon);
        }
        
        return storage;
    }

    @Override
    public void tick() {
        super.tick();
        this.party.guiOpened = true;
    }
}