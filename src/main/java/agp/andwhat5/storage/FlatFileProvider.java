package agp.andwhat5.storage;

import agp.andwhat5.Utils;
import agp.andwhat5.config.structs.*;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FlatFileProvider implements Storage {

    private static final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting()
            .create();
    private final File badges = new File("config/agp/data/badges.json");
    private final File gyms = new File("config/agp/data/gyms.json");

    public FlatFileProvider() {
        try {
            this.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init() throws Exception {
        if (Files.exists(Paths.get("config/gyms.json"))) {
            FileReader fr = new FileReader("config/gyms.json");
            ConfigStruc.gcon = gson.fromJson(fr, ConfigStruc.class);
            fr.close();
            if (ConfigStruc.gcon == null) {
                ConfigStruc.gcon = new ConfigStruc();
            }
        } else {
            try {
                if (DataStruc.gcon == null) {
                    DataStruc.gcon = new DataStruc();
                }

                File dir = new File("config/agp/data/");
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                if (!badges.exists()) {
                    badges.createNewFile();
                }

                if (!gyms.exists()) {
                    gyms.createNewFile();
                }

                FileReader fr = new FileReader(gyms.getPath());
                TypeToken<List<GymStruc>> type = new TypeToken<List<GymStruc>>() {
                };
                DataStruc.gcon.GymData = getGymData(fr, type);
                fr.close();

                Utils.sortGyms();

                fr = new FileReader(badges.getPath());
                TypeToken<HashMap<UUID, PlayerStruc>> mapType = new TypeToken<HashMap<UUID, PlayerStruc>>() {
                };
                DataStruc.gcon.PlayerData = getBadgeData(fr, mapType);
                fr.close();

                //Make sure the files are updated incase of data updates
                Utils.sortGyms();
                saveData(DataStruc.gcon);


            } catch (IOException | JsonSyntaxException | JsonIOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("deprecation")
    private <V extends List<GymStruc>> List<GymStruc> getGymData(Reader reader, TypeToken<V> token) {
        List<GymStruc> gyms = Lists.newArrayList();

        V response = gson.fromJson(reader, token.getType());
        if (response != null) {
            gyms = response;
        }


        return gyms;
    }

    private <V extends HashMap<UUID, PlayerStruc>> HashMap<UUID, PlayerStruc> getBadgeData(Reader reader,
                                                                                           TypeToken<V> token) {
        HashMap<UUID, PlayerStruc> badges = new HashMap<>();

        V response = gson.fromJson(reader, token.getType());
        if (response != null) {
            badges = response;
        }

        return badges;
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void clearData(String table) {
    }


    @Override
    public void saveData(DataStruc data) {
        try (FileWriter writer = new FileWriter("config/agp/data/gyms.json")) {
            gson.toJson(data.GymData, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileWriter writer = new FileWriter("config/agp/data/badges.json")) {
            gson.toJson(data.PlayerData, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public HashMap<UUID, PlayerStruc> getPlayerData() {
        return DataStruc.gcon.PlayerData;
    }

    @Override
    public void updateObtainedBadges(UUID uuid, String name, BadgeStruc badge, boolean add) {
        PlayerStruc ps = getPlayerData().get(uuid);
        if (ps == null) {
            ps = new PlayerStruc();
            ps.Name = name;
        }

        if (add)
            ps.Badges.add(badge);
        else
            ps.Badges.remove(badge);
        DataStruc.gcon.PlayerData.put(uuid, ps);
    }

    @Override
    public List<GymStruc> getGyms() {
        return DataStruc.gcon.GymData;
    }

    @Override
    public void updateGyms(GymStruc gym, boolean add) {
        if (DataStruc.gcon == null) {
            DataStruc.gcon = new DataStruc();
        }
        if (DataStruc.gcon.GymData == null) {
            DataStruc.gcon.GymData = Lists.newArrayList();
        }

        if (add)
            DataStruc.gcon.GymData.add(gym);
        else
            DataStruc.gcon.GymData.remove(gym);
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
            if (Utils.getGym(gym.Name) == null)
                updateGyms(gym, true);
    }

    @Override
    public void updateAllPlayers(HashMap<UUID, PlayerStruc> players) {
        for (UUID uuid : players.keySet()) {
            for (BadgeStruc badge : players.get(uuid).Badges) {
                updateObtainedBadges(uuid, players.get(uuid).Name, badge, true);
            }
        }
    }
}
