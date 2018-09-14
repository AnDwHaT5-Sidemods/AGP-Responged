package agp.andwhat5.commands.gyms;

import agp.andwhat5.Utils;
import agp.andwhat5.config.structs.GymStruc;
import com.google.common.collect.Lists;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.item.ItemType;

public class AddGym implements CommandExecutor {

    //TODO just grab the item the player is holding?

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        //Everyone wanted less functionality for addgym so I gave them less functionality. Horrah for regression.

        String gymName = args.<String>getOne("GymName").get();
        ItemType badgeItem = args.<ItemType>getOne("BadgeItem").get();

        if (Utils.gymExists(gymName)) {
            src.sendMessage(Utils.toText("&7The &b" + Utils.getGym(gymName).Name + " &7Gym already exists!", true));
            return CommandResult.success();
        }

        int money = 0;
        String Requirement = "null";
        GymStruc gs = new GymStruc(gymName, Requirement, badgeItem.getId(), 0, money, Lists.newArrayList());
        Utils.addGym(gs);
        Utils.sortGyms();
        Utils.saveAGPData();

        src.sendMessage(Utils.toText("&7Successfully created the &b" + gs.Name + " &7Gym!", true));

        return CommandResult.success();
    }

}
