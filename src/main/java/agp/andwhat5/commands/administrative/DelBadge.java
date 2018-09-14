package agp.andwhat5.commands.administrative;

import agp.andwhat5.AGP;
import agp.andwhat5.Utils;
import agp.andwhat5.config.structs.BadgeStruc;
import agp.andwhat5.config.structs.DataStruc;
import agp.andwhat5.config.structs.GymStruc;
import agp.andwhat5.config.structs.PlayerStruc;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

public class DelBadge implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        Player target = args.<Player>getOne("player").get();
        GymStruc gym = args.<GymStruc>getOne("GymName").get();

        PlayerStruc ps = DataStruc.gcon.PlayerData.getOrDefault(target.getUniqueId(), null);

        if (ps == null) {
            src.sendMessage(Utils.toText("&b" + target.getName() + " &7does not have any badges!", true));
            return CommandResult.success();
        }

        if (ps.Badges.stream().noneMatch(b -> b.Gym.equalsIgnoreCase(gym.Name))) {
            src.sendMessage(Utils.toText("&b" + target.getName() + " &7does not have the &b" + gym.Name + " &7badge!", true));
            return CommandResult.success();
        }

        BadgeStruc bs = ps.Badges.stream().filter(b -> b.Gym.equalsIgnoreCase(gym.Name)).findAny().get();
        AGP.getInstance().getStorage().updateObtainedBadges(target.getUniqueId(), target.getName(), bs, false);
        AGP.getInstance().getStorage().saveData(DataStruc.gcon);
        src.sendMessage(Utils.toText("&7Successfully removed &b" + target.getName() + "&7's &b" + gym.Name + " &7badge!", true));
        target.sendMessage(Utils.toText("&b" + src.getName() + " &7has taken away your &b" + gym.Name + " &7badge!", true));

        return CommandResult.success();
    }


}
