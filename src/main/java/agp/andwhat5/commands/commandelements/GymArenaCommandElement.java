package agp.andwhat5.commands.commandelements;

import agp.andwhat5.config.structs.ArenaStruc;
import agp.andwhat5.config.structs.GymStruc;
import com.google.common.collect.ImmutableList;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.SelectorCommandElement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.selector.Selector;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

//Don't look directly at it, just don't
public class GymArenaCommandElement extends SelectorCommandElement {

    private GymStruc currentGym = null;

    private GymArenaCommandElement() {
        super(Text.of("GymArena"));
    }

    public static CommandElement gymArena() {
        return new GymArenaCommandElement();
    }

    @Override
    protected Iterable<String> getChoices(CommandSource source) {
        if (currentGym != null) {
            List<ArenaStruc> arenas = currentGym.Arenas;
            List<String> arenaNames = new ArrayList<>(arenas.size());
            for (ArenaStruc arena : arenas) {
                arenaNames.add(arena.Name);
            }
            return arenaNames;
        }
        return ImmutableList.of();
    }

    @Override
    protected Object getValue(String choice) throws IllegalArgumentException {
        if (currentGym == null) {
            throw new IllegalArgumentException("No gym " + choice);
        }
        for (ArenaStruc arena : currentGym.Arenas) {
            if (arena.Name.equalsIgnoreCase(choice)) {
                return arena;
            }
        }

        throw new IllegalArgumentException("No gym arena " + choice);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        CommandArgs.Snapshot state = args.getSnapshot();
        final Optional<String> nextArg = args.nextIfPresent();
        args.applySnapshot(state);
        List<String> choices = nextArg.map(Selector::complete).orElseGet(ImmutableList::of);

        if (choices.isEmpty()) {
            Iterable<String> choices2 = getGymArenas(context);
            final Optional<String> nextArg2 = args.nextIfPresent();
            if (nextArg2.isPresent()) {
                choices2 = StreamSupport.stream(choices2.spliterator(), false).filter(input -> getFormattedPattern(nextArg2.get()).matcher(input).find()).collect(Collectors.toList());
                choices = ImmutableList.copyOf(choices2);
            }
            return choices;
        }

        return ImmutableList.of();
    }

    private Pattern getFormattedPattern(String input) {
        if (!input.startsWith("^")) { // Anchor matches to the beginning -- this lets us use find()
            input = "^" + input;
        }
        return Pattern.compile(input, Pattern.CASE_INSENSITIVE);

    }

    private List<String> getGymArenas(CommandContext context) {

        Optional<GymStruc> gymName = context.getOne("GymName");
        if (!gymName.isPresent()) {
            currentGym = null;
            return ImmutableList.of();
        }

        currentGym = gymName.get();
        List<ArenaStruc> arenas = gymName.get().Arenas;
        List<String> arenaNames = new ArrayList<>(arenas.size());
        for (ArenaStruc arena : arenas) {
            arenaNames.add(arena.Name);
        }

        return arenaNames;
    }

}
