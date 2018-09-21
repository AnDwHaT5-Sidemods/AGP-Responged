package agp.andwhat5.gui;

import agp.andwhat5.AGP;
import agp.andwhat5.Utils;
import agp.andwhat5.config.structs.DataStruc;
import agp.andwhat5.config.structs.GymStruc;
import agp.andwhat5.config.structs.ShowdownStruc;
import com.mcsimonflash.sponge.teslalibs.inventory.Action;
import com.mcsimonflash.sponge.teslalibs.inventory.Element;
import com.mcsimonflash.sponge.teslalibs.inventory.Layout;
import com.mcsimonflash.sponge.teslalibs.inventory.View;
import com.pixelmonmod.pixelmon.client.gui.pokemoneditor.ImportExportConverter;
import com.pixelmonmod.pixelmon.comm.PixelmonData;
import com.pixelmonmod.pixelmon.comm.PixelmonMovesetData;
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

import static agp.andwhat5.Utils.getNameFromUUID;
import static agp.andwhat5.Utils.setPosition;
import static agp.andwhat5.Utils.toText;
import static org.spongepowered.api.data.type.DyeColors.BLACK;
import static org.spongepowered.api.data.type.DyeColors.RED;
import static org.spongepowered.api.data.type.DyeColors.WHITE;

public class GymPokemonGui {

