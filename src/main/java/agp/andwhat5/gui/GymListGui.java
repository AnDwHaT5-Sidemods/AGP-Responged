package agp.andwhat5.gui;

import agp.andwhat5.AGP;
import agp.andwhat5.config.structs.DataStruc;
import agp.andwhat5.config.structs.GymStruc;
import com.mcsimonflash.sponge.teslalibs.inventory.Action;
import com.mcsimonflash.sponge.teslalibs.inventory.Element;
import com.mcsimonflash.sponge.teslalibs.inventory.Layout;
import com.mcsimonflash.sponge.teslalibs.inventory.View;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static agp.andwhat5.Utils.*;
import static org.spongepowered.api.data.type.DyeColors.*;

public class GymListGui {

    private static final Element redGlassElement = Element.of(ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DISPLAY_NAME, Text.EMPTY).add(Keys.DYE_COLOR, RED).build());
    private static final Element blackGlassElement = Element.of(ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DISPLAY_NAME, Text.EMPTY).add(Keys.DYE_COLOR, BLACK).build());
    private static final Element whiteGlassElement = Element.of(ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DISPLAY_NAME, Text.EMPTY).add(Keys.DYE_COLOR, WHITE).build());

    public static void openGymListGUI(Player player) {

        View view = View.builder()
                .archetype(InventoryArchetypes.DOUBLE_CHEST)
                .property(InventoryTitle.of(toText("&8Available Gyms", false)))
                .build(AGP.getInstance().container);
        view.open(player);

        constructGymListPage(player, view, 0);
    }

    private static void constructGymListPage(Player player, View view, int page) {

        int startOnLine = 1;
        int rowStart = 1;
        int itemsPerRow = 7;
        int itemRows = 4;
        int itemsPerPage = itemsPerRow * itemRows;

        List<GymStruc> gymData = DataStruc.gcon.GymData;

        //Sanity checks
        int maxPages = Math.max(0, (int) (Math.ceil((double) gymData.size() / (double) itemsPerPage) - 1));
        if (page < 0)
            page = 0;
        if (page >= maxPages)
            page = maxPages;

        //Clear out previous layout / items
        Layout newLayout = Layout.builder()
                .row(redGlassElement, 0)
                .row(redGlassElement, 1)
                .row(blackGlassElement, 2)
                .row(blackGlassElement, 3)
                .row(whiteGlassElement, 4)
                .row(whiteGlassElement, 5)
                .build();
        view.define(newLayout);

        int slotsDone = 0;
        for (int i = page * itemsPerPage; i < gymData.size(); i++) {
            if (slotsDone == itemsPerPage) {
                break;
            }

            int currentRow = slotsDone / itemsPerRow;
            int currentSlot = slotsDone % itemsPerRow;
            int slot = (startOnLine * 9) + (currentRow * 9) + rowStart + currentSlot;

            view.setElement(slot, getGymElement(player, gymData.get(i)));
            slotsDone++;
        }

        //Next / Prev
        int finalPage = page;
        ItemStack nextStack = ItemStack.of(Sponge.getRegistry().getType(ItemType.class, "pixelmon:trade_holder_right").get(), 1);
        nextStack.offer(Keys.DISPLAY_NAME, Text.of("Next"));
        Consumer<Action.Click> nextAction = click -> Task.builder().execute(task -> constructGymListPage(player, view, finalPage + 1)).submit(AGP.getInstance());

        ItemStack prevStack = ItemStack.of(Sponge.getRegistry().getType(ItemType.class, "pixelmon:trade_holder_left").get(), 1);
        prevStack.offer(Keys.DISPLAY_NAME, Text.of("Previous"));
        Consumer<Action.Click> prevAction = click -> Task.builder().execute(task -> constructGymListPage(player, view, finalPage - 1)).submit(AGP.getInstance());

        ItemStack backStack = ItemStack.of(Sponge.getRegistry().getType(ItemType.class, "pixelmon:trade_monitor").get(), 1);
        backStack.offer(Keys.DISPLAY_NAME, Text.EMPTY);

        Element prev = Element.of(prevStack, prevAction);
        Element next = Element.of(nextStack, nextAction);
        Element mehh = Element.of(backStack);

        view.setElement(48, prev);
        view.setElement(49, mehh);
        view.setElement(50, next);
    }

    private static Element getGymElement(Player player, GymStruc gym) {

        ItemStack itemStack = ItemStack.builder().itemType(Sponge.getRegistry().getType(ItemType.class, gym.Badge).orElse(ItemTypes.BAKED_POTATO)).build();
        itemStack.offer(Keys.DISPLAY_NAME, toText("&d\u2605 &b" + gym.Name + "&d \u2605", false));

        ArrayList<Text> lore = new ArrayList<>();
        lore.add(toText("&7Gym Status: &b" + (gym.Status.equals(GymStruc.EnumStatus.CLOSED) ? "&4Closed" : gym.Status.equals(GymStruc.EnumStatus.OPEN) ? "&2Open" : "&eNPC Mode"), false));
        lore.add(toText("&7Requires: &b" + (gym.Requirement.equals("null") ? "None" : gym.Requirement), false));
        lore.add(toText("&7Level Cap: &b" + (gym.LevelCap == 0 ? "None" : "" + gym.LevelCap), false));
        lore.add(toText("&7Leaders:", false));

        if (gym.NPCAmount > 0) {
            lore.add(toText("  &2NPC " + (gym.NPCAmount > 1 ? "(" + gym.NPCAmount + ")" : ""), false));
        }

        if (!gym.PlayerLeaders.isEmpty()) {
            for (int i = 0; i < gym.PlayerLeaders.size(); i++) {
                lore.add(toText("  " + (gym.OnlineLeaders.contains(gym.PlayerLeaders.get(i)) ? "&2" : "&4") + getNameFromUUID(gym.PlayerLeaders.get(i)), false));
            }
        }

        itemStack.offer(Keys.ITEM_LORE, lore);

        Consumer<Action.Click> clickConsumer = click -> Task.builder().execute(task -> {
            if (gym.Lobby != null) {
                setPosition(player, gym.Lobby, gym.worldUUID);
                player.sendMessage(toText("&7Teleported to the &b" + gym.Name + " &7Gym lobby!", true));
            }
        }).submit(AGP.getInstance());

        return Element.of(itemStack, clickConsumer);
    }

}
