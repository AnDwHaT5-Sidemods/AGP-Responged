package agp.andwhat5.config.structs;

import com.google.gson.annotations.Expose;

import java.util.*;

public class GymStruc {
    @Expose
    public String Name = "";

    @Expose
    public String Badge = "";

    @Expose
    public String Requirement = "";

    @Expose
    public int LevelCap = 0;

    @Expose
    public int Money = 0;

    @Expose
    public Vec3dStruc Lobby;

    @SuppressWarnings("DeprecatedIsStillUsed")
    @Expose
    @Deprecated
    public List<String> Leaders = new ArrayList<>();

    @Expose
    public List<UUID> PlayerLeaders = new ArrayList<>();

    @Expose
    public int NPCAmount = 0;

    @Expose
    public List<String> Items = new ArrayList<>();

    @Expose
    public List<ArenaStruc> Arenas = new ArrayList<>();

    @Expose
    public String Rules = "";

    // 0 = open
    // 1 = closed
    // 2 = npc leader
    public enum EnumStatus {
        OPEN,
        CLOSED,
        NPC
    }
    public EnumStatus Status = EnumStatus.CLOSED;
    public Queue<UUID> Queue = new LinkedList<>();
    public List<UUID> OnlineLeaders = new ArrayList<>();

    public GymStruc() {
    }

    public GymStruc(String name, String requirement, String badge, int levelCap, int money, List<String> items) {
        Name = name;
        Requirement = requirement;
        Badge = badge;
        LevelCap = levelCap;
        Money = money;
        Items = items;
    }
}
