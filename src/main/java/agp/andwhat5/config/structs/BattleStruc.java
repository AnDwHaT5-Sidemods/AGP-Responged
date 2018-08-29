package agp.andwhat5.config.structs;

import java.util.UUID;

public class BattleStruc {
	
	/**
	 * The {@link UUID} of the leader that is currently battling.
	 */
    public UUID leader;
    
    /**
     * The {@link UUID} of the challenger that is currently battling.
     */
    public UUID challenger;
    
    /**
     * The {@link GymStruc} of the gym the leader and challenger are fighting in.
     */
    public GymStruc gym;

    public BattleStruc() {
    }

    public BattleStruc(GymStruc gym, UUID leader, UUID challenger) {
        this.gym = gym;
        this.leader = leader;
        this.challenger = challenger;
    }

}
