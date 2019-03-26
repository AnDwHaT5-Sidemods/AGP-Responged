package agp.andwhat5.config.structs;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerStruc {

    /**
     * The username of the player.
     */
    @Deprecated
    @Expose
    public String Name;//TODO can we remove this? Avery: Later when can remove it.
    /**
     * A {@link List} of {@link BadgeStruc}s that contains all of the users badges.
     */
    @Expose
    public List<BadgeStruc> Badges = new ArrayList<>();
    /**
     * The {@link UUID} of the player.
     */
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    @Expose
    private UUID uuid;

    public PlayerStruc() {
    }

    public PlayerStruc(UUID uuid) {
        this.uuid = uuid;
        Badges = new ArrayList<>();
    }

}
