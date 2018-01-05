package agp.andwhat5.config.structs;

import net.minecraft.entity.player.EntityPlayerMP;

public class BattleStruc
{
	public BattleStruc() {}

	public BattleStruc(GymStruc gym, EntityPlayerMP leader, EntityPlayerMP challenger)
	{
		this.gym = gym;
		this.leader = leader;
		this.challenger = challenger;
	}

	public EntityPlayerMP leader;

	public EntityPlayerMP challenger;

	public GymStruc gym;

}
