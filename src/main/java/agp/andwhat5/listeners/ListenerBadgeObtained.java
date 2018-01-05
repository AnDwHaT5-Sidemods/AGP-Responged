package agp.andwhat5.listeners;

import agp.andwhat5.api.AGPBadgeGivenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ListenerBadgeObtained {

	@SubscribeEvent
	public void onBadgeObtained(AGPBadgeGivenEvent event)
	{
		System.out.println(event.player.Name + " has recieved the " + event.badge.Badge + " badge!");
	}
}
