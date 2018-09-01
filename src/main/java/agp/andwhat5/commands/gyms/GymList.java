package agp.andwhat5.commands.gyms;

import agp.andwhat5.Utils;
import agp.andwhat5.commands.Command;
import agp.andwhat5.config.structs.GymStruc;
import agp.andwhat5.ui.EnumGUIType;
import net.minecraft.command.CommandException;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static agp.andwhat5.config.structs.GymStruc.EnumStatus.*;
import static net.minecraft.command.CommandBase.getListOfStringsMatchingLastWord;

public class GymList extends Command {

    public GymList() {
        super("Shows all of the gyms the server has.");
    }

    @Override
    public void execute(MinecraftServer server, CommandSource sender, String[] args) throws CommandException {
    	Arrays.asList(args).stream().forEach(e -> sender.sendMessage(Text.of(e)));
        if (args.length == 0) 
        {
            Utils.openGUI(requireEntityPlayer(sender), requireEntityPlayer(sender), EnumGUIType.GymList);
        } 
        else 
        if (args.length == 1 && args[0].equalsIgnoreCase("-nogui")) 
        {
            sender.sendMessage(Utils.toText("&f--==[&dAGP - Gyms List&f]==--", false));
            sender.sendMessage(Utils.toText("&bGyms: &7(&aOpen&7) &7(&cClosed&7)", false));
            sender.sendMessage(Utils.toText("&bLeaders: &7(&aOnline&7) &7(&eNPC&7) &7(&cOffline&7)", false));
            sender.sendMessage(Utils.toText("", false));

            for (GymStruc gs : Utils.getGymStrucs(true)) {
                StringBuilder msg = new StringBuilder((gs.Status == CLOSED ? "&a" : gs.Status == OPEN ? "&c" : "&e") + gs.Name + "&7[&f");
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
                sender.sendMessage(Utils.toText(msg.toString(), false));
            }
        } 
        else 
        {
            sender.sendMessage(Utils.toText("&7Incorrect usage: /GymList <opt-(-nogui)&7.", true));
            return;
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, CommandSource sender, String[] args) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, Collections.singletonList("-nogui"));
        }
        return null;
    }

}
