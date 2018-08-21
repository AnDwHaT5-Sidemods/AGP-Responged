package agp.andwhat5.config.structs;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DataStruc {

    public static DataStruc gcon = new DataStruc();
    @Expose
    public List<GymStruc> GymData;

    @Expose
    public HashMap<UUID, PlayerStruc> PlayerData;

    @Expose
    public List<ArenaStruc> ArenaData;

    public List<BattleStruc> GymBattlers;

    public DataStruc() {
        this.GymData = new ArrayList<>();
        this.PlayerData = new HashMap<>();
        this.ArenaData = new ArrayList<>();
        this.GymBattlers = new ArrayList<>();
    }
}
