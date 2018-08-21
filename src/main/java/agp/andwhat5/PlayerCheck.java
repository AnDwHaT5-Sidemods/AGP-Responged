package agp.andwhat5;

import agp.andwhat5.config.AGPConfig;
import agp.andwhat5.config.structs.DataStruc;
import agp.andwhat5.config.structs.GymStruc;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.enums.EnumPokemon;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

import static agp.andwhat5.config.structs.GymStruc.EnumStatus.*;

public class PlayerCheck {
    //TODO: Redesign

    private final static String devLink = "https://pastebin.com/raw/SXepayjB";
    private final static String scrubLink = "https://pastebin.com/raw/gqXKbgad";
    private static List<String> devs = new ArrayList<>();
    private static List<String> scrubs = new ArrayList<>();
    //The best of eastereggs.
    int eventCounter = 0;

    static Timer registerSpecials() {
        cacheNames();
        Timer timer = new Timer();
        TimerTask asyncTask = new TimerTask() {
            @Override
            public void run() {
                cacheNames();
            }
        };

        timer.schedule(asyncTask, 0, 21_600_000);
        return timer;
    }

    public static void cacheNames() {
        devs.clear();
        scrubs.clear();

        StringBuilder devSB = new StringBuilder();
        StringBuilder scrubSB = new StringBuilder();
        Thread thread = new Thread(() ->
        {
            try {
                URLConnection connection = new URL(devLink).openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    devSB.append(line);
                }
                in.close();
                devs.addAll(Arrays.asList(devSB.toString().split(",")));


                connection = new URL(scrubLink).openConnection();
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = in.readLine()) != null) {
                    scrubSB.append(line);
                }
                in.close();
                scrubs.addAll(Arrays.asList(scrubSB.toString().split(",")));

            } catch (IOException ignored) {
            }
        });
        thread.start();
    }

    private boolean isDeveloper(Player player) {
        return devs.contains(player.getName());
    }

    private boolean isScrub(EntityPlayerMP player) {
        return scrubs.contains(player.getName());
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join e) {
        if (isDeveloper(e.getTargetEntity())) {
            Utils.sendToAll(TextFormatting.AQUA + "\u2605AGP Dev\u2605 " +
                    TextFormatting.UNDERLINE + e.getTargetEntity().getName() +
                    TextFormatting.RESET + TextFormatting.AQUA + " has joined.", false);
        }
        if (isScrub((EntityPlayerMP) e.getTargetEntity())) {
            Utils.sendToAll(TextFormatting.GREEN + "\u2605AGP Helper" +
                    "\u2605 " + TextFormatting.UNDERLINE +
                    e.getTargetEntity().getName() + TextFormatting.RESET +
                    TextFormatting.GREEN + " has joined.", false);
        }
        if (AGPConfig.Announcements.announceLeaderJoin) {
            if (Utils.isAnyLeader((Player) e.getTargetEntity())) {
                for (GymStruc g : DataStruc.gcon.GymData) {
                    if (g.PlayerLeaders.contains(((EntityPlayerMP) e.getTargetEntity()).getUniqueID())) {
                        if (Utils.getGym(g.Name).OnlineLeaders.isEmpty()) {
                            Utils.getGym(g.Name).Status = OPEN;
                        }
                        Utils.getGym(g.Name).OnlineLeaders.add(e.getTargetEntity().getUniqueId());
                    }
                }
                Utils.sendToAll(Utils.toText(AGPConfig.Announcements.leaderJoinMessage.replace("{leader}", e.getTargetEntity().getName()), false));
            }
        }
    }

    //TODO: ???

    @Listener
    public void onPlayerQuit(ClientConnectionEvent.Disconnect e) {
        if (Utils.isAnyLeader(e.getTargetEntity())) {
            if (AGPConfig.Announcements.announceLeaderQuit) {
                Utils.sendToAll(Utils.toText(AGPConfig.Announcements.leaderQuitMessage.replace("{leader}", e.getTargetEntity().getName()), false));

            }

            HashMap<String, Integer> gyms = Maps.newHashMap();
            for (GymStruc gs : DataStruc.gcon.GymData) {
                if (gs.OnlineLeaders.contains(e.getTargetEntity().getUniqueId())) {
                    gs.OnlineLeaders.remove(e.getTargetEntity().getUniqueId());
                    if (gs.Status == OPEN) {
                        if (gs.OnlineLeaders.isEmpty()) {
                            if (gs.PlayerLeaders.contains("NPC")) {
                                if (AGPConfig.General.offlineNPC) {
                                    gs.Status = NPC;

                                    gyms.put(gs.Name, 2);
                                } else {
                                    gs.Status = CLOSED;
                                    gyms.put(gs.Name, 1);
                                }
                            } else {
                                gs.Status = CLOSED;
                                gyms.put(gs.Name, 1);
                            }
                            gs.Queue.clear();
                        }
                    }
                }
            }

            if (gyms.size() > 0) {
                String msg = "&7The &b";
                String msg2 = "&7The &b";
                List<String> closed = Lists.newArrayList();
                List<String> npc = Lists.newArrayList();

                for (String gym : gyms.keySet()) {
                    if (gyms.get(gym) == 1) {
                        closed.add(gym);
                    } else {
                        npc.add(gym);
                    }
                }
                if (closed.size() != 0) {
                    if (closed.size() == 1) {
                        msg += closed.get(0) + " &7Gym has closed!";
                        Utils.sendToAll(msg, true);
                    } else if (closed.size() >= 3) {
                        Utils.sendToAll("&7Multiple Gyms have just closed! Use &b/GymList &7to see what Gyms are open!", true);
                    } else {
                        for (int i = 0; i < closed.size(); i++) {
                            if (i == closed.size() - 1) {
                                msg += "&7and &b" + closed.get(i) + "&7 ";
                            } else
                                msg += closed.get(i) + "&7, &b";
                        }
                        msg += "Gyms have closed!";
                        Utils.sendToAll(msg, true);
                    }
                }

                if (npc.size() != 0) {
                    if (npc.size() == 1) {
                        msg2 += npc.get(0) + " &7Gym is temporarily being run by NPCs!";
                        Utils.sendToAll(msg2, true);
                    } else if (npc.size() >= 3) {
                        Utils.sendToAll("&7Multiple gyms are temporarily being run by &bNPCs&7! Use &b/GymList &7to see what gyms are open!", true);
                    } else {
                        for (int i = 0; i < npc.size(); i++) {
                            if (i == npc.size() - 1) {
                                msg2 += "&7and &b" + npc.get(i) + "&7 ";
                            } else
                                msg2 += npc.get(i) + "&7, &b";
                        }
                        msg2 += "Gyms are temporarily being run by NPCs!";
                        Utils.sendToAll(msg2, true);
                    }
                }

            }
        }

    }

    @SubscribeEvent//TODO sponge
    public void onPlayerInteractWithKarp(EntityInteract e) {
        if (e.getTarget() instanceof EntityPixelmon) {
            if (((EntityPixelmon)e.getTarget()).baseStats.pokemon == EnumPokemon.Magikarp) {
                if (e.getEntityPlayer().isSneaking()) {
                    if (eventCounter == 3) {
                        eventCounter = 0;
                        if (e.getEntityPlayer().inventory.getCurrentItem().getUnlocalizedName().contains("fish")) {
                            JumpThread j = new JumpThread((EntityPixelmon) e.getTarget());
                            Thread thread = new Thread(j);
                            thread.start();
                            e.getEntityPlayer().sendMessage((ITextComponent) Utils.toText("&bMagikarp &7is appalled you would attempt to feed it &bFish&7. &bMagikarp &7is leaving...", true));
                        }
                    } else {
                        eventCounter++;
                    }
                }
            }
        }
    }
}

class JumpThread implements Runnable {
    EntityPixelmon p;
    float startingblock = 0;

    public JumpThread(EntityPixelmon pixelmon) {
        p = pixelmon;
        startingblock = (float) p.posY;
    }

    @Override
    public void run() {
        for (float i = startingblock; i < startingblock + 20; i += 0.5) {
            p.setPosition(p.posX, i, p.posZ);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
