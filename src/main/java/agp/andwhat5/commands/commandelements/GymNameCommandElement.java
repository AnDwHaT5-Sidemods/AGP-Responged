package agp.andwhat5.commands.commandelements;

import agp.andwhat5.Utils;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.SelectorCommandElement;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;

public class GymNameCommandElement extends SelectorCommandElement {

    protected GymNameCommandElement(@Nullable Text key) {
        super(key);
    }

    @Override
    protected Iterable<String> getChoices(CommandSource source) {
        return Utils.getGymNames(true);
    }

    @Override
    protected Object getValue(String choice) throws IllegalArgumentException {
        return choice;
    }

    public static CommandElement gymNames(Text key) {
        return new GymNameCommandElement(key);
    }

}
