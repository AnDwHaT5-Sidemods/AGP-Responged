package agp.andwhat5.config.structs;

import com.google.gson.annotations.Expose;

public class ArenaStruc {
    @Expose
    public String Name;
    @Expose
    public Vec3dStruc Stands;
    @Expose
    public Vec3dStruc Challenger;
    @Expose
    public Vec3dStruc Leader;
    public boolean inUse = false;

    public ArenaStruc(String name) {
        this.Name = name;
    }
}
