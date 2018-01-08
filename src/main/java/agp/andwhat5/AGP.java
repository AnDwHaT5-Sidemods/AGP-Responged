package agp.andwhat5;

import agp.andwhat5.commands.administrative.*;
import agp.andwhat5.commands.gyms.*;
import agp.andwhat5.commands.leaders.*;
import agp.andwhat5.commands.players.CancelChallenge;
import agp.andwhat5.commands.players.ChallengeGym;
import agp.andwhat5.commands.players.CheckBadges;
import agp.andwhat5.commands.players.GymWarp;
import agp.andwhat5.config.AGPConfig;
import agp.andwhat5.config.structs.GymStruc;
import agp.andwhat5.listeners.GymNPCDefeatListener;
import agp.andwhat5.listeners.GymPlayerDefeatListener;
import agp.andwhat5.listeners.ListenerBadgeObtained;
import agp.andwhat5.storage.FlatFileProvider;
import agp.andwhat5.storage.Storage;
import agp.andwhat5.storage.sql.H2Provider;
import agp.andwhat5.storage.sql.MySQLProvider;
import com.google.inject.Inject;
import com.pixelmonmod.pixelmon.Pixelmon;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import org.slf4j.Logger;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

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

import java.io.File;
import java.util.Timer;

@Plugin(id = "agp", name = "AGP", version = "0.5.3-beta", dependencies = @Dependency(id = "pixelmon"), description = "Another gym plugin.")
public class AGP
{
	private static AGP plugin;
	private static PluginContainer container;
	private static Logger logger;

	@Inject
	public AGP(PluginContainer container) {
		AGP.plugin = this;
		AGP.container = container;
		AGP.logger = container.getLogger();
	}

	public static AGP getPlugin() {
		return plugin;
	}
	public static PluginContainer getContainer() {
		return container;
	}
	public static Logger getLogger() {
		return logger;
	}

    // Forge things

	private static AGP mod;
	private Storage storage;
	private File base;
	public static EventBus EVENT_BUS = new EventBus();
	
	private Timer specialTimer;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		base = new File(event.getModConfigurationDirectory(), "agp");
		if (!base.exists())
		{
			base.mkdirs();
		}
		Configuration configuration = new Configuration(new File(base, "agp.cfg"));
		AGPConfig.load(configuration);
		if (configuration.hasChanged())
		{
			configuration.save();
		}
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		mod = this;
		if (AGPConfig.Storage.storageType.equalsIgnoreCase("mysql"))
		{
			this.storage = new MySQLProvider(AGPConfig.Storage.GymsTableName, AGPConfig.Storage.BadgesTableName);
		} else if (AGPConfig.Storage.storageType.equalsIgnoreCase("h2"))
		{
			this.storage = new H2Provider(AGPConfig.Storage.GymsTableName, AGPConfig.Storage.BadgesTableName);
		} else //flatfile
		{
			this.storage = new FlatFileProvider();
		}

		specialTimer = PlayerCheck.registerSpecials();

		try
		{
			this.storage.init();
			for(GymStruc gs : AGP.getInstance().getStorage().getGyms())
			{
				if(gs.Leaders.stream().anyMatch(l -> l.equalsIgnoreCase("NPC")))
				{
					gs.Status = 2;
				} else
				{
					gs.Status = 1;
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@EventHandler
	public void postInit(FMLLoadCompleteEvent event)
	{
		MinecraftForge.EVENT_BUS.register(AGPTickHandler.class);
		MinecraftForge.EVENT_BUS.register(new PlayerCheck());
		Pixelmon.EVENT_BUS.register(new GymNPCDefeatListener());
		Pixelmon.EVENT_BUS.register(new GymPlayerDefeatListener());
		AGP.EVENT_BUS.register(new ListenerBadgeObtained());
	}

	@EventHandler
	public void onServerStart(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new AcceptChallenge());
		event.registerServerCommand(new AddGym());
		event.registerServerCommand(new AddLeader());
		event.registerServerCommand(new AGPReload());
		event.registerServerCommand(new CancelChallenge());
		event.registerServerCommand(new ChallengeGym());
		event.registerServerCommand(new CheckBadges());
		event.registerServerCommand(new CloseGym());
		event.registerServerCommand(new DelBadge());
		event.registerServerCommand(new DeleteGym());
		event.registerServerCommand(new DeleteLeader());
		event.registerServerCommand(new DenyChallenge());
		event.registerServerCommand(new EditGym());
		event.registerServerCommand(new GiveBadge());
		event.registerServerCommand(new GymList());
		event.registerServerCommand(new GymWarp());
		event.registerServerCommand(new OpenGym());
		event.registerServerCommand(new QueueList());
		event.registerServerCommand(new SetGymWarp());
		event.registerServerCommand(new SpawnNPCLeader());
		event.registerServerCommand(new StorageConverter());
	}

	@EventHandler
	public void onServerStop(FMLServerStoppingEvent e)
	{
		try
		{
			this.storage.shutdown();
			specialTimer.cancel();
		} catch (Exception e1)
		{
			e1.printStackTrace();
		}
	}

	public static AGP getInstance()
	{
		return mod;
	}

	public Storage getStorage()
	{
		return this.storage;
	}

	public void setStorage(Storage storage)
	{
		this.storage = storage;
	}

	public File getBase()
	{
		return base;
	}
}
