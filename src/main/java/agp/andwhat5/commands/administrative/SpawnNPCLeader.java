package agp.andwhat5.commands.administrative;

import agp.andwhat5.commands.Command;
import agp.andwhat5.Utils;

import agp.andwhat5.config.structs.GymStruc;

import com.google.common.collect.Lists;
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
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class SpawnNPCLeader extends Command
{

	public SpawnNPCLeader()
	{
		super("spawnnpcleader", "/spawnnpcleader <gym>");
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		EntityPlayerMP user = requireEntityPlayer(sender);
		if (args.length == 1)
		{
			GymStruc gs = Utils.getGym(args[0]);
			if(gs == null){
				throw new CommandException("Invalid gym name");
			}

			NPCTrainer trainer = new NPCTrainer(user.world);
			trainer.getEntityData().setString("GymLeader", gs.Name);
			trainer.init(NPCRegistryTrainers.Steve);
			trainer.setPosition(sender.getPosition().getX() + 0.5F, sender.getPosition().getY() + 1,
					sender.getPosition().getZ() + 0.5F);
			trainer.setAIMode(EnumTrainerAI.StandStill);
			trainer.ignoreDespawnCounter = true;
			trainer.initAI();
			Pixelmon.proxy.spawnEntitySafely(trainer, user.world);
			trainer.setPosition(sender.getPosition().getX() + 0.5F, sender.getPosition().getY() + 1,
					sender.getPosition().getZ() + 0.5F);
			trainer.setStartRotationYaw(180);
			trainer.ignoreDespawnCounter = true;

			Pixelmon.network.sendTo(new ClearTrainerPokemon(), user);
			for (int i = 0; i < trainer.getPokemonStorage().count(); i++)
			{
				Pixelmon.network.sendTo(new StoreTrainerPokemon(new PixelmonData(trainer.getPokemonStorage().getList()[i])), user);
			}
			SetTrainerData trainerData = new SetTrainerData(trainer, "en_US");
			Pixelmon.network.sendTo(new SetNPCEditData(trainerData), user);
			// List<SpawnData> npcs =
			// SpawnRegistry.getNPCSpawnsForBiome(sender.getEntityWorld().getBiome(sender.getPosition()).getBiomeName());
			// sender.getEntityWorld().spawnEntityInWorld(trainer);
			sender.sendMessage(Utils.toText("&7Successfully spawned &b" + gs.Name + " &7Gym Leader! Edit using the NPC Editor!", true));
		} else
		{
			super.sendUsage(sender);
		}
	}

	@Override
	public List<String> getAliases() {
		return Lists.newArrayList("snl", "spawnleader");
	}
	
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
	{
		if (args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, Utils.getGymNames(true));
		}
		return null;
	}
}
