package agp.andwhat5.listeners;

import agp.andwhat5.api.AGPBadgeGivenEvent;
import org.spongepowered.api.event.Listener;

public class ListenerBadgeObtained {

    @Listener
    public void onBadgeObtained(AGPBadgeGivenEvent event) {
        //System.out.println(event.player.Name + " has recieved the " + event.badge.Badge + " badge!");
    }

}
