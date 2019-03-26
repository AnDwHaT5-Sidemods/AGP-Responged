package agp.andwhat5.config.structs;

import com.google.gson.annotations.Expose;

public class ArenaStruc {

    /**
     * The name of the Arena.
     */
    @Expose
    public final String Name;

    /**
     * The location of the Stands for the gym. The players watching area.
     */
    @Expose
    public Vec3dStruc Stands;

    /**
     * Where the Challenger is supposed to stand when the battle initiates.
     */
    @Expose
    public Vec3dStruc Challenger;

    /**
     * Where the Leader is supposed to stand when the battle initiates.
     */
    @Expose
    public Vec3dStruc Leader;

    /**
     * Whether or not this arena is currently in use.
     */
    public boolean inUse = false;

    public ArenaStruc(String name) {
        this.Name = name;
    }
}
