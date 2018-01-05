package agp.andwhat5.listeners;

import agp.andwhat5.Utils;
import com.pixelmonmod.pixelmon.api.events.ExperienceGainEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GymExperienceGain
{
	/**
	 * On exp gain, prevent challenger and leader from gaining
	 * experience so that they can't breach the level cap.
	 * Essentially simulates the level disabler provided
	 * from pixelmon.
	 *
	 * @param event Experience gain event
	 */
	@SubscribeEvent
	public void onExpGain(ExperienceGainEvent event)
	{
		EntityPlayerMP player = event.pokemon.getPlayerOwner();
		if (Utils.isInAnyBattle(player))
		{
			event.setExperience(0);
		}
	}
}
