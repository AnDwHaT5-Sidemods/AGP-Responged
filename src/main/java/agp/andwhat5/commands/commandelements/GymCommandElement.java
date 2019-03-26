package agp.andwhat5.commands.commandelements;

import agp.andwhat5.Utils;
import agp.andwhat5.config.structs.GymStruc;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.SelectorCommandElement;
import org.spongepowered.api.text.Text;

public class GymCommandElement extends SelectorCommandElement {

    private GymCommandElement() {
        super(Text.of("GymName"));
    }

    public static CommandElement gym() {
        return new GymCommandElement();
    }

    @Override
    protected Iterable<String> getChoices(CommandSource source) {
        return Utils.getGymNames(true);
    }

    @Override
    protected Object getValue(String choice) throws IllegalArgumentException {
        GymStruc value = Utils.getGym(choice);
        if (value == null) {
            throw new IllegalArgumentException("No gym named " + choice);
        }

        return value;
    }

}
