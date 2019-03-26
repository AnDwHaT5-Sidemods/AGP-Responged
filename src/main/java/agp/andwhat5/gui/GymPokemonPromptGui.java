package agp.andwhat5.gui;

import agp.andwhat5.AGP;
import agp.andwhat5.Utils;
import agp.andwhat5.battles.BattleUtil;
import agp.andwhat5.config.structs.GymStruc;
import agp.andwhat5.config.structs.ShowdownStruc;
import com.mcsimonflash.sponge.teslalibs.inventory.Action;
import com.mcsimonflash.sponge.teslalibs.inventory.Element;
import com.mcsimonflash.sponge.teslalibs.inventory.Layout;
import com.mcsimonflash.sponge.teslalibs.inventory.View;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.exceptions.ShowdownImportException;
import com.pixelmonmod.pixelmon.api.pokemon.ImportExportConverter;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.minecraft.entity.player.EntityPlayerMP;
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

import java.util.function.Consumer;

import static agp.andwhat5.Utils.toText;
import static org.spongepowered.api.data.type.DyeColors.*;

@SuppressWarnings("WeakerAccess")
public class GymPokemonPromptGui {

    private static final Element redGlassElement = Element.of(ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DISPLAY_NAME, Text.EMPTY).add(Keys.DYE_COLOR, RED).build());
    private static final Element blackGlassElement = Element.of(ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DISPLAY_NAME, Text.EMPTY).add(Keys.DYE_COLOR, BLACK).build());
    private static final Element whiteGlassElement = Element.of(ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DISPLAY_NAME, Text.EMPTY).add(Keys.DYE_COLOR, WHITE).build());


    public static void openPromptGui(Player player, GymStruc gym, ShowdownStruc struc) {
        View view = View.builder()
                .archetype(InventoryArchetypes.CHEST)
                .property(InventoryTitle.of(toText("&8Gym Pokemon - Action", false)))
                .build(AGP.getInstance().container);

        view.open(player);

        constructopenPromptPage(player, view, gym, struc);
    }

    private static void constructopenPromptPage(Player player, View view, GymStruc gym, ShowdownStruc struc) {
        //Clear out previous layout / items
        Layout newLayout = Layout.builder()
                .row(redGlassElement, 0)
                .row(blackGlassElement, 1)
                .row(whiteGlassElement, 2)
                .build();
        view.define(newLayout);

        ItemStack cancelStack = ItemStack.builder().itemType(ItemTypes.DYE).add(Keys.DYE_COLOR, RED).build();
        cancelStack.offer(Keys.DISPLAY_NAME, Text.of("Cancel"));
        Consumer<Action.Click> cancelAction = click -> Task.builder().execute(task -> GymPokemonGui.openGymPokemonGui(player, gym)).submit(AGP.getInstance());

        ItemStack givePokemonStack = ItemStack.of(Sponge.getRegistry().getType(ItemType.class, "pixelmon:poke_ball").get(), 1);
        givePokemonStack.offer(Keys.DISPLAY_NAME, Text.of("Give Pixelmon"));
        Consumer<Action.Click> giveAction = click ->
                Task.builder().execute(task ->
                {
                    PlayerPartyStorage storage = Pixelmon.storageManager.getParty((EntityPlayerMP) player);
                    Pokemon data = null;
                    try {
                        data = ImportExportConverter.importText(struc.showdownCode);
                    } catch (ShowdownImportException e) {
                        e.printStackTrace();
                    }
                    storage.add(BattleUtil.pixelmonDataToTempBattlePokemon(player, data).get().getPokemonData());
                    player.sendMessage(Utils.toText("&7Successfully added &b" + data.getDisplayName() + " &7to your party!", true));
                    GymPokemonGui.openGymPokemonGui(player, gym);
                }).submit(AGP.getInstance());

        ItemStack deleteStack = ItemStack.of(Sponge.getRegistry().getType(ItemType.class, "pixelmon:trash_can").get(), 1);
        deleteStack.offer(Keys.DISPLAY_NAME, Text.of("Delete Pixelmon"));
        Consumer<Action.Click> deleteAction = click ->
                Task.builder().execute(task ->
                {
                    if (gym.Pokemon.contains(struc)) {
                        gym.Pokemon.remove(struc);
                        Utils.saveAGPData();
                        player.sendMessage(Utils.toText("&7Successfully removed that Pokemon from the gym.", true));
                        GymPokemonGui.openGymPokemonGui(player, gym);
                    } else {
                        player.sendMessage(Utils.toText("&7Error removing that Pokemon from the gym.", true));
                        GymPokemonGui.openGymPokemonGui(player, gym);
                    }
                }).submit(AGP.getInstance());

        Element cancel = Element.of(cancelStack, cancelAction);
        Element give = Element.of(givePokemonStack, giveAction);
        Element delete = Element.of(deleteStack, deleteAction);

        view.setElement(10, cancel);
        view.setElement(13, give);
        view.setElement(16, delete);
    }

}
