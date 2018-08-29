package agp.andwhat5.config.structs;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class LeaderStruc {
	
    @Expose
    public String Leader;
    @Expose
    public List<String> Gyms = new ArrayList<>();
}