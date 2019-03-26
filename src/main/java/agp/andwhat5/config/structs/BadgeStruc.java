package agp.andwhat5.config.structs;

import com.google.common.collect.Lists;
import com.google.gson.annotations.Expose;

import java.util.Date;
import java.util.List;

@SuppressWarnings("CanBeFinal")
public class BadgeStruc {

    /**
     * The name of the gym that this badge belongs to.
     */
    @Expose
    public String Gym;

    /**
     * The badge of the gym noted like pixelmon:boulder_badge.
     */
    @Expose
    public String Badge;

    /**
     * The leader defeated to win this badge.
     */
    @Expose
    public String Leader;

    /**
     * The date the badge was obtained.
     */
    @Expose
    public Date Obtained;

    /**
     * A {@link List} of Pokemon names the player had when they defeated the gym.
     */
    @Expose
    public List<String> Pokemon;

    public BadgeStruc(String gym, String badge, String leader, Date obtained) {
        Gym = gym;
        Badge = badge;
        Leader = leader;
        Obtained = obtained;
        Pokemon = Lists.newArrayList();
    }
}
