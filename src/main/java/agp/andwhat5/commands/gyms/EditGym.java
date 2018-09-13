package agp.andwhat5.commands.gyms;

import agp.andwhat5.AGP;
import agp.andwhat5.Utils;
import agp.andwhat5.config.AGPConfig;
import agp.andwhat5.config.structs.DataStruc;
import agp.andwhat5.config.structs.GymStruc;
import com.pixelmonmod.pixelmon.config.PixelmonConfig;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

import java.util.Optional;

/**
 * Created by nickd on 4/30/2017.
 */
public class EditGym implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource sender, CommandContext args) throws CommandException {


        //TODO redesign this entire thing..

        GymStruc gym = args.<GymStruc>getOne("GymName").get();
        Optional<String> args1 = args.getOne("args");
        if(!args1.isPresent()) {
            sender.sendMessage(Utils.toText("&7Incorrect usage: /EditGym <gym> <name:(name) | badge:(badge) | require:(gym) | rules:(rules) | level:(level) | money:(money)>&7.", true));
            return CommandResult.success();
        }

        String argList[] = args1.get().split(" ");

        // Parse options
        for (int i = 0; i < argList.length; i++) {
            if (argList[i].toLowerCase().startsWith("rules:") && !argList[i].toLowerCase().equalsIgnoreCase("rules:")) {
                gym.Rules = "";
                int index = 0;
                for (String s : argList) {
                    if (index != 0) {
                        if (s.startsWith("rules:")) {
                            gym.Rules += s.split("rules:")[1] + " ";
                        } else {
                            gym.Rules += s + " ";
                        }
                    }
                    index++;
                }
                if (AGPConfig.Storage.storageType.equalsIgnoreCase("flatfile")) {
                    Utils.editGym(gym);
                    AGP.getInstance().getStorage().saveData(DataStruc.gcon);
                } else {
                    Utils.addGym(gym);
                }
                sender.sendMessage(Utils.toText("&7Successfully updated the gyms rules!", true));
                return CommandResult.success();
            } else if (argList[i].toLowerCase().startsWith("require:") && !argList[i].toLowerCase().equalsIgnoreCase("require:")) {
                if (!Utils.gymExists(argList[i].substring(8))) {
                    sender.sendMessage(Utils.toText("&7The &b" + argList[i].substring(8) + " &7gym does not exist!", true));
                    return CommandResult.success();
                }
                GymStruc g = Utils.getGym(argList[i].substring(8));
                if (argList[0].equalsIgnoreCase(g.Name)) {
                    sender.sendMessage(Utils.toText("&7You can not set this gyms requirement to itself!", true));
                    return CommandResult.success();
                }
                gym.Requirement = g.Name;
                sender.sendMessage(Utils.toText("&7Successfully changed the requirement of the &b" + gym.Name + "&7 gym to the &b" + g.Name + "&7 gym!", true));
            } else if (argList[i].toLowerCase().startsWith("level:") && !argList[i].toLowerCase().equalsIgnoreCase("level:")) {
                int levelcap = Integer.valueOf(argList[i].substring(6));
                if (levelcap < 0 || levelcap > PixelmonConfig.maxLevel) {
                    sender.sendMessage(Utils.toText("&7The level you provided is out of the level cap range, &b0-" + PixelmonConfig.maxLevel + "&7!", true));
                    return CommandResult.success();
                }
                gym.LevelCap = Integer.valueOf(argList[i].substring(6));
                sender.sendMessage(Utils.toText("&7Successfully changed the level cap of the &b" + gym.Name + "&7 gym to &b" + gym.LevelCap + "&7!", true));
            } else if (argList[i].toLowerCase().startsWith("money:") && !argList[i].toLowerCase().equalsIgnoreCase("money:")) {
                gym.Money = Integer.parseInt(argList[i].substring(6));
                sender.sendMessage(Utils.toText("&7Successfully changed the reward money of the &b" + gym.Name + "&7 gym to &b" + gym.Money + "&7!", true));
            } else if (argList[i].toLowerCase().startsWith("name:") && !argList[i].toLowerCase().equalsIgnoreCase("name:")) {
                if (Utils.gymExists(argList[i].substring(5))) {
                    sender.sendMessage(Utils.toText("&7That gym already exists!", true));
                    return CommandResult.success();
                }
                gym.Name = argList[i].substring(5);
                sender.sendMessage(Utils.toText("&7Successfully changed the name of the &b" + argList[0] + "&7 gym to &b" + gym.Name + "&7!", true));
            } else if (argList[i].toLowerCase().startsWith("badge:") && !argList[i].toLowerCase().equalsIgnoreCase("badge:")) {
                gym.Badge = argList[i].substring(6);
                sender.sendMessage(Utils.toText("&7Successfully changed the badge of the &b" + gym.Name + "&7 gym to &b" + gym.Badge + "&7!", true));
            } else {
                sender.sendMessage(Utils.toText("&7Incorrect usage: /EditGym <gym> <name:(name) | badge:(badge) | require:(gym) | rules:(rules) | level:(level) | money:(money)>&7.", true));
            }
        }
        if (AGPConfig.Storage.storageType.equalsIgnoreCase("flatfile")) {
            Utils.editGym(gym);
            AGP.getInstance().getStorage().saveData(DataStruc.gcon);
        } else {
            Utils.addGym(gym);
        }

        return CommandResult.success();
    }

}
