package agp.andwhat5.config.structs;

import java.util.UUID;

public class BattleStruc {

    /**
     * The {@link UUID} of the leader that is currently battling.
     */
    public final UUID leader;

    /**
     * The {@link UUID} of the challenger that is currently battling.
     */
    public final UUID challenger;

    /**
     * The {@link GymStruc} of the gym the leader and challenger are fighting in.
     */
    public final GymStruc gym;

    /**
     * The {@link ArenaStruc} the challenger and leader are fighting in.
     */
    public ArenaStruc arena;

    public BattleStruc(GymStruc gym, ArenaStruc arena, UUID leader, UUID challenger) {
        this.gym = gym;
        this.arena = arena;
        this.leader = leader;
        this.challenger = challenger;
    }

}
