package agp.andwhat5.commands.gyms;

import agp.andwhat5.AGP;
import agp.andwhat5.Utils;
import agp.andwhat5.config.AGPConfig;
import agp.andwhat5.config.structs.DataStruc;
import agp.andwhat5.config.structs.GymStruc;
import com.pixelmonmod.pixelmon.config.PixelmonConfig;
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
    public CommandResult execute(CommandSource sender, CommandContext args) {


        //TODO redesign this entire thing..

        GymStruc gym = args.<GymStruc>getOne("GymName").get();
        Optional<String> args1 = args.getOne("args");
        if (!args1.isPresent()) {
            sender.sendMessage(Utils.toText("&7Incorrect usage: /EditGym <gym> <name:(name) | badge:(badge) | weight:(weight) | minpokemon:(minpokemon) | maxpokemon:(maxpokemon) | require:(gym) | rules:(rules) | level:(level) | money:(money)>&7.", true));
            return CommandResult.success();
        }

        String[] argList = args1.get().split(" ");

        // Parse options
        for (String anArgList : argList) {
            if (anArgList.toLowerCase().startsWith("minpokemon:") && !anArgList.toLowerCase().equalsIgnoreCase("minpokemon:")) {
                int minPokemon = Integer.parseInt(anArgList.substring(11));
                if (minPokemon > 0 && minPokemon < 7) {
                    gym.minimumPokemon = minPokemon;
                    sender.sendMessage(Utils.toText("&7Successfully changed the minimum gym Pokemon count of the &b" + gym.Name + "&7 gym to &b" + minPokemon + "&7!", true));
                } else
                    sender.sendMessage(Utils.toText("&7The minimum amount of Pokemon must be 1-6.", true));
            } else if (anArgList.toLowerCase().startsWith("maxpokemon:") && !anArgList.toLowerCase().equalsIgnoreCase("maxpokemon:")) {
                int maxPokemon = Integer.parseInt(anArgList.substring(11));
                if (maxPokemon > 0 && maxPokemon < 7) {
                    gym.maximumPokemon = maxPokemon;
                    sender.sendMessage(Utils.toText("&7Successfully changed the maximum gym Pokemon count of the &b" + gym.Name + "&7 gym to &b" + maxPokemon + "&7!", true));
                } else
                    sender.sendMessage(Utils.toText("&7The maximum amount of Pokemon must be 1-6.", true));
            } else if (anArgList.toLowerCase().startsWith("weight:") && !anArgList.toLowerCase().equalsIgnoreCase("weight:")) {
                int weight = Integer.parseInt(anArgList.substring(7));
                gym.Weight = weight;
                sender.sendMessage(Utils.toText("&7Successfully changed the weight of the &b" + gym.Name + "&7 gym to &b" + weight + "&7!", true));
            } else if (anArgList.toLowerCase().startsWith("rules:") && !anArgList.toLowerCase().equalsIgnoreCase("rules:")) {
                gym.Rules = "";
                for (String s : argList) {
                    if (s.startsWith("rules:")) {
                        gym.Rules += s.split("rules:")[1] + " ";
                    } else {
                        gym.Rules += s + " ";
                    }
                }
                if (AGPConfig.Storage.storageType.equalsIgnoreCase("flatfile")) {
                    Utils.editGym(gym);
                    AGP.getInstance().getStorage().saveData(DataStruc.gcon);
                } else {
                    Utils.addGym(gym);
                }
                sender.sendMessage(Utils.toText("&7Successfully updated the gyms rules!", true));
                return CommandResult.success();
            } else if (anArgList.toLowerCase().startsWith("require:") && !anArgList.toLowerCase().equalsIgnoreCase("require:")) {
                if (!Utils.gymExists(anArgList.substring(8))) {
                    sender.sendMessage(Utils.toText("&7The &b" + anArgList.substring(8) + " &7gym does not exist!", true));
                    return CommandResult.success();
                }
                GymStruc g = Utils.getGym(anArgList.substring(8));
                if (argList[0].equalsIgnoreCase(g.Name)) {
                    sender.sendMessage(Utils.toText("&7You can not set this gyms requirement to itself!", true));
                    return CommandResult.success();
                }
                gym.Requirement = g.Name;
                sender.sendMessage(Utils.toText("&7Successfully changed the requirement of the &b" + gym.Name + "&7 gym to the &b" + g.Name + "&7 gym!", true));
            } else if (anArgList.toLowerCase().startsWith("level:") && !anArgList.toLowerCase().equalsIgnoreCase("level:")) {
                int levelcap = Integer.valueOf(anArgList.substring(6));
                if (levelcap < 0 || levelcap > PixelmonConfig.maxLevel) {
                    sender.sendMessage(Utils.toText("&7The level you provided is out of the level cap range, &b0-" + PixelmonConfig.maxLevel + "&7!", true));
                    return CommandResult.success();
                }
                gym.LevelCap = Integer.valueOf(anArgList.substring(6));
                sender.sendMessage(Utils.toText("&7Successfully changed the level cap of the &b" + gym.Name + "&7 gym to &b" + gym.LevelCap + "&7!", true));
            } else if (anArgList.toLowerCase().startsWith("money:") && !anArgList.toLowerCase().equalsIgnoreCase("money:")) {
                gym.Money = Integer.parseInt(anArgList.substring(6));
                sender.sendMessage(Utils.toText("&7Successfully changed the reward money of the &b" + gym.Name + "&7 gym to &b" + gym.Money + "&7!", true));
            } else if (anArgList.toLowerCase().startsWith("name:") && !anArgList.toLowerCase().equalsIgnoreCase("name:")) {
                if (Utils.gymExists(anArgList.substring(5))) {
                    sender.sendMessage(Utils.toText("&7That gym already exists!", true));
                    return CommandResult.success();
                }
                gym.Name = anArgList.substring(5);
                sender.sendMessage(Utils.toText("&7Successfully changed the name of the &b" + argList[0] + "&7 gym to &b" + gym.Name + "&7!", true));
            } else if (anArgList.toLowerCase().startsWith("badge:") && !anArgList.toLowerCase().equalsIgnoreCase("badge:")) {
                gym.Badge = anArgList.substring(6);
                sender.sendMessage(Utils.toText("&7Successfully changed the badge of the &b" + gym.Name + "&7 gym to &b" + gym.Badge + "&7!", true));
            } else {
                sender.sendMessage(Utils.toText("&7Incorrect usage: /EditGym <gym> <name:(name) | badge:(badge) | weight:(weight) | minpokemon:(minpokemon) | maxpokemon:(maxpokemon) | require:(gym) | rules:(rules) | level:(level) | money:(money)>&7.", true));
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
