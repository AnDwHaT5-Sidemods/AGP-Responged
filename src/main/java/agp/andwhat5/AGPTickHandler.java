package agp.andwhat5;

import agp.andwhat5.config.AGPConfig;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

//TODO: Migrate to Scheduler
public class AGPTickHandler
{
	private static int ticks = 0;

	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event)
	{
		if (!AGPConfig.Announcements.announcementEnabled || event.phase == TickEvent.Phase.END) return;

		ticks++;
		if (ticks >= AGPConfig.Announcements.announcementTimer) 
		{
			ticks = 0;
			Utils.sendToAll(AGPConfig.Announcements.announcementMessage, false);
		}
	}

}
