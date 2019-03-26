package agp.andwhat5.commands.players;

import agp.andwhat5.Utils;
import agp.andwhat5.gui.CheckBadgesGui;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class CheckBadges implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        if (!(src instanceof Player)) {
            src.sendMessage(Text.of(TextColors.RED, "This command can only be ran by players"));
            return CommandResult.success();
        }

        Player sender = (Player) src;
        Player target = args.<Player>getOne("target").get();

        if (target == src) {//Target self
            CheckBadgesGui.openCheckBadgesGUI(sender);
        } else {
            if (src.hasPermission("agp.checkbadges.other") || Utils.isAnyLeader(sender) || src.hasPermission("agp.headleader")) {
                CheckBadgesGui.openCheckBadgesGUIOther(sender, target);
            } else {
                sender.sendMessage(Utils.toText("&7You don't have permission to access another player's badges!", true));
            }
        }

        return CommandResult.success();
    }

}
