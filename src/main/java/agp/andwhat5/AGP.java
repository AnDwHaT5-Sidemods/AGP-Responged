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
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import static agp.andwhat5.config.structs.GymStruc.EnumStatus.*;


import java.io.File;
import java.io.IOException;
import java.util.Timer;

@Plugin(id = "agp", name = "AGP", version = "0.5.6-DevBuild2", dependencies = @Dependency(id = "pixelmon"), description = "Another gym plugin.")
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
    private Timer specialTimer;

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

        specialTimer = PlayerCheck.registerSpecials();

        try {
            this.storage.init();
            for (GymStruc gs : AGP.getInstance().getStorage().getGyms()) {
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
                    .execute(task -> Utils.sendToAll(AGPConfig.Announcements.announcementMessage, false))
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
        commandManager.register(this, new AddGym(), "addgym");
        commandManager.register(this, new AddLeader(), "addleader");
        commandManager.register(this, new AGPReload(), "agpreload");
        commandManager.register(this, new CancelChallenge(), "cancelchallenge", "cc");
        commandManager.register(this, new ChallengeGym(), "chalgym", "challengegym");
        commandManager.register(this, new CheckBadges(), "checkbadges", "cb", "badges");
        commandManager.register(this, new CloseGym(), "closegym");
        commandManager.register(this, new DelBadge(), "delbadge");
        commandManager.register(this, new DeleteGym(), "delgym");
        commandManager.register(this, new GymRules(), "gymrules");
        commandManager.register(this, new DeleteLeader(), "delleader");
        commandManager.register(this, new DenyChallenge(), "denychallenge", "dc");
        commandManager.register(this, new EditGym(), "editgym");
        commandManager.register(this, new GiveBadge(), "givebadge");
        commandManager.register(this, new GymList(), "gl", "gyms", "gymlist");
        commandManager.register(this, new GymWarp(), "gymwarp");
        commandManager.register(this, new OpenGym(), "opengym");
        commandManager.register(this, new QueueList(), "queuelist", "ql");
        commandManager.register(this, new SetGymWarp(), "setgymwarp", "sgw", "setgwarp");
        commandManager.register(this, new SpawnNPCLeader(), "spawnnpcleader", "snl", "spawnleader");
        commandManager.register(this, new StorageConverter(), "stc");
    }

    @Listener
    public void onServerStop(GameStoppedServerEvent e) {
        try {
            this.storage.shutdown();
            specialTimer.cancel();
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
