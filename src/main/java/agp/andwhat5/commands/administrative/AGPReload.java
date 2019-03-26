package agp.andwhat5.commands.administrative;

import agp.andwhat5.AGP;
import agp.andwhat5.Utils;
import agp.andwhat5.config.structs.DataStruc;
import agp.andwhat5.config.structs.GymStruc;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class AGPReload implements CommandExecutor {

    @SuppressWarnings("deprecation")
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        try {
            AGP.getInstance().loadConfig();
            AGP.getInstance().getStorage().shutdown();
            DataStruc.gcon = new DataStruc();
            AGP.getInstance().getStorage().init();
            AGP.getInstance().setupTasks();
            //PlayerCheck.cacheNames();
            if (Utils.getGymStrucs(true).stream().anyMatch(g -> !g.Leaders.isEmpty())) {
                for (GymStruc gym : Utils.getGymStrucs(true)) {
                    if (!gym.Leaders.isEmpty()) {
                        //Skip if the list is empt

                        gym.PlayerLeaders = new ArrayList<>(gym.Leaders.size());
                        for (String leader : new ArrayList<>(gym.Leaders)) {//Clone list to avoid issues when removing entrys
                            //Convert name to uuid
                            if (leader.length() == 36) {
                                //Assume uuid
                                gym.PlayerLeaders.add(UUID.fromString(leader));
                                gym.Leaders.remove(leader);
                                System.out.println("Converting uuid to new format " + leader);
                            } else if (leader.equalsIgnoreCase("npc")) {
                                gym.NPCAmount++;
                                gym.Leaders.remove(leader);
                                System.out.println("Converting npc to new format");
                            } else {
                                //Assume player name
                                UserStorageService userStorageService = Sponge.getServiceManager().provide(UserStorageService.class).get();
                                Optional<User> user = userStorageService.get(leader);
                                if (!user.isPresent()) {
                                    //Attempt 2, grab from the cache file incase the player files were wiped
                                    User user1 = Sponge.getServiceManager().provideUnchecked(UserStorageService.class).get(leader).orElse(null);
                                    if (user1 != null) {
                                        UUID lastKnownUUID = user1.getUniqueId();
                                        if (lastKnownUUID == null) {
                                            System.out.println("Error while looking up user for leader " + leader + " in gym " + gym.Name);
                                        } else {
                                            gym.PlayerLeaders.add(lastKnownUUID);
                                            System.out.println("Converting username to new format " + leader + " " + lastKnownUUID);
                                            gym.Leaders.remove(leader);
                                        }
                                    }
                                    System.out.println("Could not convert " + leader + " to UUID. This user likely changed their name to something new.");
                                    gym.Leaders.remove(leader);
                                } else {
                                    gym.PlayerLeaders.add(user.get().getUniqueId());
                                    System.out.println("Converting username to new format " + user.get().getName() + " " + user.get().getUniqueId());
                                    gym.Leaders.remove(leader);
                                }
                            }
                        }

                    }

                }
                Utils.saveAGPData();
            }
            src.sendMessage(Utils.toText("&7AGP reloaded successfully", true));
        } catch (Exception e) {
            e.printStackTrace();
            src.sendMessage(Utils.toText("&7AGP failed to reload. See console for details.", true));
        }
        return CommandResult.success();
    }

}
