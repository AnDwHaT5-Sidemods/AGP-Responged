package agp.andwhat5.commands.gyms;

import agp.andwhat5.Utils;
import agp.andwhat5.commands.Command;
import agp.andwhat5.config.structs.DataStruc;
import agp.andwhat5.config.structs.GymStruc;
import com.google.common.collect.Lists;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.api.command.CommandSource;

public class AddGym extends Command {

    //TODO just grab the item the player is holding?
    public AddGym() {
        super("Adds a gym with the specified badge.");
    }

    @Override
    public void execute(MinecraftServer server, CommandSource sender, String[] args) throws CommandException {
        //Everyone wanted less functionality for addgym so I gave them less functionality. Horrah for regression.
        if (args.length != 2) {
            sender.sendMessage(Utils.toText("&7Incorrect usage: &b/AddGym <gym> <badge>&7.", true));
            return;
        }
        if (Utils.gymExists(args[0])) {
            sender.sendMessage(Utils.toText("&7The &b" + Utils.getGym(args[0]).Name + " &7Gym already exists!", true));
            return;
        }
        if(DataStruc.gcon.GymData.size() >= 54)
        {
        	sender.sendMessage(Utils.toText("&7AGP already has the max supported gyms of 54 gyms. You can not add another.", true));
        	return;
        }
        //int levelCap = parseInt(args[2]);
        //if (levelCap < 1 || levelCap > PixelmonConfig.maxLevel)
        //{
        //	sender.addChatMessage(Utils.toText("&7The level cap is outside the range of &b1 - " +
        //												 PixelmonConfig.maxLevel, true));
        //}

        try {
            CommandBase.getItemByText((ICommandSender) sender, args[1]);
        } catch (NumberInvalidException e) {
            throw new CommandException("The specified item doesn't exist and can't be a badge");
        }

        int money = 0;
        String item1 = "null";
        String item2 = "null";
        String Requirement = "null";
            /*switch (args.length)
            {
                case 6:
                    money = parseInt(args[3]);
                    item1 = args[4];
                    item2 = args[5];
                    break;
                case 5:
                    money = parseInt(args[3]);
                    item1 = args[4];
                    break;
                case 4:
                    money = parseInt(args[3]);
                    break;
            }*/
        GymStruc gs = new GymStruc(args[0], Requirement, args[1], 0, money, Lists.newArrayList(item1, item2));
        Utils.addGym(gs);
        Utils.sortGyms();
        Utils.saveAGPData();

        sender.sendMessage(Utils.toText("&7Successfully created the &b" + gs.Name + " &7Gym!", true));


    }


}
