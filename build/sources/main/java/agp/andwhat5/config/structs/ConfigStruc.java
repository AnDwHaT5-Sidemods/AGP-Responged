package agp.andwhat5.config.structs;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class ConfigStruc
{

	@Deprecated
	@Expose(serialize = false)
	public List<String> GymNames = new ArrayList<>();

	@Deprecated
	@Expose(serialize = false)
	public List<String[]> Items = new ArrayList<>();

	@Deprecated
	@Expose(serialize = false)
	public List<Integer> Money = new ArrayList<>();

	@Deprecated
	@Expose(serialize = false)
	public List<String> Badges = new ArrayList<>();

	@Deprecated
	@Expose(serialize = false)
	public List<LeaderStruc> Leaders = new ArrayList<>();

	public static ConfigStruc gcon = new ConfigStruc();
}
