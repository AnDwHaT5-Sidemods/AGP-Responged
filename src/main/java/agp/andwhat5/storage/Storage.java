package agp.andwhat5.storage;

import agp.andwhat5.config.structs.BadgeStruc;
import agp.andwhat5.config.structs.DataStruc;
import agp.andwhat5.config.structs.GymStruc;
import agp.andwhat5.config.structs.PlayerStruc;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public interface Storage {
    /**
     * Initialize the Storage type
     *
     * @throws Exception Occurs on any failure
     */
    void init() throws Exception;

    /**
     * For the SQL Databases, this will close all pooled connections by Hikari
     * <p>
     * For Flatfile, this will save the data one last time
     *
     * @throws Exception Occurs on any failure
     */
    void shutdown();

    /**
     * Will delete all current data saved to the system
     */
    void clearData(String table);

    /**
     * Saves all data cached into memory
     *
     * @param data The cached memory of all gym/player data
     */
    void saveData(DataStruc data);

    /**
     * Grabs all data of each player and caches it into memory.
     * This method should really only be called when needed,
     * since we can simply just used the cached data representation
     * then save all our data from there
     *
     * @return A mapping of all player data to their {@link UUID}
     */
    HashMap<UUID, PlayerStruc> getPlayerData();

    /**
     * Will attempt to update a Player's Badges
     * <p>
     * This function takes a boolean parameter to choose its function.
     * If 'add' is true, we will add the {@link BadgeStruc} into storage,
     * but, if 'add' is false, we will remove it instead
     *
     * @param uuid  The uuid of the player we are updating
     * @param badge The badge data in question for the operation
     * @param add   Whether to add or remove the badge
     */
    void updateObtainedBadges(UUID uuid, String name, BadgeStruc badge, boolean add);

    /**
     * Grabs all gym data and caches it into memory.
     * This method should only be called when needed, preferablly only startup & reload
     * All the data past that point can be found in the {@link DataStruc}
     *
     * @return A List of all the created gyms and their assigned data
     */
    List<GymStruc> getGyms();

    /**
     * Will attempt to update the Gyms on the server
     * <p>
     * This function takes a boolean parameter to choose its function.
     * If 'add' is true, we will add the {@link GymStruc} into storage,
     * but, if 'add' is false, we will remove it instead
     *
     * @param gym The gym in question for the operation
     * @param add Whether to add or remove the gym
     */
    void updateGyms(GymStruc gym, boolean add);

    /**
     * Will attempt to remove a deleted gym's badge from all players
     * in possession of the badge
     *
     * @param gym The gym that is being deleted from storage
     */
    void updateAllBadges(GymStruc gym);

    /**
     * This method is meant to serve as an easy way to allow for
     * data conversion for all gym data
     *
     * @param gyms All loaded gyms ready for a data conversion
     */
    void updateAllGyms(List<GymStruc> gyms);

    /**
     * This method is meant to serve as an easy way to allow for
     * data conversion for all player data
     *
     * @param players The players ready for a data conversion
     */
    void updateAllPlayers(HashMap<UUID, PlayerStruc> players);

}
