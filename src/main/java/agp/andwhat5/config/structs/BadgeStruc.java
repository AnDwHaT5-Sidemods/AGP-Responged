package agp.andwhat5.config.structs;

import com.google.common.collect.Lists;
import com.google.gson.annotations.Expose;

import java.util.Date;
import java.util.List;

public class BadgeStruc {
    @Expose
    public String Gym;
    @Expose
    public String Badge;
    @Expose
    public String Leader;
    @Expose
    public Date Obtained;
    @Expose
    public List<String> Pokemon;

    public BadgeStruc() {
    }

    public BadgeStruc(String gym, String badge, String leader, Date obtained) {
        Gym = gym;
        Badge = badge;
        Leader = leader;
        Obtained = obtained;
        Pokemon = Lists.newArrayList();
    }
}
