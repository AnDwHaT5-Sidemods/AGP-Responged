package agp.andwhat5.api;

import agp.andwhat5.config.structs.BadgeStruc;
import agp.andwhat5.config.structs.PlayerStruc;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

public class AGPBadgeGivenEvent extends AbstractEvent {

    private final Cause cause;
    public PlayerStruc player;
    public BadgeStruc badge;

    public AGPBadgeGivenEvent(PlayerStruc player, BadgeStruc badge, Cause cause) {
        this.player = player;
        this.badge = badge;
        this.cause = cause;
    }

    @Override
    public Cause getCause() {
        return this.cause;
    }

}
