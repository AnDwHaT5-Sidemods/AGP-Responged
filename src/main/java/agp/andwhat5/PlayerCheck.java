package agp.andwhat5;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;

import agp.andwhat5.config.AGPConfig;
import agp.andwhat5.config.structs.DataStruc;
import agp.andwhat5.config.structs.GymStruc;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class PlayerCheck
{
    //TODO: Redesign

	private static List<String> devs = Lists.newArrayList();
	private static List<String> scrubs = Lists.newArrayList();

	private final static String devLink = "https://pastebin.com/raw/SXepayjB";
	private final static String scrubLink = "https://pastebin.com/raw/gqXKbgad";

	static Timer registerSpecials(){
		cacheNames();
		Timer timer = new Timer();
		TimerTask asyncTask = new TimerTask()
		{
			@Override
			public void run()
			{
				cacheNames();
			}
		};

		timer.schedule(asyncTask, 0, 21_600_000);
		return timer;
	}

	private static void cacheNames(){
		devs.clear();
		scrubs.clear();

		StringBuilder devSB = new StringBuilder();
		StringBuilder scrubSB = new StringBuilder();
		Thread thread = new Thread(() ->
	    {
		    try
		    {
			    URLConnection connection = new URL(devLink).openConnection();
			    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			    String line;
			    while ((line = in.readLine()) != null)
			    {
				    devSB.append(line);
			    }
			    in.close();
				devs.addAll(Arrays.asList(devSB.toString().split(",")));


				connection = new URL(scrubLink).openConnection();
				in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				while ((line = in.readLine()) != null)
				{
					scrubSB.append(line);
				}
				in.close();
				scrubs.addAll(Arrays.asList(scrubSB.toString().split(",")));

			} catch (IOException ignored) {}
	    });
		thread.start();
	}

	private boolean isDeveloper(EntityPlayerMP player)
	{
		return devs.contains(player.getName());
	}

	private boolean isScrub(EntityPlayerMP player)
	{
		return scrubs.contains(player.getName());
	}

	@SubscribeEvent
	public void onPlayerJoin(PlayerLoggedInEvent e)
	{
		if (isDeveloper((EntityPlayerMP) e.player))
		{
			Utils.sendToAll(TextFormatting.AQUA + "\u2605AGP Dev\u2605 " + 
							TextFormatting.UNDERLINE + e.player.getName() + 
							TextFormatting.RESET + TextFormatting.AQUA + " has joined.", false);
		}
		if (isScrub((EntityPlayerMP) e.player))
		{
			Utils.sendToAll(TextFormatting.GREEN + "\u2605AGP Helper" +
							"\u2605 " + TextFormatting.UNDERLINE +
							e.player.getName() + TextFormatting.RESET +
							TextFormatting.GREEN + " has joined.", false);
		}
		if (AGPConfig.Announcements.announceLeaderJoin)
		{
			if (Utils.isAnyLeader((EntityPlayerMP) e.player))
			{
				for(GymStruc g : DataStruc.gcon.GymData)
				{
					if(g.Leaders.contains(e.player.getName()))
					{
						if(Utils.getGym(g.Name).OnlineLeaders.isEmpty())
						{
							Utils.getGym(g.Name).Status = 0;
						}
						Utils.getGym(g.Name).OnlineLeaders.add(e.player.getName());
					}
				}
					Utils.sendToAll(Utils.toText(AGPConfig.Announcements.leaderJoinMessage.replace("{leader}", e.player.getName()), false));
			}
		}
	}

	@SubscribeEvent
	public void onPlayerQuit(PlayerEvent.PlayerLoggedOutEvent e)
	{
		if (Utils.isAnyLeader((EntityPlayerMP) e.player))
		{
			if (AGPConfig.Announcements.announceLeaderQuit)
			{
					Utils.sendToAll(Utils.toText(AGPConfig.Announcements.leaderQuitMessage.replace("{leader}", e.player.getName()), false));

			}

			HashMap<String, Integer> gyms = Maps.newHashMap();
			for (GymStruc gs : DataStruc.gcon.GymData)
			{
				if(gs.OnlineLeaders.contains(e.player.getName()))
				{
					gs.OnlineLeaders.remove(e.player.getName());
					if (gs.Status == 0)
					{
						if(gs.OnlineLeaders.isEmpty())
						{
							if(gs.Leaders.contains("NPC"))
							{
								if(AGPConfig.General.offlineNPC)
								{
									gs.Status = 2;

									gyms.put(gs.Name, 2);
								}
								else
								{
									gs.Status = 1;
									gyms.put(gs.Name, 1);
								}
							}
							else
							{
								gs.Status = 1;
								gyms.put(gs.Name, 1);
							}
							gs.Queue.clear();
						}
					}
				}
			}

			if(gyms.size() > 0)
			{
				String msg = "&7The &b";
				String msg2 = "&7The &b";
				List<String> closed = Lists.newArrayList();
				List<String> npc = Lists.newArrayList();

				for(String gym : gyms.keySet())
				{
					if(gyms.get(gym) == 1){
						closed.add(gym);
					} else {
						npc.add(gym);
					}
				}
				if(closed.size() != 0)
				{
					if(closed.size() == 1)
					{
						msg+=closed.get(0)+" &7Gym has closed!";
						Utils.sendToAll(msg, true);
					}
					else
					if(closed.size() >= 3)
					{
						Utils.sendToAll("&7Multiple Gyms have just closed! Use &b/GymList &7to see what Gyms are open!", true);
					}
					else
					{
						for(int i = 0; i < closed.size(); i++)
						{
							if(i == closed.size()-1)
							{
								msg+="&7and &b"+closed.get(i)+"&7 ";
							}
							else
								msg+=closed.get(i)+"&7, &b";
						}
						msg+="Gyms have closed!";
						Utils.sendToAll(msg, true);
					}
				}
				
				if(npc.size() != 0)
				{
					if(npc.size() == 1)
					{
						msg2+=npc.get(0)+" &7Gym is temporarily being run by NPCs!";
						Utils.sendToAll(msg2, true);
					}
					else
					if(npc.size() >= 3)
					{
						Utils.sendToAll("&7Multiple gyms are temporarily being run by &bNPCs&7! Use &b/GymList &7to see what gyms are open!", true);
					}
					else
					{
						for(int i = 0; i < npc.size(); i++)
						{
							if(i == npc.size()-1)
							{
								msg2+="&7and &b"+npc.get(i)+"&7 ";
							}
							else
								msg2+=npc.get(i)+"&7, &b";
						}
						msg2+="Gyms are temporarily being run by NPCs!";
						Utils.sendToAll(msg2, true);
					}
				}

			}
		}
		
	}

	//TODO: ???
	
	//The best of eastereggs.
	int eventCounter = 0;
	@SubscribeEvent
	public void onPlayerInteractWithKarp(EntityInteract e)
	{
		if(e.getTarget() instanceof EntityPixelmon)
		{
			if(e.getTarget().getName().equals("Magikarp"))
			{
				if(e.getEntityPlayer().isSneaking())
				{
					if(eventCounter == 3)
					{
						eventCounter = 0;
						if(e.getEntityPlayer().inventory.getCurrentItem().getUnlocalizedName().contains("fish"))
						{
							JumpThread j = new JumpThread((EntityPixelmon)e.getTarget());
							Thread thread = new Thread(j);
							thread.start();
							e.getEntityPlayer().sendMessage(Utils.toText("&bMagikarp &7is appalled you would attempt to feed it &bFish&7. &bMagikarp &7is leaving...", true));
						}
					}
					else
						eventCounter++;
				}
			}
		}
	}
}
class JumpThread implements Runnable
{
	EntityPixelmon p;
	float startingblock = 0;
	public JumpThread(EntityPixelmon pixelmon)
	{
		p = pixelmon;
		startingblock = (float)p.posY;
	}
	@Override
	public void run() {
		for(float i = startingblock; i < startingblock + 20; i += 0.5)
		{
			p.setPosition(p.posX, i, p.posZ);
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
