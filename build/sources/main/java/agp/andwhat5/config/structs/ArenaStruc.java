package agp.andwhat5.config.structs;

import com.google.gson.annotations.Expose;

public class ArenaStruc
{
	public ArenaStruc(String name) {
		this.Name = name;
	}

	@Expose
	public String Name;

	@Expose
	public Vec3dStruc Stands;

	@Expose
	public Vec3dStruc Challenger;

	@Expose
	public Vec3dStruc Leader;

	public boolean inUse = false;
}
