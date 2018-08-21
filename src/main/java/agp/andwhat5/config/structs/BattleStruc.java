package agp.andwhat5.config.structs;

import java.util.UUID;

public class BattleStruc {
    public UUID leader;
    public UUID challenger;
    public GymStruc gym;

    public BattleStruc() {
    }

    public BattleStruc(GymStruc gym, UUID leader, UUID challenger) {
        this.gym = gym;
        this.leader = leader;
        this.challenger = challenger;
    }

}
