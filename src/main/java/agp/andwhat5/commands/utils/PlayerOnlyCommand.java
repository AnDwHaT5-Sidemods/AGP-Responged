package agp.andwhat5.commands.utils;

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public abstract class PlayerOnlyCommand implements CommandExecutor {

    @Override
    public final CommandResult execute(CommandSource src, CommandContext args) {

        if (!(src instanceof Player)) {
            src.sendMessage(Text.of(TextColors.RED, "This command may only be executed by a player"));
            return CommandResult.success();
        }

        return execute((Player) src, args);

    }

    protected abstract CommandResult execute(Player player, CommandContext args);

}
