package agp.andwhat5;

import agp.andwhat5.api.AGPBadgeGivenEvent;
import agp.andwhat5.config.AGPConfig;
import agp.andwhat5.config.structs.*;
import agp.andwhat5.exceptions.AGPException;
import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.config.PixelmonItems;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.storage.NbtKeys;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.util.helpers.SpriteHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.World;

import java.time.Instant;
import java.util.*;

@SuppressWarnings("Duplicates")
public class Utils {
    //TODO: Utility methods for Player <-> EntityPlayerMP

    /**
     * Saves all data currently in the DataStruc.gcon field
     * <p>
     * WARNING: Use only when needed to preserve system performance
     */
    public static void saveAGPData() {
        AGP.getInstance().getStorage().saveData(DataStruc.gcon);
    }

    /**
     * Returns the data associated with a player in the form of a
     * {@link PlayerStruc}.
     *
     * @param player The player we are checking the data of
     * @return A {@link PlayerStruc} representation of the player
     */
    public static PlayerStruc getPlayerData(Player player) {
        return DataStruc.gcon.PlayerData.getOrDefault(player.getUniqueId(), new PlayerStruc(player.getUniqueId()));
    }

    /**
     * Attempts to give a player a badge. This data will be assigned to the player's
     * {@link PlayerStruc} for details later
     *
     * @param player The player receiving the badge
     * @param gs     The gym they are receiving the badge from
     * @param leader The leader giving the badge
     */
    public static void giveBadge(Player player, GymStruc gs, String leader) {
        BadgeStruc bs = new BadgeStruc(gs.Name, gs.Badge, leader, Date.from(Instant.now()));

        try (CauseStackManager.StackFrame frame = Sponge.getCauseStackManager().pushCauseFrame()) {
            Sponge.getEventManager().post(new AGPBadgeGivenEvent(getPlayerData(player), bs, frame.getCurrentCause()));
        }

        PlayerPartyStorage storage = Pixelmon.storageManager.getParty((EntityPlayerMP) player);
        for (Pokemon pokemon : storage.getAll()) {
            if (pokemon != null)
                bs.Pokemon.add(pokemon.getDisplayName());
        }
        AGP.getInstance().getStorage().updateObtainedBadges(player.getUniqueId(), player.getName(), bs, true);
        saveAGPData();
    }

    public static String getNameFromUUID(UUID uuid) {
        UserStorageService userStorage = Sponge.getServiceManager().provide(UserStorageService.class).get();
        Optional<User> userop = userStorage.get(uuid);
        return userop.map(User::getName).orElse("INVALID USER");
    }

    public static UUID getUUIDFromName(String name) {
        UserStorageService userStorageService = Sponge.getServiceManager().provide(UserStorageService.class).get();
        Optional<User> user = userStorageService.get(name);
        UUID uuid = null;
        if (!user.isPresent()) {
            //Attempt 2, grab from the cache file incase the player files were wiped

            User user1 = Sponge.getServiceManager().provideUnchecked(UserStorageService.class).get(name).orElse(null);
            if (user1 != null) {
                uuid = user1.getUniqueId();
            }
        } else {
            uuid = user.get().getUniqueId();
        }
        return uuid;
    }

    /**
     * Broadcasts a message to all players
     *
     * @param message The message you wish to be broadcasted.
     * @param prefix  Whether AGP uses its prefix in the announcement. True: Yes False: No.
     */
    public static void sendToAll(String message, boolean prefix) {
        Sponge.getServer().getBroadcastChannel().send(Utils.toText(message, prefix));
    }

    /**
     * Adds a gym to the Gyms List
     *
     * @param gs The representative {@link GymStruc} for the gym
     */
    public static void addGym(GymStruc gs) {
        AGP.getInstance().getStorage().updateGyms(gs, true);
    }

    /**
     * Removes a gym from the Gyms List
     *
     * @param gs The representative {@link GymStruc} for the gym
     */
    public static void removeGym(GymStruc gs) {
        AGP.getInstance().getStorage().updateGyms(gs, false);
    }

    /**
     * First removes the old copy of a gym, then adds it back into the list
     *
     * @param gs The representative {@link GymStruc} for the gym
     */
    public static void editGym(GymStruc gs) {
        removeGym(getGym(gs.Name));
        addGym(gs);
    }

    /**
     * Checks to see if a particular gym exists in the system
     *
     * @param gym The name of the gym
     * @return True if it exists, false otherwise
     */
    public static boolean gymExists(String gym) {
        return DataStruc.gcon.GymData.stream().anyMatch(gs -> gs.Name.equalsIgnoreCase(gym));
    }

    /**
     * Returns a copy of the matching gym
     *
     * @param gym The name of the gym
     * @return A {@link GymStruc} representation of the gym, or null if not found
     */
    public static GymStruc getGym(String gym) {
        return DataStruc.gcon.GymData.stream().filter(gs -> gs.Name.equalsIgnoreCase(gym)).findAny().orElse(null);
    }

