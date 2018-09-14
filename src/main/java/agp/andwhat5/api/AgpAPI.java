package agp.andwhat5.api;

import agp.andwhat5.config.structs.DataStruc;
import agp.andwhat5.config.structs.GymStruc;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by nickd on 4/7/2017.
 */
@SuppressWarnings("unused")
class AgpAPI {
    //TODO: Make the API a separate project?
    //TODO: This should have little implementation. Most of this is in Utils as well

    public AgpAPI() {
    }

    /**
     * Fetches all current gyms registered to the server for
     * easy reference.
     * <p>
     * <p>
     * Data contains all information about a GymStruc, such as leaders,
     * level cap, and more. If trying to fetch leaders, I recommend the
     * usage of the {@link #getGymLeaders(String) getGymLeaders()} method
     * </P>
     *
     * @return Every registered gym to the server in the form of a List
     */
    public List<GymStruc> getAllGyms() {
        return DataStruc.gcon.GymData;
    }

    /**
     * Fetches the specified gym from the server data, if it exists
     *
     * @return An optional parameter, representing a specific gym being present or not
     */
    private Optional<GymStruc> getSpecificGym(String gym) {
        return DataStruc.gcon.GymData.stream().filter(g -> g.Name.equalsIgnoreCase(gym)).findAny();
    }

    /**
     * Fetches and creates a list of all leaders in the server data.
     *
     * <P>Note: This method does not mention where the leader came from,
     * only that the player is a leader. If you want more clear data,
     * I recommend the use of {@link #getAllLeadersMap() getAllLeadersMap()}
     * </P>
     *
     * @return A list full of all leaders
     */
    public List<UUID> getAllLeaders() {
        List<UUID> leaders = Lists.newArrayList();
        for (GymStruc gym : DataStruc.gcon.GymData) {
            for (UUID leader : gym.PlayerLeaders) {
                if (!leaders.contains(leader)) {
                    leaders.add(leader);
                }
            }
        }

        return leaders;
    }

    /**
     * Fetches and creates a mapping of all leaders and their gyms respectively.
     *
     * @return A list full of all leaders with the gyms they are registered to
     */
    public HashMap<UUID, List<String>> getAllLeadersMap() {
        HashMap<UUID, List<String>> leaders = Maps.newHashMap();
        for (GymStruc gym : DataStruc.gcon.GymData) {
            for (UUID leader : gym.PlayerLeaders) {
                if (leaders.containsKey(leader)) {
                    List<String> gyms = leaders.get(leader);
                    gyms.add(gym.Name);
                    leaders.put(leader, gyms);
                } else {
                    List<String> gyms = Lists.newArrayList(gym.Name);
                    leaders.put(leader, gyms);
                }
            }
        }

        return leaders;
    }

    /**
     * Fetches and creates a list of all leaders registered to a specific gym
     *
     * <P>This method utilizes the {@link #getSpecificGym(String) getSpecificGym()} method to
     * properly find the right gym
     * </P>
     *
     * @return A list full of all leaders with the gyms they are registered to
     */
    public List<UUID> getGymLeaders(String gym) {
        Optional<GymStruc> g = getSpecificGym(gym);
        if (g.isPresent())
            return g.get().PlayerLeaders;
        else
            return Lists.newArrayList();
    }
}
