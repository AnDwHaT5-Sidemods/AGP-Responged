package agp.andwhat5;

import java.time.Instant;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.storage.NbtKeys;
import com.pixelmonmod.pixelmon.storage.PixelmonStorage;
import com.pixelmonmod.pixelmon.storage.PlayerStorage;

import agp.andwhat5.api.AGPBadgeGivenEvent;
import agp.andwhat5.config.structs.ArenaStruc;
import agp.andwhat5.config.structs.BadgeStruc;
import agp.andwhat5.config.structs.BattleStruc;
import agp.andwhat5.config.structs.DataStruc;
import agp.andwhat5.config.structs.GymStruc;
import agp.andwhat5.config.structs.PlayerStruc;
import agp.andwhat5.config.structs.Vec3dStruc;
import agp.andwhat5.ui.AbstractContainer;
import agp.andwhat5.ui.EnumGUIType;
import agp.andwhat5.ui.display.checkbadges.CheckBadgesContainer;
import agp.andwhat5.ui.display.gymlist.GymListContainer;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class Utils
{
    //TODO: Review and update as needed.
    //TODO: Utility methods for Player <-> EntityPlayerMP

	/**
	 * Saves all data currently in the DataStruc.gcon field
	 *
	 * WARNING: Use only when needed to preserve system performance
	 */
	public static void saveAGPData()
	{
		AGP.getInstance().getStorage().saveData(DataStruc.gcon);
	}

	/**
	 * Returns the data associated with a player in the form of a
	 * {@link PlayerStruc}.
	 *
	 * @param player The player we are checking the data of
	 * @return A {@link PlayerStruc} representation of the player
	 */
	public static PlayerStruc getPlayerData(EntityPlayerMP player)
	{
		if (DataStruc.gcon.PlayerData.containsKey(player.getUniqueID()))
		{
			return DataStruc.gcon.PlayerData.get(player.getUniqueID());
		} else
		{
			return new PlayerStruc(player.getName());
		}
	}

	/**
	 * Attempts to give a player a badge. This data will be assigned to the player's
	 * {@link PlayerStruc} for details later
	 *
	 * @param player The player receiving the badge
	 * @param gs The gym they are receiving the badge from
	 * @param leader The leader giving the badge
	 */
	public static void giveBadge(EntityPlayerMP player, GymStruc gs, String leader)
	{
		BadgeStruc bs = new BadgeStruc(gs.Name, gs.Badge, leader, Date.from(Instant.now()));
		AGP.EVENT_BUS.post(new AGPBadgeGivenEvent(getPlayerData(player), bs));
		
		Optional<PlayerStorage> storage = PixelmonStorage.pokeBallManager.getPlayerStorage(player);
		if (storage.isPresent())
		{
			for (NBTTagCompound nbt : storage.get().getList())
			{
				if (nbt != null)
					bs.Pokemon.add(nbt.getString(NbtKeys.NAME));
			}
		}
		AGP.getInstance().getStorage().updateObtainedBadges(player.getUniqueID(), player.getName(), bs, true);
		saveAGPData();
	}

	//TODO: Better inventory system
	/**
	 * UI main calling function, setups the UI parameters based on {@link EnumGUIType} type
	 * and proceeds to open the UI to the player
	 *
	 * @param player A possibly-differing source other than actor (e.g. Used with Check Badges UI)
	 * @param actor The source of the UI creation
	 * @param type The type of UI to open
	 */
	public static void openGUI(EntityPlayerMP player, EntityPlayerMP actor, EnumGUIType type)
	{
		if (actor.openContainer != actor.inventoryContainer)
		{
			actor.closeScreen();
		}
		AbstractContainer container = null;
		String title = "";
		if (type.equals(EnumGUIType.CheckBadges))
		{
			title = player.getName() + "'s Badges";
			PlayerStruc ps = DataStruc.gcon.PlayerData.get(player.getUniqueID());
			List<BadgeStruc> badges = (ps == null) ? Lists.newArrayList() : ps.Badges;
			int rows = badges.size() / 9 + 1;
			rows = (rows > 6) ? 6 : rows;
			container = new CheckBadgesContainer(new InventoryBasic(title, false, rows * 9), actor);
			container.fillContents(rows, badges);
		} else if (type.equals(EnumGUIType.GymList))
		{
			title = "Gym List";
			List<GymStruc> gyms = DataStruc.gcon.GymData;
			int rows = gyms.size() / 9 + 1;
			rows = (rows > 6) ? 6 : rows;
			container = new GymListContainer(new InventoryBasic(title, false, rows * 9), actor);
			sortGyms();
			container.fillContents(rows, gyms);
		}
		actor.getNextWindowId();
		actor.openContainer = container;
		actor.connection.sendPacket(new SPacketOpenWindow(actor.currentWindowId, "minecraft:container", Utils.toText(title, false), container.getSizeOfInv()));
		actor.openContainer.windowId = actor.currentWindowId;
		actor.openContainer.addListener(actor);
	}
	/**
	 * Broadcasts a message to all players
	 *
	 * @param message The message you wish to be broadcasted.
	 * @param prefix Whether AGP uses its prefix in the announcement. True: Yes False: No.
	 */
	public static void sendToAll(String message, boolean prefix)
	{
		for(EntityPlayerMP p : Utils.getAllPlayers())
		{
			((ICommandSender)p).sendMessage(Utils.toText(message, prefix));
		}
	}
	
	/**
	 * Broadcasts a message to all players
	 *
	 * @param message The message you wish to be broadcasted.
	 */
	public static void sendToAll(ITextComponent message)
	{
		for(EntityPlayerMP p : Utils.getAllPlayers())
		{
			((ICommandSender)p).sendMessage(message);
		}
	}
	

	/**
	 * Adds a gym to the Gyms List
	 *
	 * @param gs The representative {@link GymStruc} for the gym
	 */
	public static void addGym(GymStruc gs)
	{
		AGP.getInstance().getStorage().updateGyms(gs, true);
	}

	/**
	 * Removes a gym from the Gyms List
	 *
	 * @param gs The representative {@link GymStruc} for the gym
	 */
	public static void removeGym(GymStruc gs)
	{
		AGP.getInstance().getStorage().updateGyms(gs, false);
	}

	/**
	 * First removes the old copy of a gym, then adds it back into the list
	 *
	 * @param gs The representative {@link GymStruc} for the gym
	 */
	public static void editGym(GymStruc gs)
	{
		removeGym(getGym(gs.Name));
		addGym(gs);
	}

	/**
	 * Checks to see if a particular gym exists in the system
	 *
	 * @param gym The name of the gym
	 * @return True if it exists, false otherwise
	 */
	public static boolean gymExists(String gym)
	{
		return DataStruc.gcon.GymData.stream().anyMatch(gs -> gs.Name.equalsIgnoreCase(gym));
	}

	/**
	 * Returns a copy of the matching gym
	 *
	 * @param gym The name of the gym
	 * @return A {@link GymStruc} representation of the gym, or null if not found
	 */
	public static GymStruc getGym(String gym)
	{
		return DataStruc.gcon.GymData.stream().filter(gs -> gs.Name.equalsIgnoreCase(gym)).findAny().orElse(null);
	}

	/**
	 * Adds a leader to the passed gym
	 *
	 * @param leader The name of the leader
	 * @param gs The gym we are adding the leader to
	 */
	public static void addLeader(String leader, GymStruc gs)
	{
		gs.Leaders.add(leader);
		editGym(gs);
	}

	/**
	 * Checks to see if a particular player has the passed gym's badge
	 *
	 * @param player The player in question
	 * @param gs The gym being considered
	 * @return True if the player has the gym's badge, false otherwise
	 */
	public static boolean hasBadge(EntityPlayerMP player, GymStruc gs)
	{
		PlayerStruc ps = DataStruc.gcon.PlayerData.get(player.getUniqueID());
		return ps != null && ps.Badges.stream().anyMatch(bs -> bs.Gym.equals(gs.Name));
	}

	//TODO: Thanks Nick.
	/**
	 * We sort the available gyms in alphabetical order.
	 * Now even though this method accepts a boolean argument,
	 * it is always true, and I am not going to be the one to edit
	 * every single command class to alter this. But this method
	 * doesn't need a boolean variable if it is going to do the
	 * same fucking thing no matter what
	 *
	 * @param sort Delete this
	 * @return Sorted names of the gyms
	 */
	public static List<String> getGymNames(boolean sort)
	{
		List<String> gymNames = Lists.newArrayList();
		for (GymStruc gs : DataStruc.gcon.GymData)
		{
			gymNames.add(gs.Name);
		}

		gymNames.sort(String::compareTo);

		return gymNames;
	}

	/**
	 * Like the prior method, gets a list of gyms. Not entirely
	 * sure why this method is needed other than for sorting
	 *
	 * NOTE: This one actually has a use for the sort variable
	 *
	 *
	 * @param sort Whether to sort them or not
	 * @return The gym list
	 */
	public static List<GymStruc> getGymStrucs(boolean sort)
	{
		if (sort)
		{
			sortGyms();
		}
		return DataStruc.gcon.GymData;
	}

	/**
	 * Did you read the method name? No, the javadoc came first.
	 */
	public static void sortGyms()
	{
		DataStruc.gcon.GymData.sort((gym1, gym2) ->
		{
			if (gym1.LevelCap < gym2.LevelCap)
				return -1;
			else if (gym1.LevelCap > gym2.LevelCap)
				return 1;
			else
			{
				return gym1.Name.compareTo(gym2.Name);
			}
		});
	}

	/**
	 * Checks to ensure a player's party matches the specified
	 * level cap of a gym
	 *
	 * @param player Player in question
	 * @param cap The gym's level cap
	 * @return True if passing, false otherwise
	 */
	public static boolean checkLevels(EntityPlayerMP player, int cap)
	{
		if (cap == 0)
		{
			return true;
		}
		Optional<PlayerStorage> ps = PixelmonStorage.pokeBallManager.getPlayerStorage(player);
		for (NBTTagCompound l : ps.get().partyPokemon)
		{
			if (l != null && l.getInteger(NbtKeys.LEVEL) > cap)
			{
				return false;
			}

		}
		return true;
	}

	/**
	 * Obtains an {@link ArenaStruc} object associated with the parameters.
	 * @param gs The {@link GymStruc} associated with the designated arena.
	 * @param aName The name of the arena.
	 */
	public static ArenaStruc getArena(GymStruc gs, String aName)
	{
		return gs.Arenas.stream().filter(a -> a.Name.equalsIgnoreCase(aName)).findAny().orElse(null);
	}

	/**
	 * Returns a list of all the arena names associated with a gym.
	 * @param gs The GymStruc associated with the arena objects.
	 * @param sort Determines whether it returns the list of arenas in alphabetical order.
	 */
	public static List<String> getArenaNames(GymStruc gs, boolean sort) {
		List<String> arenaNames = Lists.newArrayList();
		for (ArenaStruc as : gs.Arenas)
		{
			arenaNames.add(as.Name);
		}
		if (sort)
		{
			arenaNames.sort(Comparator.naturalOrder());
		}
		return arenaNames;
	}

	/**
	 * Checks to see if there is anyone located in the arena. Mainly for active battles.
	 * @param as The {@link ArenaStruc} object you are checking.
	 * @return True if Empty or False if Occupied.
	 */
	public static boolean isArenaEmpty(ArenaStruc as)
	{
		return (as.Leader == null && as.Challenger == null);
	}

	public static void setPosition(EntityPlayerMP player, Vec3dStruc loc)
	{
		player.rotationPitch = loc.pitch;
		player.rotationYaw = loc.yaw;
		player.setPositionAndUpdate(loc.x, loc.y + 1, loc.z);
	}

	public static boolean isOnline(EntityPlayerMP player)
	{
		return getAllPlayers().contains(player);
	}

	public static boolean isAnyLeader(EntityPlayerMP player)
	{
		for (GymStruc gs : DataStruc.gcon.GymData)
		{
			if (gs.Leaders.stream().anyMatch(p -> p.equalsIgnoreCase(player.getName())))
			{
				return true;
			}
		}
		return false;
	}

	public static boolean isGymLeader(EntityPlayerMP player, GymStruc gs)
	{
		return gs.Leaders.stream().anyMatch(p -> p.equalsIgnoreCase(player.getName()));
	}

	public static boolean isInAnyQueue(EntityPlayerMP player)
	{
		for (GymStruc gs : DataStruc.gcon.GymData)
		{
			if (gs.Queue.stream().anyMatch(p -> p.equalsIgnoreCase(player.getName())))
			{
				return true;
			}
		}
		return false;
	}

	public static boolean isInGymQueue(EntityPlayerMP player, GymStruc gs)
	{
		return gs.Queue.stream().anyMatch(p -> p.equalsIgnoreCase(player.getName()));
	}

	public static boolean isInAnyBattle(EntityPlayerMP player)
	{
		return DataStruc.gcon.GymBattlers.stream().anyMatch(bs -> bs.leader.getName().equalsIgnoreCase(player.getName())
				|| bs.challenger.getName().equalsIgnoreCase(player.getName()));
	}

	public static ITextComponent toText(String msg, boolean prefix)
	{
		if (prefix)
		{
			msg = "&f[&dAGP&f] " + msg;
		}
		return new TextComponentString(msg.replaceAll("(&([a-f0-9k-r]))", "\u00A7$2"));
	}

	public static List<EntityPlayerMP> getAllPlayers()
	{
		return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers();
	}

	public static List<EntityPlayerMP> getQueuedPlayers(GymStruc gs)
	{
		List<EntityPlayerMP> toList = Lists.newArrayList();
		for (EntityPlayerMP player : getAllPlayers())
		{
			if (isInGymQueue(player, gs))
			{
				toList.add(player);
			}
		}
		toList.sort(Comparator.comparing(EntityPlayerMP::getName));
		return toList;
	}

	public static List<EntityPlayerMP> getBattlers(BattleStruc bs)
	{
		return Lists.newArrayList(bs.leader, bs.challenger);
	}

	public static List<EntityPlayerMP> getAllLeaders()
	{
		List<EntityPlayerMP> toList = Lists.newArrayList();
		for (EntityPlayerMP player : getAllPlayers())
		{
			if (isAnyLeader(player))
			{
				toList.add(player);
			}
		}
		toList.sort(Comparator.comparing(EntityPlayerMP::getName));
		return toList;
	}

	public static List<EntityPlayerMP> getGymLeaders(GymStruc gs)
	{
		List<EntityPlayerMP> toList = Lists.newArrayList();
		for (EntityPlayerMP player : getAllPlayers())
		{
			if (isGymLeader(player, gs))
			{
				toList.add(player);
			}
		}
		toList.sort(Comparator.comparing(EntityPlayerMP::getName));
		return toList;
	}
}
