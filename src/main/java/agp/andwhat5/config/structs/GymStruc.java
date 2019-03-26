package agp.andwhat5.config.structs;

import com.google.gson.annotations.Expose;

import java.util.*;

public class GymStruc {

    public final List<UUID> Queue = new ArrayList<>();
    public final Set<UUID> OnlineLeaders = new HashSet<>();
    /**
     * The name of the gym. If you change this, there may be issues with your players saved data.
     */
    @Expose
    public String Name = "";
    /**
     * The item ID of the badge associated with the gym. For example: pixelmon:boulder_badge.
     */
    @Expose
    public String Badge = "";
    /**
     * The name of the gym that is required before you can battle this one.
     */
    @Expose
    public String Requirement = "";
    /**
     * The level cap of the gym.
     */
    @Expose
    public int LevelCap = 0;
    /**
     * How much money is rewarded for defeating the gym.
     */
    @Expose
    public int Money = 0;
    /**
     * A {@link Vec3dStruc} of the location of the lobby for the gym.
     */
    @Expose
    public Vec3dStruc Lobby;
    //Only here for legacy reasons. Remove later.
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Expose
    @Deprecated
    public List<String> Leaders = new ArrayList<>();
    /**
     * A list of the gyms leaders.
     */
    @Expose
    public List<UUID> PlayerLeaders = new ArrayList<>();
    @Expose
    public int NPCAmount = 0;
    /**
     * A {@link List} of commands to be executed when the player earns a badge.
     */
    @Expose
    public List<String> Commands = new ArrayList<>();
    /**
     * A {@link List} of arenas that this gym has.
     */
    @Expose
    public List<ArenaStruc> Arenas = new ArrayList<>();
    /**
     * The rules of the gym that is called via the GymRules command.
     */
    @Expose
    public String Rules = "";
    /**
     * The UUID of the world the gym is located in.
     */
    @Expose
    public UUID worldUUID;
    /**
     * The weight of the gym for sorting.
     */
    @Expose
    public int Weight = 0;
    /**
     * The pool of the gyms pokemon.
     */
    @Expose
    public List<ShowdownStruc> Pokemon = new ArrayList<>();
    /**
     * The minimum amount of Pokemon a gym leader has to pick for a queue battle.
     */
    @Expose
    public int minimumPokemon = 1;
    /**
     * The maximum amount of Pokemon a gym leader has to pick for a queue battle.
     */
    @Expose
    public int maximumPokemon = 6;
    public EnumStatus Status = EnumStatus.CLOSED;
    public GymStruc() {
    }

    public GymStruc(String name, String requirement, String badge, int levelCap, int money, List<String> commands) {
        Name = name;
        Requirement = requirement;
        Badge = badge;
        LevelCap = levelCap;
        Money = money;
        Commands = commands;
    }

    // 0 = open
    // 1 = closed
    // 2 = npc leader
    public enum EnumStatus {
        OPEN,
        CLOSED,
        NPC
    }
}
