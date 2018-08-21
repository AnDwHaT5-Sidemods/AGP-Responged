package agp.andwhat5.config.structs;

import com.google.gson.annotations.Expose;

public class Vec3dStruc {
    @Expose
    public double x;
    @Expose
    public double y;
    @Expose
    public double z;
    @Expose
    public double pitch;
    @Expose
    public double yaw;

    public Vec3dStruc() {
    }

    public Vec3dStruc(double x, double y, double z, double pitch, double yaw) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
    }
}
