package agp.andwhat5;

/* A file header of amazingness
 *                               ,-'   ,"",
                             / / ,-'.-'
                   _,..-----+-".".-'_,..
           ,...,."'             `--.---'
         /,..,'                     `.
       ,'  .'                         `.
      j   /                             `.
      |  /,----._           ,.----.       .
     ,  j    _   \        .'  .,   `.     |
   ,'   |        |  ____  |         | ."--+,^.
  /     |`-....-',-'    `._`--....-' _/      |
 /      |     _,'          `--..__  `        '
j       | ,-"'    `    .'         `. `        `.
|        .\                        /  |         \
|         `\                     ,'   |          \
|          |                    |   ,-|           `.
.         ,'                    |-"'  |             \
 \       /                      `.    |              .
  ` /  ,'                        |    `              |
   /  /                          |     \             |
  /  |                           |      \           /
 /   |                           |       `.       _,
.     .                         .'         `.__,.',.----,
|      `.                     ,'             .-""      /
|        `._               _.'               |        /
|           `---.......,--"                  |      ,'
'                                            '    ,'
 \                                          /   ,'
  \                                        /  ,'
   \                                      / ,'
    `.                                   ,+'
      >.                               ,'
  _.-'  `-.._                      _,-'-._
,__          `",-............,.---"       `.
   \..---. _,-'            ,'               `.
          "                '..,--.___,-"""---'
 *
 * AGP designed by AnDwHaT5
 */

import agp.andwhat5.commands.administrative.*;
import agp.andwhat5.commands.commandelements.GymNameCommandElement;
import agp.andwhat5.commands.gyms.*;
import agp.andwhat5.commands.leaders.*;
import agp.andwhat5.commands.players.*;
import agp.andwhat5.config.AGPConfig;
import agp.andwhat5.config.structs.GymStruc;
import agp.andwhat5.listeners.ListenerBadgeObtained;
import agp.andwhat5.listeners.forge.GymNPCDefeatListener;
import agp.andwhat5.listeners.forge.GymPlayerDefeatListener;
import agp.andwhat5.storage.FlatFileProvider;
import agp.andwhat5.storage.Storage;
import agp.andwhat5.storage.sql.H2Provider;
import agp.andwhat5.storage.sql.MySQLProvider;
import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.pixelmonmod.pixelmon.Pixelmon;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.CatalogTypes;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;

import static agp.andwhat5.config.structs.GymStruc.EnumStatus.*;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Timer;
import java.util.UUID;

@Plugin(id = "agp", name = "AGP Responged", version = "1.0.0-Beta1", dependencies = @Dependency(id = "pixelmon"), description = "Another gym plugin... but for Sponge!", authors = {"AnDwHaT5", "ClientHax"})
public class AGP {

    private static AGP instance;
    @Inject
    public PluginContainer container;
    public AGPConfig config;
    Task announcementTask;
    @Inject
    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> configLoader;
    private CommentedConfigurationNode node;
    private Storage storage; //TODO: Look into storage options.
    private File base;
    //private Timer specialTimer;

    //TODO: Move storage related actions to a dedicated class

    public static AGP getInstance() {
        return instance;
    }

    //TODO make config converter
    public void loadConfig() throws IOException, ObjectMappingException {
        //Config
        this.node = this.configLoader.load();
        TypeToken<AGPConfig> type = TypeToken.of(AGPConfig.class);
        this.config = node.getValue(type, new AGPConfig());
        node.setValue(type, this.config);
        this.configLoader.save(node);
        //End config
    }

