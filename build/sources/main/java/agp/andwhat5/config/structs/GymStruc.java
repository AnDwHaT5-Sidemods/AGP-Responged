package agp.andwhat5.config.structs;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.google.gson.annotations.Expose;

public class GymStruc
{
	public GymStruc() {}

	public GymStruc(String name, String requirement, String badge, int levelCap, int money, List<String> items)
	{
		Name = name;
		Requirement = requirement;
		Badge = badge;
		LevelCap = levelCap;
		Money = money;
		Items = items;
	}

	@Expose
	public String Name = "";

	@Expose
	public String Badge = "";

	@Expose
	public String Requirement = "";

	@Expose
	public int LevelCap = 0;

	@Expose
	public int Money = 0;

	@Expose
	public Vec3dStruc Lobby;

	@Expose
	public List<String> Leaders = new ArrayList<>();

	@Expose
	public List<String> Items = new ArrayList<>();

	@Expose
	public List<ArenaStruc> Arenas = new ArrayList<>();

	public short Status = 1;
	
	public Queue<String> Queue = new LinkedList<>();
	
	public List <String> OnlineLeaders = new ArrayList<>();
}
