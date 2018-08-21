package agp.andwhat5.ui.display.gymlist;

import agp.andwhat5.config.structs.GymStruc;
import agp.andwhat5.ui.AbstractContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static agp.andwhat5.config.structs.GymStruc.EnumStatus.*;

public class GymListContainer extends AbstractContainer {

    public GymListContainer(IInventory chestInventory, EntityPlayer player) {
        super(chestInventory, player);
    }

    @Override
    public <E extends List> void fillContents(int rows, E gyms) {
        for (int i = 0; i < gyms.size() && i < 36; i++) {
            GymStruc gym = (GymStruc) gyms.get(i);

            Item item = Item.getByNameOrId(gym.Badge);
            item = item == null ? Item.getItemFromBlock(Blocks.BARRIER) : item;
            ItemStack stack = new ItemStack(item);
            if (!stack.hasTagCompound()) {
                stack.setTagCompound(new NBTTagCompound());
            }
            if (!stack.getTagCompound().hasKey("display")) {
                stack.getTagCompound().setTag("display", new NBTTagCompound());
            }
            NBTTagCompound display = stack.getTagCompound().getCompoundTag("display");
            stack.setStackDisplayName(TextFormatting.YELLOW + "\u2605 " + TextFormatting.GREEN + gym.Name +
                    TextFormatting.YELLOW + " \u2605");

            // Append lore
            NBTTagList lore = new NBTTagList();
            lore.appendTag(new NBTTagString(TextFormatting.GRAY + "Status: " + (
                    gym.Status == OPEN ? TextFormatting.GREEN + "Open" :
                            gym.Status == CLOSED ? TextFormatting.RED + "Closed" :
                                    TextFormatting.GREEN + "Open " + TextFormatting.GRAY + "(" + TextFormatting.YELLOW
                                            + "NPC Mode" + TextFormatting.GRAY + ")"
            )));
            lore.appendTag(new NBTTagString(TextFormatting.GRAY + "Level Cap: " + TextFormatting.YELLOW + (gym
                    .LevelCap != 0 ? gym.LevelCap : "No limit")));
            if (gym.Requirement.equals("") || gym.Requirement.equalsIgnoreCase("null")) //The "" is for legacy reasons.
                lore.appendTag(new NBTTagString(TextFormatting.GRAY + "Requires: " + TextFormatting.YELLOW + "None"));
            else
                lore.appendTag(new NBTTagString(TextFormatting.GRAY + "Requires: " + TextFormatting.YELLOW + gym.Requirement));
            lore.appendTag(new NBTTagString(TextFormatting.GRAY + "PlayerLeaders:"));

            PlayerList p = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();
            int npcs = 0;
            List<UUID> onlineLeaders = new ArrayList<>();
            List<UUID> offlineLeaders = new ArrayList<>();
            for (UUID leader : gym.PlayerLeaders) {
                //TODO Sponge
                //if (leader.equalsIgnoreCase("NPC")) {
                //    npcs++;
                //} else {
                //TODO
                boolean online = Arrays.stream(p.getOnlinePlayerNames()).anyMatch(pl -> pl.equals(leader));
                if (online) {
                    onlineLeaders.add(leader);
                } else {
                    offlineLeaders.add(leader);
                }
                //}
            }

            for (UUID leader : onlineLeaders) {
                lore.appendTag(new NBTTagString("  " + TextFormatting.YELLOW + leader + TextFormatting.GRAY +
                        " (" +
                        TextFormatting.GREEN + "Online" + TextFormatting.GRAY
                        + ")"));
            }

            for (UUID leader : offlineLeaders) {
                lore.appendTag(new NBTTagString("  " + TextFormatting.YELLOW + leader + TextFormatting.GRAY +
                        " (" +
                        TextFormatting.RED + "Offline" + TextFormatting.GRAY
                        + ")"));
            }

            if (npcs != 0)
                lore.appendTag(new NBTTagString("  " + TextFormatting.YELLOW + "NPC x" + npcs));

            display.setTag("Lore", lore);
            putStackInSlot(i, stack);

        }

        if (rows == 6) {
            Item item = Item.getItemFromBlock(Blocks.STAINED_GLASS_PANE);
            ItemStack stack = new ItemStack(item);
            if (!stack.hasTagCompound()) {
                stack.setTagCompound(new NBTTagCompound());
            }
            if (!stack.getTagCompound().hasKey("display")) {
                stack.getTagCompound().setTag("display", new NBTTagCompound());
            }
            stack.setStackDisplayName(TextFormatting.YELLOW + "");
            for (int i = 36; i < 45; i++) {
                putStackInSlot(i, stack);
            }
        }
    }
}
