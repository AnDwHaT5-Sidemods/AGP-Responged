package agp.andwhat5.battles;

import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.comm.ChatHandler;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.Arrays;
import java.util.Optional;

public class TempTeamedParticipant extends PlayerParticipant {

    public TempTeamedParticipant(EntityPlayerMP p, EntityPixelmon... startingPixelmon) {
        super(p, startingPixelmon);
    }

    @Override
    public PixelmonWrapper switchPokemon(PixelmonWrapper pw, int[] newPixelmonId) {
        double x = this.player.posX;
        double y = this.player.posY;
        double z = this.player.posZ;
        String beforeName = pw.getNickname();
        pw.beforeSwitch();
        if (!pw.isFainted() && !pw.nextSwitchIsMove) {
            ChatHandler.sendBattleMessage(this.player, "playerparticipant.enough", pw.getNickname());
            this.bc.sendToOthers("playerparticipant.withdrew", this, this.player.getDisplayName().getUnformattedText(), beforeName);
        }

        PixelmonWrapper newWrapper = null;

        for (PixelmonWrapper pixelmonWrapper : allPokemon) {
            if(Arrays.equals(pixelmonWrapper.getPokemonID(), newPixelmonId)) {
                newWrapper = pixelmonWrapper;
                //TODO bail out if this is null
                break;
            }
        }

        if (!this.bc.simulateMode) {
            pw.pokemon.catchInPokeball();
            Optional<EntityPixelmon> newPokemonOptional = this.storage.getAlreadyExists(newPixelmonId, this.player.world);
            EntityPixelmon newPixelmon = null;
            if (newPokemonOptional.isPresent()) {
                newPixelmon = newPokemonOptional.get();
                newPixelmon.motionX = newPixelmon.motionY = newPixelmon.motionZ = 0.0D;
                newPixelmon.setLocationAndAngles(x, y, z, this.player.rotationYaw, 0.0F);
            } else {
                newPixelmon = newWrapper.pokemon;
                getWorld().spawnEntity(newPixelmon);
                newPixelmon.motionX = newPixelmon.motionY = newPixelmon.motionZ = 0.0D;
                newPixelmon.setLocationAndAngles(x, y, z, this.player.rotationYaw, 0.0F);
                newPixelmon.releaseFromPokeball();
            }

            newWrapper.pokemon = newPixelmon;
        }

        newWrapper.battlePosition = pw.battlePosition;
        newWrapper.getBattleAbility().beforeSwitch(newWrapper);
        String newNickname = newWrapper.getNickname();
        ChatHandler.sendBattleMessage(this.player, "playerparticipant.go", newNickname);
        this.bc.sendToOthers("battlecontroller.sendout", this, this.player.getDisplayName().getUnformattedText(), newNickname);
        int index = this.controlledPokemon.indexOf(pw);
        this.controlledPokemon.set(index, newWrapper);
        this.bc.participants.forEach(BattleParticipant::updateOtherPokemon);
        newWrapper.afterSwitch();
        return newWrapper;
    }

}
