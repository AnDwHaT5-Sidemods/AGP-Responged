package agp.andwhat5.commands.administrative;

import agp.andwhat5.Utils;
import agp.andwhat5.commands.Command;
import agp.andwhat5.config.structs.GymStruc;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.comm.PixelmonData;
import com.pixelmonmod.pixelmon.comm.SetTrainerData;
import com.pixelmonmod.pixelmon.comm.packetHandlers.ClearTrainerPokemon;
import com.pixelmonmod.pixelmon.comm.packetHandlers.npc.SetNPCEditData;
import com.pixelmonmod.pixelmon.comm.packetHandlers.npc.StoreTrainerPokemon;
import com.pixelmonmod.pixelmon.entities.npcs.NPCTrainer;
import com.pixelmonmod.pixelmon.entities.npcs.registry.NPCRegistryTrainers;
import com.pixelmonmod.pixelmon.enums.EnumTrainerAI;
import net.minecraft.command.CommandException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

import java.util.List;

import static net.minecraft.command.CommandBase.getListOfStringsMatchingLastWord;

public class SpawnNPCLeader extends Command {

    public SpawnNPCLeader() {
        super("/spawnnpcleader <gym>");
    }

    @Override
    public void execute(MinecraftServer server, CommandSource sender, String[] args) throws CommandException {
        Player user = requireEntityPlayer(sender);
        if (args.length != 1) {
            super.sendUsage(sender);
            return;
        }

        GymStruc gs = Utils.getGym(args[0]);
        if (gs == null) {
            throw new CommandException("Invalid gym name");
        }

        NPCTrainer trainer = new NPCTrainer((World) user.getWorld());
        trainer.getEntityData().setString("GymLeader", gs.Name);//TODO pixelmon already uses this !?
        trainer.init(NPCRegistryTrainers.Steve);
        trainer.setPosition(user.getPosition().getX() + 0.5F, user.getPosition().getY() + 1, user.getPosition().getZ() + 0.5F);
        trainer.setAIMode(EnumTrainerAI.StandStill);
        trainer.ignoreDespawnCounter = true;
        trainer.initAI();
        Pixelmon.proxy.spawnEntitySafely(trainer, (World) user.getWorld());
        trainer.setPosition(user.getPosition().getX() + 0.5F, user.getPosition().getY() + 1, user.getPosition().getZ() + 0.5F);
        trainer.setStartRotationYaw(180);
        trainer.ignoreDespawnCounter = true;

        Pixelmon.network.sendTo(new ClearTrainerPokemon(), (EntityPlayerMP) user);
        for (int i = 0; i < trainer.getPokemonStorage().count(); i++) {
            Pixelmon.network.sendTo(new StoreTrainerPokemon(new PixelmonData(trainer.getPokemonStorage().getList()[i])), (EntityPlayerMP) user);
        }
        SetTrainerData trainerData = new SetTrainerData(trainer, "en_US");
        Pixelmon.network.sendTo(new SetNPCEditData(trainerData), (EntityPlayerMP) user);
        sender.sendMessage(Utils.toText("&7Successfully spawned &b" + gs.Name + " &7Gym Leader! Edit using the NPC Editor!", true));

    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, CommandSource sender, String[] args) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, Utils.getGymNames(true));
        }
        return null;
    }

}
