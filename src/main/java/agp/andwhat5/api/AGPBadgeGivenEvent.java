package agp.andwhat5.api;

import agp.andwhat5.config.structs.BadgeStruc;
import agp.andwhat5.config.structs.PlayerStruc;
import net.minecraftforge.fml.common.eventhandler.Event;

public class AGPBadgeGivenEvent extends Event{
	public PlayerStruc player;
	public BadgeStruc badge;
	public AGPBadgeGivenEvent(PlayerStruc player, BadgeStruc badge)
	{
		this.player = player;
		this.badge = badge;
	}
}
