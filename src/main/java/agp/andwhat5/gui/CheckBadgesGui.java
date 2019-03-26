package agp.andwhat5.gui;

import agp.andwhat5.AGP;
import agp.andwhat5.config.structs.BadgeStruc;
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

import static agp.andwhat5.Utils.getPlayerData;
import static agp.andwhat5.Utils.toText;
import static org.spongepowered.api.data.type.DyeColors.*;

@SuppressWarnings("Duplicates")
public class CheckBadgesGui {

    private static final Element redGlassElement = Element.of(ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DISPLAY_NAME, Text.EMPTY).add(Keys.DYE_COLOR, RED).build());
    private static final Element blackGlassElement = Element.of(ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DISPLAY_NAME, Text.EMPTY).add(Keys.DYE_COLOR, BLACK).build());
    private static final Element whiteGlassElement = Element.of(ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DISPLAY_NAME, Text.EMPTY).add(Keys.DYE_COLOR, WHITE).build());

    public static void openCheckBadgesGUI(Player player) {
        openCheckBadgesGUIOther(player, player);
    }


    public static void openCheckBadgesGUIOther(Player viewer, Player target) {
        View view = View.builder()
                .archetype(InventoryArchetypes.DOUBLE_CHEST)
                .property(InventoryTitle.of(toText("&8" + target.getName() + "'s Badges", false)))
                .build(AGP.getInstance().container);

        view.open(viewer);

        constructCheckBadgesPage(target, view, 0);
    }

    private static void constructCheckBadgesPage(Player player, View view, int page) {

        int startOnLine = 1;
        int rowStart = 1;
        int itemsPerRow = 7;
        int itemRows = 4;
        int itemsPerPage = itemsPerRow * itemRows;//28

        List<BadgeStruc> badgeData = getPlayerData(player).Badges;//56

        //Sanity checks
        int maxPages = Math.max(0, (int) (Math.ceil((double) badgeData.size() / (double) itemsPerPage) - 1));
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
        for (int i = page * itemsPerPage; i < badgeData.size(); i++) {
            if (slotsDone == itemsPerPage) {
                break;
            }

            int currentRow = slotsDone / itemsPerRow;
            int currentSlot = slotsDone % itemsPerRow;
            int slot = (startOnLine * 9) + (currentRow * 9) + rowStart + currentSlot;

            view.setElement(slot, getBadgeElement(player, badgeData.get(i)));
            slotsDone++;
        }

        //Next / Prev
        int finalPage = page;
        ItemStack nextStack = ItemStack.of(Sponge.getRegistry().getType(ItemType.class, "pixelmon:trade_holder_right").get(), 1);
        nextStack.offer(Keys.DISPLAY_NAME, Text.of("Next"));
        Consumer<Action.Click> nextAction = click -> Task.builder().execute(task -> constructCheckBadgesPage(player, view, finalPage + 1)).submit(AGP.getInstance());

        ItemStack prevStack = ItemStack.of(Sponge.getRegistry().getType(ItemType.class, "pixelmon:trade_holder_left").get(), 1);
        prevStack.offer(Keys.DISPLAY_NAME, Text.of("Previous"));
        Consumer<Action.Click> prevAction = click -> Task.builder().execute(task -> constructCheckBadgesPage(player, view, finalPage - 1)).submit(AGP.getInstance());

        ItemStack backStack = ItemStack.of(Sponge.getRegistry().getType(ItemType.class, "pixelmon:trade_monitor").get(), 1);
        backStack.offer(Keys.DISPLAY_NAME, Text.EMPTY);

        Element prev = Element.of(prevStack, prevAction);
        Element next = Element.of(nextStack, nextAction);
        Element mehh = Element.of(backStack);

        view.setElement(48, prev);
        view.setElement(49, mehh);
        view.setElement(50, next);
    }

    @SuppressWarnings("unused")
    private static Element getBadgeElement(Player player, BadgeStruc badge) {

        ItemStack itemStack = ItemStack.builder().itemType(Sponge.getRegistry().getType(ItemType.class, badge.Badge).orElse(ItemTypes.BAKED_POTATO)).build();
        itemStack.offer(Keys.DISPLAY_NAME, toText("&d\u2605 &b" + badge.Gym + "&d \u2605", false));

        ArrayList<Text> lore = new ArrayList<>();
        lore.add(toText("&7Leader: &b" + badge.Leader, false));
        lore.add(toText("&7Date Obtained: &b" + badge.Obtained, false));
        lore.add(toText("&7Pokemon:", false));

        if (badge.Pokemon.isEmpty()) {
            lore.add(toText("  &4" + "Unknown", false));
        } else {
            for (int i = 0; i < badge.Pokemon.size(); i++) {
                lore.add(toText("  &b" + badge.Pokemon.get(i), false));
            }
        }

        itemStack.offer(Keys.ITEM_LORE, lore);

        return Element.of(itemStack);
    }

}
