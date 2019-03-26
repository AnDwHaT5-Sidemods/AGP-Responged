package agp.andwhat5.commands.gyms;

import agp.andwhat5.Utils;
import agp.andwhat5.config.structs.GymStruc;
import agp.andwhat5.gui.GymListGui;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

import static agp.andwhat5.config.structs.GymStruc.EnumStatus.CLOSED;
import static agp.andwhat5.config.structs.GymStruc.EnumStatus.OPEN;

public class GymList implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        if (src instanceof Player) {
            GymListGui.openGymListGUI((Player) src);
        } else {
            src.sendMessage(Utils.toText("&f--==[&dAGP - Gyms List&f]==--", false));
            src.sendMessage(Utils.toText("&bGyms: &7(&aOpen&7) &7(&cClosed&7)", false));
            src.sendMessage(Utils.toText("&bLeaders: &7(&aOnline&7) &7(&eNPC&7) &7(&cOffline&7)", false));
            src.sendMessage(Utils.toText("", false));

            for (GymStruc gs : Utils.getGymStrucs(true)) {
                StringBuilder msg = new StringBuilder((gs.Status == CLOSED ? "&c" : gs.Status == OPEN ? "&a" : "&e") + gs.Name + "&7[&f");
                msg.append(gs.LevelCap == 0 ? "No Cap" : "lvl" + gs.LevelCap).append("&7]&8: ");
                boolean foundNPC = false;
                if (gs.PlayerLeaders.isEmpty()) {
                    msg.append("&8No leaders");
                } else {
                    for (int l = 0; l < gs.PlayerLeaders.size(); l++) {
                        if (gs.NPCAmount > 0) {
                            foundNPC = true;
                        } else {
                            msg.append(gs.OnlineLeaders.contains(gs.PlayerLeaders.get(l)) ? "&a" : "&c").append(gs.PlayerLeaders.get(l));
                        }
                        msg.append(gs.Status == OPEN ? "&a" : gs.Status == CLOSED ? "&c" : "&e").append(l == gs.PlayerLeaders.size() - 1 ? (foundNPC ? "&eNPC" : "") : ", ");
                    }
                }
                src.sendMessage(Utils.toText(msg.toString(), false));
            }
        }
        return CommandResult.success();
    }

}
