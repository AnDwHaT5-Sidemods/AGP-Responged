package agp.andwhat5.commands.administrative;

import agp.andwhat5.Utils;
import agp.andwhat5.commands.utils.PlayerOnlyCommand;
import agp.andwhat5.config.structs.GymStruc;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.comm.SetTrainerData;
import com.pixelmonmod.pixelmon.comm.packetHandlers.ClearTrainerPokemon;
import com.pixelmonmod.pixelmon.comm.packetHandlers.npc.SetNPCEditData;
import com.pixelmonmod.pixelmon.comm.packetHandlers.npc.StoreTrainerPokemon;
import com.pixelmonmod.pixelmon.entities.npcs.NPCTrainer;
import com.pixelmonmod.pixelmon.entities.npcs.registry.NPCRegistryTrainers;
import com.pixelmonmod.pixelmon.enums.EnumTrainerAI;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

public class SpawnNPCLeader extends PlayerOnlyCommand {

    @Override
    public CommandResult execute(Player src, CommandContext args) {
        GymStruc gym = args.<GymStruc>getOne("GymName").get();

        NPCTrainer trainer = new NPCTrainer((World) src.getWorld());
        trainer.getEntityData().setString("GymLeader", gym.Name);//TODO pixelmon already uses this !?
        trainer.init(NPCRegistryTrainers.Steve);
        trainer.setPosition(src.getPosition().getX() + 0.5F, src.getPosition().getY() + 1, src.getPosition().getZ() + 0.5F);
        trainer.setAIMode(EnumTrainerAI.StandStill);
        trainer.ignoreDespawnCounter = true;
        trainer.initAI();
        Pixelmon.proxy.spawnEntitySafely(trainer, (World) src.getWorld());
        trainer.setPosition(src.getPosition().getX() + 0.5F, src.getPosition().getY() + 1, src.getPosition().getZ() + 0.5F);
        trainer.setStartRotationYaw(180);
        trainer.ignoreDespawnCounter = true;

        Pixelmon.network.sendTo(new ClearTrainerPokemon(), (EntityPlayerMP) src);
        for (int i = 0; i < trainer.getPokemonStorage().countAll(); i++) {
            Pixelmon.network.sendTo(new StoreTrainerPokemon(trainer.getPokemonStorage().get(i)), (EntityPlayerMP) src);
        }
        SetTrainerData trainerData = new SetTrainerData(trainer, "en_US");
        Pixelmon.network.sendTo(new SetNPCEditData(trainerData), (EntityPlayerMP) src);
        src.sendMessage(Utils.toText("&7Successfully spawned &b" + gym.Name + " &7Gym Leader! Edit using the NPC Editor!", true));

        return CommandResult.success();
    }

}
