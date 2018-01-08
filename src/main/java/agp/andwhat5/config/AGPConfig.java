package agp.andwhat5.config;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

public class AGPConfig
{
	//TODO: Migrate to Configurate
	private static Configuration configuration;

	public static class Announcements
	{
		@Comment("Whether or not the AGP announcement message is enabled.")
		public static boolean announcementEnabled = true;
		@Comment("The time in ticks between each AGP announcement message.")
		public static int announcementTimer = 16_800;
		@Comment("The AGP announcement message. (Supports color formatting codes)")
		public static String announcementMessage = "&f[&dAGP&f] &7This server is running &bAGP &7created by &bAnDwHaT5! " +
				"&7Use &b/GymList &7to see what Gyms the server has to offer!";
		@Comment("Whether or not to announce leaders joining the server")
		public static boolean announceLeaderJoin = true;
		@Comment("The announcement for leaders joining the server. {leader} = Gym Leader. (Supports color formatting codes).")
		public static String leaderJoinMessage = "&f[&dAGP&f] &7Gym Leader &b{leader} &7has joined the server!";
		@Comment("Whether or not to announce leaders quiting the server")
		public static boolean announceLeaderQuit = true;
		@Comment("The announcement for leaders joining the server. {leader} = Gym Leader. (Supports color formatting codes).")
		public static String leaderQuitMessage = "&f[&dAGP&f] &7Gym Leader &b{leader} &7has left the server!";
		@Comment("Whether or not to announce leaders opening a Gym")
		public static boolean openAnnouncement = true;
		@Comment("The announcement for opening a gym. {leader} = Gym Leader, {gym} = gym opened. (Supports color formatting codes).")
		public static String openMessage = "&f[&dAGP&f] &7Gym Leader &b{leader} &7has opened the &b{gym} &7Gym!";
		@Comment("Whether or not to announce leaders closing a gym")
		public static boolean closeAnnouncement = true;
		@Comment("The announcement for closing a gym. {leader} = Gym Leader, {gym} = gym opened. (Supports color formatting codes).")
		public static String closeMessage = "&f[&dAGP&f] &7Gym Leader &b{leader} &7has closed the &b{gym} &7Gym!";
		@Comment("Whether or not to announce players beating a gym")
		public static boolean winAnnouncement = true;
		@Comment("The announcement for defeating a gym. {challenger} = player, {leader} = Gym Leader, {gym} = gym defeated. (Supports color formatting codes).")
		public static String winMessage = "&f[&dAGP&f] &7Challenger &b{challenger} &7has beat the &b{gym} &7Gym!";
	}

	public static class General
	{
		@Comment("Should physical gym badges be given along with the digital ones?")
		public static boolean physicalBadge = true;
		@Comment("Should a Gym show NPC Mode as its status when the last leader of a certain gym logs off and an NPC is present?")
		public static boolean offlineNPC = true;
		@Comment("Should a gym be automatically be opened when a leader from a gym with no other leaders online joins the server?")
		public static boolean autoOpen = true;
	}

	public static class Storage
	{
		@Comment("Valid storage types are: flatfile, h2, mysql.")
		public static String storageType = "flatfile";
		@Comment("The table for badges to be stored under. Only valid with h2 or mysql storage options.")
		public static String BadgesTableName = "agp_badges";
		@Comment("The table for gyms to be stored under. Only valid with h2 or mysql storage options.")
		public static String GymsTableName = "agp_gyms";
		@Comment("The mysql username.")
		public static String MysqlUsername = "username";
		@Comment("The mysql password.")
		public static String MysqlPassword = "password1";
		@Comment("The mysql database name.")
		public static String MysqlDatabaseName = "database";
		@Comment("The mysql address.")
		public static String MysqlAddress = "localhost";
		@Comment("The mysql port.")
		public static int MysqlPort = 3306;
	}
	public static void load(Configuration configuration)
	{
		AGPConfig.configuration = configuration;
		reload(false);
	}
	public static void reload(boolean loadFromDisk)
	{
		if (loadFromDisk) configuration.load();
		List<Class> classes = Lists.newArrayList(AGPConfig.class, Storage.class, Announcements.class, General.class);
		Joiner NEW_LINE = Joiner.on('\n');
		for (Class clazz : classes)
		{
			String category = (clazz == AGPConfig.class) ? "general" : clazz.getSimpleName().toLowerCase();
			for (Field field : clazz.getDeclaredFields())
			{
				if (Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers()))
				{
					Class<?> type = field.getType();
					Comment comment = field.getAnnotation(Comment.class);
					String sComment = comment != null ? NEW_LINE.join(comment.value()) : "";
					if (type == boolean.class)
					{
						set(field, configuration.get(category, field.getName(), getBoolean(field), sComment), type);
					} else if (type == int.class)
					{
						set(field, configuration.get(category, field.getName(), getInt(field), sComment), type);
					} else if (type == String.class)
					{
						set(field, configuration.get(category, field.getName(), getString(field), sComment), type);
					}
				}
			}
		}
	}
	private static void set(Field field, Property prop, Class<?> type)
	{
		try
		{
			if (type == boolean.class)
			{
				field.setBoolean(null, prop.getBoolean());
			} else if (type == int.class)
			{
				field.setInt(null, prop.getInt());
			} else if (type == String.class)
			{
				field.set(null, prop.getString());
			}
		} catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}
	private static boolean getBoolean(Field field)
	{
		try
		{
			return field.getBoolean(null);
		} catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		return true;
	}
	private static int getInt(Field field)
	{
		try
		{
			return field.getInt(null);
		} catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		return 0;
	}
	private static String getString(Field field)
	{
		try
		{
			return (String) field.get(null);
		} catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		return "";
	}
}