    private static final Element redGlassElement = Element.of(ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DISPLAY_NAME, Text.EMPTY).add(Keys.DYE_COLOR, RED).build());
    private static final Element blackGlassElement = Element.of(ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DISPLAY_NAME, Text.EMPTY).add(Keys.DYE_COLOR, BLACK).build());
    private static final Element whiteGlassElement = Element.of(ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DISPLAY_NAME, Text.EMPTY).add(Keys.DYE_COLOR, WHITE).build());

    public static void openGymPokemonGui(Player player, GymStruc gym) {

        View view = View.builder()
                .archetype(InventoryArchetypes.DOUBLE_CHEST)
                .property(InventoryTitle.of(toText("&8Gym Pokemon", false)))
                .build(AGP.getInstance().container);
        view.open(player);

        constructGymPokemonPage(player, view, gym, 0);
    }

    private static void constructGymPokemonPage(Player player, View view, GymStruc gym, int page)
    {

        int startOnLine = 1;
        int rowStart = 1;
        int itemsPerRow = 7;
        int itemRows = 4;
        int itemsPerPage = itemsPerRow * itemRows;

        List<ShowdownStruc> gymData = gym.Pokemon;

        //Sanity checks
        int maxPages = Math.max(0, (int) (Math.ceil((double)gymData.size() / (double)itemsPerPage) - 1));
        if(page < 0)
            page = 0;
        if(page >= maxPages)
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
        if(!gymData.isEmpty()) {
            for (int i = page * itemsPerPage; i < gymData.size(); i++) {
                if (slotsDone == itemsPerPage) {
                    break;
                }

                int currentRow = slotsDone / itemsPerRow;
                int currentSlot = slotsDone % itemsPerRow;
                int slot = (startOnLine * 9) + (currentRow * 9) + rowStart + currentSlot;
                PixelmonData data = new PixelmonData();
                ImportExportConverter.importText(gymData.get(i).showdownCode, data);
                view.setElement(slot, getPokemonElement(player, gym, data, gymData.get(i)));
                slotsDone++;
            }
        }

        //Next / Prev
        int finalPage = page;
        ItemStack nextStack = ItemStack.of(Sponge.getRegistry().getType(ItemType.class, "pixelmon:trade_holder_right").get(), 1);
        nextStack.offer(Keys.DISPLAY_NAME, Text.of("Next"));
        Consumer<Action.Click> nextAction = click -> Task.builder().execute(task -> constructGymPokemonPage(player, view, gym, finalPage + 1)).submit(AGP.getInstance());

        ItemStack prevStack = ItemStack.of(Sponge.getRegistry().getType(ItemType.class, "pixelmon:trade_holder_left").get(), 1);
        prevStack.offer(Keys.DISPLAY_NAME, Text.of("Previous"));
        Consumer<Action.Click> prevAction = click -> Task.builder().execute(task -> constructGymPokemonPage(player, view, gym, finalPage - 1)).submit(AGP.getInstance());

        ItemStack backStack = ItemStack.of(Sponge.getRegistry().getType(ItemType.class, "pixelmon:trade_monitor").get(), 1);
        backStack.offer(Keys.DISPLAY_NAME, Text.EMPTY);

        Element prev = Element.of(prevStack, prevAction);
        Element next = Element.of(nextStack, nextAction);
        Element mehh = Element.of(backStack);

        view.setElement(48, prev);
        view.setElement(49, mehh);
        view.setElement(50, next);
    }

    private static Element getPokemonElement(Player player, GymStruc gym, PixelmonData pokemon, ShowdownStruc struc) {

        ItemStack itemStack = Utils.getPixelmonSprite(pokemon);
        itemStack.offer(Keys.DISPLAY_NAME, toText("&d\u2605 &b" + pokemon.name + (!pokemon.nickname.isEmpty()?"("+pokemon.nickname+")":"") + "&d \u2605", false));

        ArrayList<Text> lore = new ArrayList<>();
        lore.add(toText("&7Nature: &b" + pokemon.nature, false));
        lore.add(toText("&7Ability: &b" + pokemon.ability, false));
        lore.add(toText("&7Friendship: &b" + pokemon.friendship, false));
        float ivHP = pokemon.ivs[0];
        float ivAtk = pokemon.ivs[1];
        float ivDef = pokemon.ivs[2];
        float ivSpeed = pokemon.ivs[5];
        float ivSAtk = pokemon.ivs[3];
        float ivSDef = pokemon.ivs[4];
        int percentage = Math.round(((ivHP + ivDef + ivAtk + ivSpeed + ivSAtk + ivSDef) / 186f) * 100);
        lore.add(toText("&7IVs " + "(&b"+percentage+"%&7):", false));
        lore.add((toText("    &7HP: &b" + (int)ivHP + " &d| &7Atk: &b" + (int)ivAtk + " &d| &7Def: &b" + (int)ivDef, false)));
        lore.add((toText("    &7SAtk: &b" + (int)ivSAtk + " &d| &7SDef: &b" + ivSDef + " &d| &7Spd: &b" + (int)ivSpeed, false)));
        float evHP = pokemon.evs[0];
        float evAtk = pokemon.evs[1];
        float evDef = pokemon.evs[2];
        float evSpeed = pokemon.evs[5];
        float evSAtk = pokemon.evs[3];
        float evSDef = pokemon.evs[4];
        lore.add(toText("&7EVs:", false));
        lore.add((toText("    &7HP: &b" + (int)evHP + " &d| &7Atk: &b" + (int)evAtk + " &d| &7Def: &b" + (int)evDef, false)));
        lore.add((toText( "    &7SAtk: &b" + (int)evSAtk + " &d| &7SDef: &b" + (int)evSDef + " &d| &7Spd: &b" + (int)evSpeed, false)));
        lore.add(toText("&7Moves:", false));
        if(pokemon.moveset != null)
        {
            for(PixelmonMovesetData da : pokemon.moveset)
            {
                if(da != null)
                {
                    lore.add(toText("    &b" + da.getAttack().baseAttack.getUnLocalizedName(), false));
                }
            }
        }
        itemStack.offer(Keys.ITEM_LORE, lore);


        Consumer<Action.Click> clickConsumer = click -> Task.builder().execute(task -> {
            if(player.hasPermission("agp.headleader") || player.hasPermission("agp.gympokemon.admin")) {
                GymPokemonPromptGui.openPromptGui(player, gym, struc);
            }
        }).submit(AGP.getInstance());

        return Element.of(itemStack, clickConsumer);
    }

}
