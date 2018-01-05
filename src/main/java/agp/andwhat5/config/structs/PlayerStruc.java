package agp.andwhat5.config.structs;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class PlayerStruc
{
	public PlayerStruc() {}

	public PlayerStruc(String name) {
		Name = name;
		Badges = new ArrayList<>();
	}

	@Expose
	public String Name;
	@Expose
	public List<BadgeStruc> Badges = new ArrayList<>();

	public static class Leader
	{
		@Expose
		public boolean isLeader = false;
		@Expose
		public boolean JoinMessage = false;
	}
}
