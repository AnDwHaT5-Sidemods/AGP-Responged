package agp.andwhat5.config.structs;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DataStruc {

	/**
	 * The object that holds all of AGP's information.
	 */
    public static DataStruc gcon = new DataStruc();
    
    /**
     * Holds a {@link List} of {@link GymStruc}s.
     */
    @Expose
    public List<GymStruc> GymData;

    /**
     * Holds a {@link HashMap} of all of the player data. It takes a {@link UUID} and translates it into a {@link PlayerStruc}.
     */
    @Expose
    public HashMap<UUID, PlayerStruc> PlayerData;

    /**
     * Holds all of the Arena Data for all of the gyms.
     */
    @Expose
    public List<ArenaStruc> ArenaData;

    /**
     * Holds all of the active gym battles.
     */
    public List<BattleStruc> GymBattlers;

    public DataStruc() {
        this.GymData = new ArrayList<>();
        this.PlayerData = new HashMap<>();
        this.ArenaData = new ArrayList<>();
        this.GymBattlers = new ArrayList<>();
    }
}
