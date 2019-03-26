package agp.andwhat5.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@SuppressWarnings("CanBeFinal")
@ConfigSerializable
public class AGPConfig {

    @SuppressWarnings("unused")
    @Setting
    Announcements announcements = new Announcements();

    @SuppressWarnings("unused")
    @Setting
    General general = new General();

    @SuppressWarnings("unused")
    @Setting
    Storage storage = new Storage();

    @SuppressWarnings("CanBeFinal")
    @ConfigSerializable
    public static class Announcements {
        @Setting(comment = "Whether or not the AGP announcement message is enabled.")
        public static boolean announcementEnabled = true;

        @Setting(comment = "The prefix AGP will use in all chat messages.")
        public static String agpPrefix = "&f[&dAGP-R&f] ";

        @Setting(comment = "The time in ticks between each AGP announcement message.")
        public static int announcementTimer = 16_800;

        @Setting(comment = "The AGP announcement message. (Supports color formatting codes)")
        public static String announcementMessage = "&7This server is running &bAGP Responged &7created by &bAnDwHaT5! &7Use &b/GymList &7to see what Gyms the server has to offer!";

        @Setting(comment = "Whether or not to announce leaders joining the server")
        public static boolean announceLeaderJoin = true;

        @Setting(comment = "The announcement for leaders joining the server. {leader} = Gym Leader. (Supports color formatting codes).")
        public static String leaderJoinMessage = "&7Gym Leader &b{leader} &7has joined the server!";

        @Setting(comment = "Whether or not to announce leaders quiting the server")
        public static boolean announceLeaderQuit = true;

        @Setting(comment = "The announcement for leaders joining the server. {leader} = Gym Leader. (Supports color formatting codes).")
        public static String leaderQuitMessage = "&7Gym Leader &b{leader} &7has left the server!";

        @Setting(comment = "Whether or not to announce leaders opening a Gym")
        public static boolean openAnnouncement = true;

        @Setting(comment = "The announcement for opening a gym. {leader} = Gym Leader, {gym} = gym opened. (Supports color formatting codes).")
        public static String openMessage = "&7Gym Leader &b{leader} &7has opened the &b{gym} &7Gym!";

        @Setting(comment = "Whether or not to announce leaders closing a gym")
        public static boolean closeAnnouncement = true;

        @Setting(comment = "The announcement for closing a gym. {leader} = Gym Leader, {gym} = gym opened. (Supports color formatting codes).")
        public static String closeMessage = "&7Gym Leader &b{leader} &7has closed the &b{gym} &7Gym!";

        @Setting(comment = "Whether or not to announce players beating a gym")
        public static boolean winAnnouncement = true;

        @Setting(comment = "The announcement for defeating a gym. {challenger} = player, {leader} = Gym Leader, {gym} = gym defeated. (Supports color formatting codes).")
        public static String winMessage = "&7Challenger &b{challenger} &7has beat the &b{gym} &7Gym!";
    }

    @SuppressWarnings("CanBeFinal")
    @ConfigSerializable
    public static class General {
        @Setting(comment = "Should physical gym badges be given along with the digital ones?")
        public static boolean physicalBadge = true;

        @Setting(comment = "Should a Gym show NPC Mode as its status when the last leader of a certain gym logs off and an NPC is present?")
        public static boolean offlineNPC = true;

        @Setting(comment = "Should a gym be automatically be opened when a leader from a gym with no other leaders online joins the server?")
        public static boolean autoOpen = true;
    }

    @SuppressWarnings("CanBeFinal")
    @ConfigSerializable
    public static class Storage {
        @Setting(comment = "Valid storage types are: flatfile, h2, mysql.")
        public static String storageType = "flatfile";

        @Setting(comment = "The table for badges to be stored under. Only valid with h2 or mysql storage options.")
        public static String BadgesTableName = "agp_badges";

        @Setting(comment = "The table for gyms to be stored under. Only valid with h2 or mysql storage options.")
        public static String GymsTableName = "agp_gyms";

        @Setting(comment = "The mysql username.")
        public static String MysqlUsername = "username";

        @Setting(comment = "The mysql password.")
        public static String MysqlPassword = "password1";

        @Setting(comment = "The mysql database name.")
        public static String MysqlDatabaseName = "database";

        @Setting(comment = "The mysql address.")
        public static String MysqlAddress = "localhost";

        @Setting(comment = "The mysql port.")
        public static int MysqlPort = 3306;
    }
}
