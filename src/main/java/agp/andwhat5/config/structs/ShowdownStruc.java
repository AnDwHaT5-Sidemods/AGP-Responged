package agp.andwhat5.config.structs;

import com.google.gson.annotations.Expose;

import java.util.UUID;

public class ShowdownStruc {

    @SuppressWarnings("unused")
    @Expose
    public UUID uuid;

    @Expose
    public String showdownCode;
}
