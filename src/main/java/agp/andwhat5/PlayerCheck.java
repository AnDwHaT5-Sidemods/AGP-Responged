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

    //private final static String devLink = "https://pastebin.com/raw/SXepayjB";
    //private final static String scrubLink = "https://pastebin.com/raw/gqXKbgad";
    private static List<UUID> devs = Lists.newArrayList(
    		UUID.fromString("e978a5b2-3ea7-4f10-acde-1c220967c338") /*AnDwHaT5*/,
    		UUID.fromString("88333268-79b6-4537-8066-48d255a6a0f9") /*Sy1veon*/,
    		UUID.fromString("07aa849d-43e5-4da1-b2f9-5d8ac69f4d1a") /*ClientHax*/);
    private static List<UUID> scrubs = Lists.newArrayList(
    		UUID.fromString("0eb8e4fa-f8dc-4648-989b-98ac5bd417a3") /*HackoJacko*/);
    
    //The best of eastereggs.
    int eventCounter = 0;

    /*static Timer registerSpecials() {
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
    }*/

    private boolean isDeveloper(Player player) {
        return devs.contains(player.getUniqueId());
    }

    private boolean isScrub(Player player) {
        return scrubs.contains(player.getUniqueId());
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join e) {
    	Player player = e.getTargetEntity();
        if (isDeveloper(player)) {
            Utils.sendToAll(TextFormatting.AQUA + "\u2605AGP-R Dev\u2605 " +
                    TextFormatting.UNDERLINE + player.getName() +
                    TextFormatting.RESET + TextFormatting.AQUA + " has joined.", false);
        }
        if (isScrub(player)) {
            Utils.sendToAll(TextFormatting.GREEN + "\u2605AGP-R Helper" +
                    "\u2605 " + TextFormatting.UNDERLINE +
                    player.getName() + TextFormatting.RESET +
                    TextFormatting.GREEN + " has joined.", false);
        }
        boolean isLeader = Utils.isAnyLeader(player);
        if(!isLeader)
        	return;
        DataStruc.gcon.GymData.stream().forEach(g -> {if(g.PlayerLeaders.contains(player.getUniqueId())) g.OnlineLeaders.add(player.getUniqueId());});       
        if (AGPConfig.Announcements.announceLeaderJoin) 
        {
        	for (GymStruc g : DataStruc.gcon.GymData) 
        	{
        		if (g.PlayerLeaders.contains(player.getUniqueId())) 
        		{
                    Utils.getGym(g.Name).OnlineLeaders.add(player.getUniqueId());
        		}
        	}
        	Utils.sendToAll(AGPConfig.Announcements.leaderJoinMessage.replace("{leader}", player.getName()), true);
        }
        
        if(AGPConfig.General.autoOpen)
        {
        	List<String> gymNames = new ArrayList<>();
        	for(GymStruc gym : DataStruc.gcon.GymData)
        	{
        		if(gym.PlayerLeaders.contains(player.getUniqueId()) && gym.OnlineLeaders.isEmpty())
        		{
        			gym.Status = OPEN;
        			gymNames.add(gym.Name);
        		}
        	}
        	
        	if(AGPConfig.Announcements.openAnnouncement)
        	{
        		if(!gymNames.isEmpty())
        		{
        			if(gymNames.size() == 1)
        			{
        				Utils.sendToAll("The " + gymNames.get(0) + " gym has opened!", true);
        			}
        			else
        			if(gymNames.size() == 2)
        			{
        				Utils.sendToAll("The &b" + gymNames.get(0) + " &7and &b" + gymNames.get(1) + " &7gyms have opened!", true);
        			}
        			else
        			{
        				Utils.sendToAll("Multiple gyms have opened! Use &b/GymList &7to see all open gyms.", true);
        			}
        		}
        	}
        }
    }
    
    @Listener
    public void onPlayerQuit(ClientConnectionEvent.Disconnect e) {
    	if(!Utils.isAnyLeader(e.getTargetEntity()))
    		return;
    	
    	Player player = e.getTargetEntity();
        if (AGPConfig.Announcements.announceLeaderQuit) {
            Utils.sendToAll(AGPConfig.Announcements.leaderQuitMessage.replace("{leader}", player.getName()), true);

            List<String> closedGyms = new ArrayList<>();
            List<String> npcGyms = new ArrayList<>();
            
            for (GymStruc gs : DataStruc.gcon.GymData) {
                if (gs.OnlineLeaders.contains(player.getUniqueId())) {
                    gs.OnlineLeaders.remove(player.getUniqueId());
                    if (gs.Status == OPEN) {
                        if (gs.OnlineLeaders.isEmpty()) {
                            if (gs.NPCAmount > 0) {
                                if (AGPConfig.General.offlineNPC) {
                                    gs.Status = NPC;
                                    npcGyms.add(gs.Name);
                                } else {
                                    gs.Status = CLOSED;
                                    closedGyms.add(gs.Name);
                                }
                            } else {
                                gs.Status = CLOSED;
                                npcGyms.add(gs.Name);
                            }
                            gs.Queue.clear();
                        }
                    }
                }
            }

            if(AGPConfig.Announcements.closeAnnouncement)
            {
            	if(!closedGyms.isEmpty())
            	{
            		if(closedGyms.size() == 1)
            		{
            			Utils.sendToAll("&7The &b" + closedGyms.get(0) + " &7gym has closed.", true);
            		}
            		else
            		if(closedGyms.size() == 2)
            		{
            			Utils.sendToAll("&7The &b" + closedGyms.get(0) + " &7and &b" + closedGyms.get(1) + " &7gyms have closed.", true);
            		}
            		else
            		{
            			Utils.sendToAll("&7Multiple gyms have closed. Use &b/GymList &7to see what gyms are currently open.", true);
            		}
            	}
            	
            	if(!npcGyms.isEmpty())
            	{
            		if(npcGyms.size() == 1)
            		{
            			Utils.sendToAll("&7The &b" + closedGyms.get(0) + " &7gym is now being run by NPCs.", true);
            		}
            		else
            		if(npcGyms.size() == 2)
            		{
            			Utils.sendToAll("&7The &b" + closedGyms.get(0) + " &7and &b" + closedGyms.get(1) + " &7gyms are now being run by NPCs.", true);
            		}
            		else
            		{
            			Utils.sendToAll("&7Multiple gyms are being run by NPCs. Use &b/GymList &7to see what gyms are currently open.", true);
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
                	((EntityPixelmon)e.getTarget()).setVelocity(0, 20, 0);
                    /*if (eventCounter == 3) {
                        eventCounter = 0;
                        if (e.getEntityPlayer().inventory.getCurrentItem().getUnlocalizedName().contains("fish")) {
                            JumpThread j = new JumpThread((EntityPixelmon) e.getTarget());
                            Thread thread = new Thread(j);
                            thread.start();
                            e.getEntityPlayer().sendMessage((ITextComponent) Utils.toText("&bMagikarp &7is appalled you would attempt to feed it &bFish&7. &bMagikarp &7is leaving...", true));
                        }
                    } else {
                        eventCounter++;
                    }*/
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
