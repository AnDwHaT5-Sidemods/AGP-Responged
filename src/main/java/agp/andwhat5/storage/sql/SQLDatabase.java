package agp.andwhat5.storage.sql;

import agp.andwhat5.Utils;
import agp.andwhat5.config.structs.*;
import agp.andwhat5.storage.Storage;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.*;
import java.util.regex.Pattern;

public abstract class SQLDatabase implements Storage {

    private final String gymTable;
    private final String badgeTable;

    SQLDatabase(String gymTable, String badgeTable) {
        this.gymTable = gymTable;
        this.badgeTable = badgeTable;
    }

    abstract HikariDataSource getHikari();

    @Override
    public void shutdown() {
        if (getHikari() != null) {
            getHikari().close();
        }
    }

    private Connection getConnection() throws SQLException {
        return getHikari().getConnection();
    }

    void createTables() {
        try {
            try (Connection connection = this.getConnection()) {
                if (connection == null || connection.isClosed()) {
                    throw new IllegalStateException("SQL connection is null");
                }

                try (PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `"
                        + badgeTable + "` (uuid CHAR(36) NOT NULL PRIMARY KEY, Player varchar(16), Badges MEDIUMTEXT);")) {
                    statement.executeUpdate();
                }
                // Gym Table for Data
                try (PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `" + gymTable
                        + "` (Gym varchar(50) NOT NULL PRIMARY KEY, Badge MEDIUMTEXT, LevelCap Integer, Commands " +
                        "MEDIUMTEXT, Money DECIMAL, " +
                        "Leaders MEDIUMTEXT, " +
                        "ArenaData MEDIUMTEXT, " +
                        "Lobby MEDIUMTEXT," +
                        "Requirement MEDIUMTEXT," +
                        "Rules MEDIUMTEXT);")) {
                    statement.executeUpdate();
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void updateTables() {
        try (Connection connection = this.getConnection()) {
            DatabaseMetaData data = connection.getMetaData();
            ResultSet rs = data.getColumns(null, null, this.gymTable, "ArenaData");
            if (!rs.next()) {
                PreparedStatement statement = this.getConnection().prepareStatement("ALTER TABLE `" + this.gymTable + "` " +
                        "ADD ArenaData MEDIUMTEXT;");
                statement.executeUpdate();
                statement.close();
            }

            rs = data.getColumns(null, null, this.gymTable, "Lobby");
            if (!rs.next()) {
                PreparedStatement statement = this.getConnection().prepareStatement("ALTER TABLE `" + this.gymTable + "` " +
                        "ADD Lobby MEDIUMTEXT;");
                statement.executeUpdate();
                statement.close();
            }
            rs = data.getColumns(null, null, this.gymTable, "Requirement");
            if (!rs.next()) {
                PreparedStatement statement = this.getConnection().prepareStatement("ALTER TABLE `" + this.gymTable + "` " +
                        "ADD Requirement MEDIUMTEXT;");
                statement.executeUpdate();
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void clearData(String table) {
        try {
            try (Connection connection = this.getConnection()) {
                if (connection == null || connection.isClosed()) {
                    throw new IllegalStateException("SQL connection is null");
                }

                try (PreparedStatement statement = connection.prepareStatement("TRUNCATE TABLE `" + table + "`")) {
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveData(DataStruc data) {
    }

    @Override
    public HashMap<UUID, PlayerStruc> getPlayerData() {
        HashMap<UUID, PlayerStruc> playerData = new HashMap<>();
        try {
            try (Connection connection = this.getConnection()) {
                if (connection == null || connection.isClosed()) {
                    throw new IllegalStateException("SQL connection is null");
                }

                try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM `" + badgeTable + "`")) {
                    ResultSet result = ps.executeQuery();
                    while (result.next()) {
                        UUID u = UUID.fromString(result.getString("uuid"));
                        String p = result.getString("Player");
                        String b = result.getString("Badges");

                        TypeToken<List<BadgeStruc>> type = new TypeToken<List<BadgeStruc>>() {
                        };
                        List<BadgeStruc> badges = getBadgeData(b, type);

                        PlayerStruc plst = new PlayerStruc();
                        plst.Name = p;
                        plst.Badges = badges;
                        playerData.put(u, plst);
                    }
                    result.close();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playerData;
    }

    private <V extends List<BadgeStruc>> List<BadgeStruc> getBadgeData(String badgeList, TypeToken<V> token) {
        List<BadgeStruc> badges = Lists.newArrayList();

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
        V response = gson.fromJson(badgeList, token.getType());
        if (response != null) {
            badges = response;
        }

        return badges;
    }

    @Override
    public void updateObtainedBadges(UUID uuid, String name, BadgeStruc badge, boolean add) {
        PlayerStruc data = DataStruc.gcon.PlayerData.get(uuid);
        if (data == null) {
            data = new PlayerStruc();
            data.Name = name;
        }
        if (add)
            data.Badges.add(badge);
        else
            data.Badges.remove(badge);

        try {
            try (Connection connection = this.getConnection()) {
                if (connection == null || connection.isClosed()) {
                    throw new IllegalStateException("SQL connection is null");
                }

                Gson gson = new Gson();
                try (PreparedStatement ps = connection.prepareStatement(
                        "REPLACE INTO `" + badgeTable + "` VALUES('" + uuid + "', '" + name
                                + "', '" + gson.toJson(data.Badges) + "')")) {
                    ps.executeUpdate();
                }
                DataStruc.gcon.PlayerData.put(uuid, data);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<GymStruc> getGyms() {
        List<GymStruc> gyms = Lists.newArrayList();
        try {
            try (Connection connection = this.getConnection()) {
                if (connection == null || connection.isClosed()) {
                    throw new IllegalStateException("SQL connection is null");
                }

                try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM `" + gymTable + "`")) {
                    ResultSet result = ps.executeQuery();
                    while (result.next()) {
                        GymStruc gym = new GymStruc();
                        gym.Name = result.getString("Gym");
                        gym.Badge = result.getString("Badge");
                        gym.Requirement = result.getString("Requirement");
                        gym.Rules = result.getString("Rules");
                        gym.LevelCap = result.getInt("LevelCap");
                        gym.Money = result.getInt("Money");
                        String[] playerLeaders = result.getString("PlayerLeaders").replace("[", "").replace("]", "").split(Pattern.quote(", "));
                        ArrayList<UUID> uuids = new ArrayList<>();
                        for (String playerLeader : playerLeaders) {
                            uuids.add(UUID.fromString(playerLeader));
                        }
                        gym.PlayerLeaders = uuids;
                        gym.Commands = Lists.newArrayList(
                                result.getString("Commands").replace("[", "").replace("]", "").split(Pattern.quote(", ")));

                        TypeToken<List<ArenaStruc>> arenaType = new TypeToken<List<ArenaStruc>>() {
                        };
                        gym.Arenas = new Gson().fromJson(result.getString("ArenaData"), arenaType.getType());
                        gym.Lobby = new Gson().fromJson(result.getString("Lobby"), Vec3dStruc.class);
                        // Legacy Gym Check
                        if (gym.Arenas == null) {
                            gym.Arenas = Lists.newArrayList();
                        }

                        if (gym.Lobby == null) {
                            gym.Lobby = new Vec3dStruc(0, 64, 0, 90, 0);
                        }

                        if (gym.Requirement == null) {
                            gym.Requirement = "null";
                        }

                        gyms.add(gym);
                    }
                    result.close();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return gyms;
    }

    @Override
    public void updateGyms(GymStruc gym, boolean add) {
        if (DataStruc.gcon == null) {
            DataStruc.gcon = new DataStruc();
        }
        if (DataStruc.gcon.GymData == null) {
            DataStruc.gcon.GymData = Lists.newArrayList();
        }

        try {
            try (Connection connection = this.getConnection()) {
                if (connection == null || connection.isClosed()) {
                    throw new IllegalStateException("SQL connection is null");
                }

                if (add) {
                    try (PreparedStatement ps = connection
                            .prepareStatement("REPLACE INTO `" + gymTable + "` VALUES('" + gym.Name + "', '" + gym
                                    .Badge + "', '" + gym.LevelCap + "', '" + gym.Commands + "', '" + gym.Money + "', '" + gym
                                    .PlayerLeaders + "', '" + new Gson().toJson(gym.Arenas) + "', '" + new Gson().toJson(gym.Lobby) + "', '" + gym.Requirement + "', '" + gym.Rules + "')")) {
                        ps.executeUpdate();
                    }
                    if (Utils.gymExists(gym.Name))
                        Utils.editGym(gym);
                    else
                        DataStruc.gcon.GymData.add(gym);
                } else {
                    try (PreparedStatement ps = connection
                            .prepareStatement("DELETE FROM `" + gymTable + "` WHERE Gym='" + gym.Name + "'")) {
                        ps.executeUpdate();
                    }
                    DataStruc.gcon.GymData.remove(gym);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateAllBadges(GymStruc gym) {
        for (UUID uuid : DataStruc.gcon.PlayerData.keySet()) {
            PlayerStruc ps = DataStruc.gcon.PlayerData.get(uuid);
            Optional<BadgeStruc> bs = ps.Badges.stream().filter(g -> g.Gym.equalsIgnoreCase(gym.Name)).findAny();
            bs.ifPresent(b -> updateObtainedBadges(uuid, ps.Name, b, false));
        }
    }

    @Override
    public void updateAllGyms(List<GymStruc> gyms) {
        for (GymStruc gym : gyms)
            updateGyms(gym, true);

    }

    @Override
    public void updateAllPlayers(HashMap<UUID, PlayerStruc> players) {
        for (UUID uuid : players.keySet()) {
            for (BadgeStruc badge : players.get(uuid).Badges)
                updateObtainedBadges(uuid, players.get(uuid).Name, badge, true);
        }
    }
}