    public void saveConfig() {
        try {
            TypeToken<AGPConfig> type = TypeToken.of(AGPConfig.class);
            node.setValue(type, this.config);
            this.configLoader.save(node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
	@Listener
    public void init(GameStartedServerEvent event) {
        instance = this;
        if (AGPConfig.Storage.storageType.equalsIgnoreCase("mysql")) {
            this.storage = new MySQLProvider(AGPConfig.Storage.GymsTableName, AGPConfig.Storage.BadgesTableName);
        } else if (AGPConfig.Storage.storageType.equalsIgnoreCase("h2")) {
            this.storage = new H2Provider(AGPConfig.Storage.GymsTableName, AGPConfig.Storage.BadgesTableName);
        } else //flatfile
        {
            this.storage = new FlatFileProvider();
        }

        //specialTimer = PlayerCheck.registerSpecials();

        try {
            this.storage.init();
            //Do conversion from old type player list
            if(Utils.getGymStrucs(true).stream().anyMatch(g -> !g.Leaders.isEmpty()))
            {
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
		                            if(user1 != null)
		                            {
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
            //AGP.getInstance().getStorage().getGyms()
            for (GymStruc gs : Utils.getGymStrucs(true)) {
                if (gs.NPCAmount > 0) {
                    gs.Status = NPC;
                } else {
                    gs.Status = CLOSED;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setupTasks() {
        if (announcementTask != null)
            announcementTask.cancel();

        if (AGPConfig.Announcements.announcementEnabled) {
            announcementTask = Task.builder()
                    .execute(task -> Utils.sendToAll(AGPConfig.Announcements.announcementMessage, true))
                    .intervalTicks(AGPConfig.Announcements.announcementTimer)
                    .submit(this);
        }
    }

    @Listener
    public void postInit(GameStartedServerEvent event) {
        setupTasks();
        Sponge.getEventManager().registerListeners(this, new PlayerCheck());
        Pixelmon.EVENT_BUS.register(new GymNPCDefeatListener());
        Pixelmon.EVENT_BUS.register(new GymPlayerDefeatListener());
        Sponge.getEventManager().registerListeners(this, new ListenerBadgeObtained());
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) throws IOException, ObjectMappingException {
        loadConfig();

        CommandManager commandManager = Sponge.getCommandManager();
        commandManager.register(this, new AcceptChallenge(), "acceptchallenge", "ac");
        commandManager.register(this, new AddLeader(), "addleader");
        commandManager.register(this, new AGPReload(), "agpreload");
        commandManager.register(this, new DelBadge(), "delbadge");
        commandManager.register(this, new DeleteLeader(), "delleader");
        commandManager.register(this, new DenyChallenge(), "denychallenge", "dc");
        commandManager.register(this, new EditGym(), "editgym");//This command makes we want to kill myself.
        commandManager.register(this, new GiveBadge(), "givebadge");
        commandManager.register(this, new GymWarp(), "gymwarp");//This command makes me want to kill myself.
        commandManager.register(this, new QueueList(), "queuelist", "ql");
        commandManager.register(this, new SetGymWarp(), "setgymwarp", "sgw", "setgwarp");//This command also makes me want to kill myself
        commandManager.register(this, new SpawnNPCLeader(), "spawnnpcleader", "snl", "spawnleader");
        commandManager.register(this, new StorageConverter(), "stc");

        CommandSpec gymRulesSpec = CommandSpec.builder()
                .description(Text.of("Shows the rules for the specified gym."))
                .permission("agp.command.gymrules")
                .executor(new GymRules())
                .arguments(
                        GenericArguments.onlyOne(GymNameCommandElement.gym(Text.of("GymName")))
                )
                .build();
        commandManager.register(this, gymRulesSpec, "gymrules");

        CommandSpec checkBadgesSpec = CommandSpec.builder()
                .description(Text.of("Checks your or another players badges."))
                .permission("agp.command.checkbadges")
                .executor(new CheckBadges())
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.playerOrSource(Text.of("target")))
                )
                .build();
        commandManager.register(this, checkBadgesSpec, "checkbadges", "cb", "badges");

        CommandSpec challengeGymSpec = CommandSpec.builder()
                .description(Text.of("Challenges the specified gym to a battle."))
                .permission("agp.command.challangegym")
                .executor(new ChallengeGym())
                .arguments(
                        GenericArguments.onlyOne(GymNameCommandElement.gym(Text.of("GymName")))
                )
                .build();
        commandManager.register(this, challengeGymSpec, "challengegym", "chalgym");

        CommandSpec cancelChallengeSpec = CommandSpec.builder()
                .description(Text.of("Cancels a challenge issues to a gym."))
                .permission("agp.command.cancelchallenge")
                .executor(new CancelChallenge())
                .build();
        commandManager.register(this, cancelChallengeSpec, "cancelchallenge", "cc");

        CommandSpec listGymCommandsSpec = CommandSpec.builder()
                .description(Text.of("Displays all of the commands in the gyms rewards pool."))
                .permission("agp.command.listgymcommands")
                .executor(new ListGymCommands())
                .arguments(
                        GenericArguments.onlyOne(GymNameCommandElement.gym(Text.of("GymName")))
                )
                .build();
        commandManager.register(this, listGymCommandsSpec, "listgymcommands");

        CommandSpec gymListSpec = CommandSpec.builder()
                .description(Text.of("Shows all of the gyms the server has."))
                .permission("agp.command.gymlist")
                .executor(new GymList())
                .build();
        commandManager.register(this, gymListSpec, "gymlist", "gl", "gyms");

        CommandSpec deleteGymSpec = CommandSpec.builder()
                .description(Text.of("Deletes the specified gym."))
                .permission("agp.command.delgym")
                .executor(new DeleteGym())
                .arguments(
                        GenericArguments.onlyOne(GymNameCommandElement.gym(Text.of("GymName")))
                )
                .build();
        commandManager.register(this, deleteGymSpec, "delgym");

        CommandSpec addGymCommandSpec = CommandSpec.builder()
                .description(Text.of("Adds a command to the specified gyms rewards pool."))
                .permission("agp.command.addgymcommand")
                .executor(new AddGymCommand())
                .arguments(
                        GenericArguments.onlyOne(GymNameCommandElement.gym(Text.of("GymName"))),
                        GenericArguments.onlyOne(GenericArguments.remainingJoinedStrings(Text.of("command")))
                )
                .build();
        commandManager.register(this, addGymCommandSpec, "addgymcommand");

        CommandSpec delGymCommandSpec = CommandSpec.builder()
                .description(Text.of("Deletes a command from the specified gyms rewards pool."))
                .permission("agp.command.delgymcommand")
                .executor(new DelGymCommand())
                .arguments(
                        GenericArguments.onlyOne(GymNameCommandElement.gym(Text.of("GymName"))),
                        GenericArguments.onlyOne(GenericArguments.remainingJoinedStrings(Text.of("command")))
                )
                .build();
        commandManager.register(this, delGymCommandSpec, "delgymcommand");

        CommandSpec openGymSpec = CommandSpec.builder()
                .description(Text.of("Opens the specified gym."))
                .permission("agp.command.opengym")
                .executor(new OpenGym())
                .arguments(
                        GenericArguments.onlyOne(GymNameCommandElement.gym(Text.of("GymName")))
                )
                .build();
        commandManager.register(this, openGymSpec, "opengym");

        CommandSpec closeGymSpec = CommandSpec.builder()
                .description(Text.of("Closes the specified gym."))
                .permission("agp.command.closegym")
                .executor(new CloseGym())
                .arguments(
                        GenericArguments.onlyOne(GymNameCommandElement.gym(Text.of("GymName"))),
                        GenericArguments.optional(GenericArguments.string(Text.of("-f")))
                )
                .build();
        commandManager.register(this, closeGymSpec, "closegym");

        CommandSpec addGymSpec = CommandSpec.builder()
                .description(Text.of("Adds a gym with the specified badge."))
                .permission("agp.command.addgym")
                .executor(new AddGym())
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.string(Text.of("GymName"))),
                        GenericArguments.onlyOne(GenericArguments.catalogedElement(Text.of("BadgeItem"), CatalogTypes.ITEM_TYPE))
                )
                .build();
        commandManager.register(this, addGymSpec, "addgym");
    }

    @Listener
    public void onServerStop(GameStoppedServerEvent e) {
        try {
            this.storage.shutdown();
            //specialTimer.cancel();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public Storage getStorage() {
        return this.storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public File getBase() {
        return base;
    }

}
