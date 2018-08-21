package agp.andwhat5.commands;

import agp.andwhat5.Utils;
import net.minecraft.command.CommandException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.spongepowered.api.text.format.TextColors.RED;

/**
 * This is currently acting as a sponge -> forge style command wrapper :(
 */
public abstract class Command implements /*CommandExecutor*/ CommandCallable {

    protected String usage;

    public Command(String usage) {
        this.usage = usage;
    }

    /**
     * Checks if the commandSender is a player and returns the entityPlayer if true.
     * If not, it throws a CommandException
     *
     * @param sender commandSender
     * @return the sender as EntityPlayerMP
     * @throws CommandException
     */
    public static Player requireEntityPlayer(CommandSource sender) throws CommandException {
        if (sender instanceof Player) {
            return (Player) sender;
        }
        throw getException("You must be a player to use this command!");
    }

    /**
     * Checks if the user is online. If they are it returns the entityPlayer.
     * If not, it throws a CommandException stating the player can not be found.
     *
     * @param username The username or uuid string to check.
     * @return the EntityPlayerMP
     * @throws CommandException
     */
    public static Player requireEntityPlayer(String username) throws CommandException {
        Player player = getEntityPlayer(username);
        if (player != null) return player;
        throw getException("That player can not be found!");
    }

    /**
     * Gets the entityPlayer if they are online else returns null
     *
     * @param username The username or uuid string to lookup
     * @return EntityPlayerMP or null if they are not online
     * @throws IllegalArgumentException if we cant parse the UUID
     */
    public static Player getEntityPlayer(String username) {
        if (username == null) return null;
        if (username.length() == 36) {
            UUID uuid = UUID.fromString(username);
            return Sponge.getServer().getPlayer(uuid).orElse(null);
        }
        return Sponge.getServer().getPlayer(username).orElse(null);
    }

    public static Player getEntityPlayer(UUID uuid) {
        return Sponge.getServer().getPlayer(uuid).orElse(null);
    }

    /**
     * Creates a new CommandException that doesn't have a StackTrace.
     *
     * @param message The message to send to the commandSender
     * @return the CommandException to be thrown.
     */
    private static CommandException getException(String message) {
        return new CommandException(TextFormatting.RED + message) {
            @Override
            public synchronized Throwable fillInStackTrace() {
                return this;
            }
        };
    }

    @Override
    public Text getUsage(CommandSource source) {
        return Utils.toText("&7Incorrect Usage: &b" + usage, true);
    }

    @Override
    public Optional<Text> getHelp(CommandSource source) {
        return Optional.empty();
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return Optional.empty();
    }

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments, @Nullable Location<World> targetPosition) throws org.spongepowered.api.command.CommandException {
        return getTabCompletions((MinecraftServer) Sponge.getServer(), source, arguments.split(" "));
    }

    public List<String> getTabCompletions(MinecraftServer server, CommandSource sender, String[] args) {
        return Collections.emptyList();
    }

    //TODO
    @Override
    public boolean testPermission(CommandSource source) {
        return true;
    }

    /**
     * Sends the sender the usage message for this command
     *
     * @param sender commandSender
     */
    public void sendUsage(CommandSource sender) {
        sender.sendMessage(getUsage(sender));
    }


    @Override
    public CommandResult process(CommandSource source, String arguments) throws org.spongepowered.api.command.CommandException {
        try {
            execute((MinecraftServer) Sponge.getServer(), source, arguments.split(" "));
        } catch (CommandException e) {
            source.sendMessage(Text.of(RED, e.getMessage()));
        }
        return CommandResult.success();
    }

    /*
        public CommandResult execute(CommandSource src, CommandContext context) throws org.spongepowered.api.command.CommandException {
            String[] args = context.<String>getOne("args").orElse("").split(" ");
            try {
                execute((MinecraftServer) Sponge.getServer(), src, args);
            } catch (CommandException e) {
                src.sendMessage(Text.of(RED, e.getMessage()));
            }
            return CommandResult.success();
        }
    */
    public abstract void execute(MinecraftServer server, CommandSource sender, String[] args) throws CommandException;
}
