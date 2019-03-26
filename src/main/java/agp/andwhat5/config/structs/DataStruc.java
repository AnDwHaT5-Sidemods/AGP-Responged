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
     * Holds all of the active gym battles.
     */
    public final List<BattleStruc> GymBattlers;
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

    public DataStruc() {
        this.GymData = new ArrayList<>();
        this.PlayerData = new HashMap<>();
        this.GymBattlers = new ArrayList<>();
    }
}