    /**
     * Adds a leader to the passed gym
     *
     * @param leader The name of the leader
     * @param gs     The gym we are adding the leader to
     */
    public static void addLeader(UUID leader, GymStruc gs) {
        gs.PlayerLeaders.add(leader);
        editGym(gs);
    }

    /**
     * Checks to see if a particular player has the passed gym's badge
     *
     * @param player The player in question
     * @param gs     The gym being considered
     * @return True if the player has the gym's badge, false otherwise
     */
    public static boolean hasBadge(Player player, GymStruc gs) {
        PlayerStruc ps = DataStruc.gcon.PlayerData.get(player.getUniqueId());
        return ps != null && ps.Badges.stream().anyMatch(bs -> bs.Gym.equals(gs.Name));
    }

    /**
     * Checks to see if a particular player has the passed gym's badge
     *
     * @param player The player in question
     * @param gs     The gym being considered
     * @return True if the player has the gym's badge, false otherwise
     */
    public static boolean hasBadge(UUID player, GymStruc gs) {
        PlayerStruc ps = DataStruc.gcon.PlayerData.get(player);
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
    public static List<String> getGymNames(boolean sort) {
        List<String> gymNames = Lists.newArrayList();
        sortGyms();
        DataStruc.gcon.GymData.forEach(gym -> gymNames.add(gym.Name));
        return gymNames;
    }

    /**
     * Like the prior method, gets a list of gyms. Not entirely
     * sure why this method is needed other than for sorting
     * <p>
     * NOTE: This one actually has a use for the sort variable
     *
     * @param sort Whether to sort them or not
     * @return The gym list
     */
    public static List<GymStruc> getGymStrucs(boolean sort) {
        if (sort) {
            sortGyms();
        }
        return DataStruc.gcon.GymData;
    }

    /**
     * Did you read the method name? No, the javadoc came first.
     */
    public static void sortGyms() {
        DataStruc.gcon.GymData.sort((e1, e2) -> (Integer.compare(e1.Weight, e2.Weight)));

        //DataStruc.gcon.GymData.sort(Comparator.comparing(e -> e.Weight);
    }

    /**
     * Checks to ensure a player's party matches the specified
     * level cap of a gym
     *
     * @param player Player in question
     * @param cap    The gym's level cap
     * @return True if passing, false otherwise
     */
    public static boolean checkLevels(Player player, int cap) {
        if (cap == 0) {
            return true;
        }
        PlayerPartyStorage storage = Pixelmon.storageManager.getParty((EntityPlayerMP) player);
        for (Pokemon pokemon : storage.getAll()) {
            if (pokemon != null && pokemon.getLevel() > cap) {
                return false;
            }
        }
        return true;
    }

    /**
     * Obtains an {@link ArenaStruc} object associated with the parameters.
     *
     * @param gs    The {@link GymStruc} associated with the designated arena.
     * @param aName The name of the arena.
     */
    public static ArenaStruc getArena(GymStruc gs, String aName) {
        return gs.Arenas.stream().filter(a -> a.Name.equalsIgnoreCase(aName)).findAny().orElse(null);
    }

    /**
     * Returns a list of all the arena names associated with a gym.
     *
     * @param gs   The GymStruc associated with the arena objects.
     * @param sort Determines whether it returns the list of arenas in alphabetical order.
     */
    public static List<String> getArenaNames(GymStruc gs, boolean sort) {
        List<String> arenaNames = Lists.newArrayList();
        gs.Arenas.forEach(arena -> arenaNames.add(arena.Name));
        if (sort)
            arenaNames.sort(Comparator.naturalOrder());
        return arenaNames;
    }

    /**
     * Checks to see if there is anyone located in the arena. Mainly for active battles.
     *
     * @param as The {@link ArenaStruc} object you are checking.
     * @return True if Empty or False if Occupied.
     */
    public static boolean isArenaEmpty(ArenaStruc as) {
        return (as.Leader == null && as.Challenger == null);
    }

    /**
     * Sets the players position to the specified position.
     *
     * @param player    The player whom will be teleported.
     * @param loc       The {@link Vec3dStruc} where the player will be teleported.
     * @param worldUUID The UUID of the destination world.
     */
    public static void setPosition(Player player, Vec3dStruc loc, UUID worldUUID) {
        World world = null;
        try {
            world = Sponge.getServer().getWorld(worldUUID).orElseThrow(() -> new AGPException("Invalid World"));
        } catch (AGPException e) {
            e.printStackTrace();
        }
        player.transferToWorld(world);
        player.setRotation(new Vector3d(loc.pitch, loc.yaw, 0));
        player.setLocation(player.getWorld().getLocation(loc.x, loc.y + 1, loc.z));
    }

    /**
     * Checks to see if the specified player is a leader of any gyms.
     *
     * @param player The player which you would like to check against.
     * @return True: The player is a leader of a gym. False: The player is not a leader of any gym.
     */
    public static boolean isAnyLeader(Player player) {
        for (GymStruc gs : DataStruc.gcon.GymData) {
            if (gs.PlayerLeaders.stream().anyMatch(p -> p.equals(player.getUniqueId()))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks to see if a player is the leader of a specific gym.
     *
     * @param player The player which you would like to check against.
     * @param gs     The {@link GymStruc} of the gym you are checking against.
     * @return True: The player is the leader of the gym. False: The player is not a leader of the gym.
     */
    public static boolean isGymLeader(Player player, GymStruc gs) {
        return gs.PlayerLeaders.stream().anyMatch(p -> p.equals(player.getUniqueId()));
    }

    /**
     * Checks to see if the player is in any gyms queue for battle.
     *
     * @param player The player which you would like to check against.
     * @return True: The player is in a gym queue. False: The player is not in a gym queue.
     */
    public static boolean isInAnyQueue(Player player) {
        for (GymStruc gs : DataStruc.gcon.GymData) {
            if (gs.Queue.stream().anyMatch(p -> p.equals(player.getUniqueId()))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks to see if the specified player is in a gym battle.
     *
     * @param player The player which you would like to check against.
     * @return True: The player is in a gym battle. False: The player is not in a gym battle.
     */
    public static boolean isInGymBattle(Player player) {
        return DataStruc.gcon.GymBattlers.stream().anyMatch(bs -> bs.leader.equals(player.getUniqueId())
                || bs.challenger.equals(player.getUniqueId()));
    }

    /**
     * Converts a string of text into a {@link Text}. Supports Minecraft color code formats.
     *
     * @param msg    The message you would like to be converted to {@link Text}.
     * @param prefix Whether or not to apply the AGP prefix
     * @return A {@link Text} instance of msg.
     */
    public static Text toText(String msg, boolean prefix) {
        if (prefix) {
            msg = AGPConfig.Announcements.agpPrefix + msg;
        }

        return TextSerializers.FORMATTING_CODE.deserializeUnchecked(msg);
    }

    /**
     * Gets all of the players on the server and returns them as a {@link Collection}
     *
     * @return Returns all of the players as a {@link Collection}.
     */
    @Deprecated //Marked so i can see all the crap code used for broadcasts
    public static Collection<Player> getAllPlayers() {
        return Sponge.getServer().getOnlinePlayers();
    }

    //TODO: Replace methods that use this with gymstruc.Queue

    /**
     * Gets all of the players waiting in the specified gyms queue for battle.
     *
     * @param gs The {@link GymStruc} of the gym you are checking against.
     * @return Returns a {@link List} of {@link UUID}s of the players waiting in the list.
     */
    public static List<UUID> getQueuedPlayers(GymStruc gs) {
        return gs.Queue;
    }

    /**
     * Gets a {@link List} of {@link UUID}s of the leaders of the specified gym.
     *
     * @param gs The {@link GymStruc} of the gym you are checking against.
     * @return Returns a {@link List} of {@link UUID}s of the leaders of the specified gym.
     */
    public static List<UUID> getGymLeaders(GymStruc gs) {
        return gs.PlayerLeaders;
    }

    /**
     * Adds the specified amount of money to the players balance.
     *
     * @param player The player which you would like to give the money to.
     * @param money  The amount of money you would like to give the player.
     */
    public static void addCurrency(Player player, int money) {
        Pixelmon.moneyManager.getBankAccount((EntityPlayerMP) player).get().changeMoney(money);
    }

    /**
     * A custom converter for EntityPixelmon to PixelmonData
     *
     * @param pixelmon The EntityPixelmon you would like to convert.
     * @return The PixelmonData of the EntityPixelmon provided.
     */
    public static Pokemon entityPixelmonToPixelmonData(EntityPixelmon pixelmon) {
        return pixelmon.getPokemonData();
    }

    public static ItemStack getPixelmonSprite(Pokemon data) {
        net.minecraft.item.ItemStack nativeItem = new net.minecraft.item.ItemStack(PixelmonItems.itemPixelmonSprite);
        NBTTagCompound nbt = new NBTTagCompound();
        EnumSpecies species = data.getSpecies();
        String idValue = String.format("%03d", species.getNationalPokedexInteger());
        if (data.isEgg()) {
            switch (species) {
                case Manaphy:
                case Togepi:
                    nbt.setString(NbtKeys.SPRITE_NAME, "pixelmon:sprites/eggs/manaphy1");
                    break;
                default:
                    nbt.setString(NbtKeys.SPRITE_NAME, "pixelmon:sprites/eggs/egg1");
                    break;
            }
        } else {
            if (data.isShiny()) {
                nbt.setString(NbtKeys.SPRITE_NAME, "pixelmon:sprites/shinypokemon/" + idValue + SpriteHelper.getSpriteExtra(
                        species.name, data.getForm())
                );
            } else {
                nbt.setString(NbtKeys.SPRITE_NAME, "pixelmon:sprites/pokemon/" + idValue + SpriteHelper.getSpriteExtra(
                        species.name, data.getForm())
                );
            }
        }
        nativeItem.setTagCompound(nbt);
        return (ItemStack) (Object) nativeItem;
    }

}
