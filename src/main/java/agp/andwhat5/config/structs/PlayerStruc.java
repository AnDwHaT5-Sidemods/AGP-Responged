package agp.andwhat5.config.structs;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.gson.annotations.Expose;

public class PlayerStruc {
	
	/**
	 * The username of the player.
	 */
	@Deprecated
    @Expose
    public String Name;//TODO can we remove this? Avery: Later when can remove it.
    
	/**
	 * The {@link UUID} of the player.
	 */
    @Expose
    public UUID uuid;

    /**
     * A {@link List} of {@link BadgeStruc}s that contains all of the users badges.
     */
    @Expose
    public List<BadgeStruc> Badges = new ArrayList<>();

    public PlayerStruc() {
    }

    public PlayerStruc(UUID uuid) {
    	this.uuid = uuid;
        Badges = new ArrayList<>();
    }

    public static class Leader {
        @Expose
        public boolean isLeader = false;
        @Expose
        public boolean JoinMessage = false;
    }
}
