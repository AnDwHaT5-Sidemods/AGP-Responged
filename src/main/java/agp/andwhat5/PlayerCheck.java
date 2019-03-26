package agp.andwhat5;

import agp.andwhat5.config.AGPConfig;
import agp.andwhat5.config.structs.DataStruc;
import agp.andwhat5.config.structs.GymStruc;
import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static agp.andwhat5.config.structs.GymStruc.EnumStatus.*;

public class PlayerCheck {
    //TODO: Redesign

    //private final static String devLink = "https://pastebin.com/raw/SXepayjB";
    //private final static String scrubLink = "https://pastebin.com/raw/gqXKbgad";
    private static final List<UUID> devs = Lists.newArrayList(
            UUID.fromString("e978a5b2-3ea7-4f10-acde-1c220967c338") /*AnDwHaT5*/,
            UUID.fromString("88333268-79b6-4537-8066-48d255a6a0f9") /*Sy1veon*/,
            UUID.fromString("07aa849d-43e5-4da1-b2f9-5d8ac69f4d1a") /*ClientHax*/,
            UUID.fromString("971ea45e-22ec-4a9a-81e8-5aa26dfd7bf9") /*JustinSamaKun*/);
    private static final List<UUID> scrubs = Lists.newArrayList(
            UUID.fromString("6d55917f-1f16-4044-80be-71c117971b97") /*HackoJacko*/,
            UUID.fromString("0b813a81-5814-4050-9a6d-a492e89417ba") /*XpanD*/,
            UUID.fromString("8f580f86-2ef2-455e-84ad-442b12219b5f") /*MarshallxD*/,
            UUID.fromString("940d87bf-5aff-4ac7-99ec-ca3986612c40") /*1997Stijn*/,
            UUID.fromString("b256860b-60a9-4903-b90d-7e59dac11529") /*Malakae*/,
            UUID.fromString("f7d833a0-75d6-4000-a635-baedce9dd832") /*CraftSteamG*/,
            UUID.fromString("c3a0c2eb-84a7-498d-b222-edb8e659fcb5") /*krypticism*/);

    //The best of eastereggs.
    private int eventCounter = 0;

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
        if (!isLeader)
            return;

        DataStruc.gcon.GymData.forEach(g -> {
            if (g.PlayerLeaders.contains(player.getUniqueId())) {
                g.OnlineLeaders.add(player.getUniqueId());
            }
        });
        if (AGPConfig.Announcements.announceLeaderJoin) {
            Utils.sendToAll(AGPConfig.Announcements.leaderJoinMessage.replace("{leader}", player.getName()), true);
        }

        if (AGPConfig.General.autoOpen) {
            List<String> gymNames = new ArrayList<>();
            for (GymStruc gym : DataStruc.gcon.GymData) {
                if (gym.PlayerLeaders.contains(player.getUniqueId()) && gym.Status != OPEN) {
                    gym.Status = OPEN;
                    gymNames.add(gym.Name);
                }
            }

            if (AGPConfig.Announcements.openAnnouncement) {
                if (!gymNames.isEmpty()) {
                    if (gymNames.size() == 1) {
                        Utils.sendToAll("&7The &b" + gymNames.get(0) + " &7gym has opened!", true);
                    } else if (gymNames.size() == 2) {
                        Utils.sendToAll("&7The &b" + gymNames.get(0) + " &7and &b" + gymNames.get(1) + " &7gyms have opened!", true);
                    } else {
                        Utils.sendToAll("&7Multiple gyms have opened! Use &b/GymList &7to see all open gyms.", true);
                    }
                }
            }
        }
    }

    @Listener
    public void onPlayerQuit(ClientConnectionEvent.Disconnect e) {
        if (!Utils.isAnyLeader(e.getTargetEntity()))
            return;

        Player player = e.getTargetEntity();
        if (AGPConfig.Announcements.announceLeaderQuit) {
            Utils.sendToAll(AGPConfig.Announcements.leaderQuitMessage.replace("{leader}", player.getName()), true);
        }

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
                            closedGyms.add(gs.Name);
                        }
                        gs.Queue.clear();
                    }
                }
            }
        }

        if (AGPConfig.Announcements.closeAnnouncement) {
            if (!closedGyms.isEmpty()) {
                if (closedGyms.size() == 1) {
                    Utils.sendToAll("&7The &b" + closedGyms.get(0) + " &7gym has closed.", true);
                } else if (closedGyms.size() == 2) {
                    Utils.sendToAll("&7The &b" + closedGyms.get(0) + " &7and &b" + closedGyms.get(1) + " &7gyms have closed.", true);
                } else {
                    Utils.sendToAll("&7Multiple gyms have closed. Use &b/GymList &7to see what gyms are currently open.", true);
                }
            }

            if (!npcGyms.isEmpty()) {
                if (npcGyms.size() == 1) {
                    Utils.sendToAll("&7The &b" + npcGyms.get(0) + " &7gym is now being run by NPCs.", true);
                } else if (npcGyms.size() == 2) {
                    Utils.sendToAll("&7The &b" + npcGyms.get(0) + " &7and &b" + npcGyms.get(1) + " &7gyms are now being run by NPCs.", true);
                } else {
                    Utils.sendToAll("&7Multiple gyms are being run by NPCs. Use &b/GymList &7to see what gyms are currently open.", true);
                }
            }
        }


    }

    @Listener
    public void onPlayerInteractWithKarp(InteractEntityEvent.Secondary.MainHand event, @Root Player player) {
        Entity targetEntity = event.getTargetEntity();

        Optional<ItemStack> itemInHand = player.getItemInHand(HandTypes.MAIN_HAND);
        if (!itemInHand.isPresent()) {
            return;
        }

        if (targetEntity instanceof EntityPixelmon) {
            EntityPixelmon pixelmon = (EntityPixelmon) targetEntity;
            if (pixelmon.getPokemonData().getBaseStats().pokemon == EnumSpecies.Magikarp) {
                if (player.get(Keys.IS_SNEAKING).get()) {
                    if (eventCounter == 3) {
                        eventCounter = 0;

                        if (itemInHand.get().getType().getName().toLowerCase().contains("fish")) {
                            Task.builder()
                                    .interval(1, TimeUnit.SECONDS)
                                    .execute(new JumpThread(pixelmon))
                                    .submit(AGP.getInstance());

                            player.sendMessage(Utils.toText("&bMagikarp &7is appalled you would attempt to feed it &bFish&7. &bMagikarp &7is leaving...", true));
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
    private final EntityPixelmon p;
    private final float startingblock;
    private float i;

    JumpThread(EntityPixelmon pixelmon) {
        p = pixelmon;
        startingblock = (float) p.posY;
        i = startingblock;
    }

    @Override
    public void run() {
        if (i < startingblock + 20) {
            i += 0.5;
            p.setPosition(p.posX, i, p.posZ);

            ParticleEffect effect = ParticleEffect.builder()
                    .type(ParticleTypes.WATER_SPLASH)
                    .quantity(50)
                    .build();
            ((Entity) p).getWorld().spawnParticles(effect, ((Entity) p).getLocation().getPosition().add(0, -0.5, 0));

        }
    }
}
