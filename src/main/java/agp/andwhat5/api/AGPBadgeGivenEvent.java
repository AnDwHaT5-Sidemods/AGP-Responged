package agp.andwhat5.api;

import agp.andwhat5.config.structs.BadgeStruc;
import agp.andwhat5.config.structs.PlayerStruc;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

@SuppressWarnings("unused")
public class AGPBadgeGivenEvent extends AbstractEvent {

    private final Cause cause;
    private final PlayerStruc player;
    private final BadgeStruc badge;

    public AGPBadgeGivenEvent(PlayerStruc player, BadgeStruc badge, Cause cause) {
        this.player = player;
        this.badge = badge;
        this.cause = cause;
    }

    @Override
    public Cause getCause() {
        return this.cause;
    }

    public PlayerStruc getPlayer() {
        return player;
    }

    public BadgeStruc getBadge() {
        return badge;
    }

}
