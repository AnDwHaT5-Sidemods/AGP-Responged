package agp.andwhat5.gui;

import agp.andwhat5.AGP;
import agp.andwhat5.Utils;
import agp.andwhat5.battles.BattleUtil;
import agp.andwhat5.config.structs.*;
import com.mcsimonflash.sponge.teslalibs.inventory.Action;
import com.mcsimonflash.sponge.teslalibs.inventory.Element;
import com.mcsimonflash.sponge.teslalibs.inventory.Layout;
import com.mcsimonflash.sponge.teslalibs.inventory.View;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.exceptions.ShowdownImportException;
import com.pixelmonmod.pixelmon.api.pokemon.ImportExportConverter;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static agp.andwhat5.Utils.toText;
import static org.spongepowered.api.data.type.DyeColors.*;

public class ChooseTeamGui {

    private final Element redGlassElement = Element.of(ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DISPLAY_NAME, Text.EMPTY).add(Keys.DYE_COLOR, RED).build());
    private final Element blackGlassElement = Element.of(ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DISPLAY_NAME, Text.EMPTY).add(Keys.DYE_COLOR, BLACK).build());
    private final Element whiteGlassElement = Element.of(ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DISPLAY_NAME, Text.EMPTY).add(Keys.DYE_COLOR, WHITE).build());

    private List<ShowdownStruc> selectedPokemon = new ArrayList<>();

    private Player leader;
    private UUID challengerUUID;
    private GymStruc gym;
    @Nullable
    private ArenaStruc arena;

    public void openChooseTeamGui(Player gymLeader, UUID challengerUUID, GymStruc pokeGym, @Nullable ArenaStruc arena) {

        View view = View.builder()
                .archetype(InventoryArchetypes.DOUBLE_CHEST)
                .property(InventoryTitle.of(toText("&8Gym Pokemon", false)))
                .build(AGP.getInstance().container);
        view.open(gymLeader);
        this.leader = gymLeader;
        this.challengerUUID = challengerUUID;
        this.gym = pokeGym;
        this.arena = arena;
        constructChooseTeamPage(gymLeader, challengerUUID, view, pokeGym, arena, 0);
    }

    private void constructChooseTeamPage(Player leader, UUID challUUID, View view, GymStruc gym, @Nullable ArenaStruc arena, int page) {

        int startOnLine = 3;
        int rowStart = 1;
        int itemsPerRow = 7;
        int itemRows = 2;
        int itemsPerPage = itemsPerRow * itemRows;

        List<ShowdownStruc> gymData = gym.Pokemon;

        //Sanity checks
        int maxPages = Math.max(0, (int) (Math.ceil((double) gymData.size() / (double) itemsPerPage) - 1));
        if (page < 0)
            page = 0;
        if (page >= maxPages)
            page = maxPages;

        //Clear out previous layout / items
        Layout newLayout = Layout.builder()
                .row(redGlassElement, 0)
                .set(redGlassElement, 9, 13, 17)
                .row(blackGlassElement, 2)
                .row(blackGlassElement, 3)
                .row(whiteGlassElement, 4)
                .row(whiteGlassElement, 5)
                .build();
        view.define(newLayout);

        int slotsDone = 0;
        for (int i = 10; i <= 16; i++) {
            if (i != 13) {
                if (!selectedPokemon.isEmpty())
                    if (slotsDone < selectedPokemon.size()) {
                        view.setElement(i, getPokemonElement(selectedPokemon.get(slotsDone), true, view, page));
                        slotsDone++;
                    }
            }
        }
        slotsDone = 0;
        for (int i = page * itemsPerPage; i < gymData.size(); i++) {
            if (slotsDone == itemsPerPage) {
                break;
            }
            if (!selectedPokemon.contains(gymData.get(i))) {
                int currentRow = slotsDone / itemsPerRow;
                int currentSlot = slotsDone % itemsPerRow;
                int slot = (startOnLine * 9) + (currentRow * 9) + rowStart + currentSlot;
                //PixelmonData data = new PixelmonData();
                //ImportExportConverter.importText(gymData.get(i), data);
                view.setElement(slot, getPokemonElement(gymData.get(i), false, view, page));
                slotsDone++;
            }
        }

        //Next / Prev
        int finalPage = page;
        ItemStack nextStack = ItemStack.of(Sponge.getRegistry().getType(ItemType.class, "pixelmon:trade_holder_right").get(), 1);
        nextStack.offer(Keys.DISPLAY_NAME, Text.of("Next"));
        Consumer<Action.Click> nextAction = click -> Task.builder().execute(task -> constructChooseTeamPage(leader, challUUID, view, gym, arena, finalPage + 1)).submit(AGP.getInstance());

        ItemStack prevStack = ItemStack.of(Sponge.getRegistry().getType(ItemType.class, "pixelmon:trade_holder_left").get(), 1);
        prevStack.offer(Keys.DISPLAY_NAME, Text.of("Previous"));
        Consumer<Action.Click> prevAction = click -> Task.builder().execute(task -> constructChooseTeamPage(leader, challUUID, view, gym, arena, finalPage - 1)).submit(AGP.getInstance());

        ItemStack backStack = ItemStack.of(Sponge.getRegistry().getType(ItemType.class, "pixelmon:trade_monitor").get(), 1);
        backStack.offer(Keys.DISPLAY_NAME, Text.EMPTY);

        ItemStack confirmStack = ItemStack.builder().itemType(ItemTypes.DYE).add(Keys.DISPLAY_NAME, Utils.toText("&aStart Battle", false)).add(Keys.DYE_COLOR, LIME).build();
        Consumer<Action.Click> startBattleAction = click -> Task.builder().execute(task ->
        {
            if (selectedPokemon.size() >= gym.minimumPokemon && selectedPokemon.size() <= gym.maximumPokemon) {
                Optional<Player> challenger = Sponge.getServer().getPlayer(challUUID);
                if (!challenger.isPresent()) {
                    leader.sendMessage(Utils.toText("&7The challenger has gone offline!", true));
                    leader.closeInventory();
                    return;
                }
                if (!selectedPokemon.isEmpty()) {
                    List<Pokemon> leaderPixelmon = new ArrayList<>();
                    for (ShowdownStruc s : selectedPokemon) {
                        try {
                            Pokemon data = ImportExportConverter.importText(s.showdownCode);
                            leaderPixelmon.add(data);
                            //BattleUtil.pixelmonDataToTempBattlePokemon(leader, data).ifPresent(leaderPixelmon::add);
                        } catch (ShowdownImportException e) {
                            e.printStackTrace();
                            leader.sendMessage(Utils.toText("Unable to convert pokemon", true));
                        }
                    }

                    ArenaStruc as = arena;
                    if (as == null) {
                        for (ArenaStruc a : gym.Arenas) {
                            if (a != null) {
                                if (!a.inUse && a.Leader != null && a.Challenger != null) {
                                    Utils.setPosition(leader, a.Leader, gym.worldUUID);
                                    Utils.setPosition(challenger.get(), a.Challenger, gym.worldUUID);

                                    a.inUse = true;
                                    as = a;
                                    break;
                                }
                            }
                        }
                    } else {
                        if (as.Leader != null && as.Challenger != null) {
                            as.inUse = true;
                            Utils.setPosition(leader, as.Leader, gym.worldUUID);
                            Utils.setPosition(challenger.get(), as.Challenger, gym.worldUUID);
                        }
                    }
                    PlayerPartyStorage challengerTeam = Pixelmon.storageManager.getParty((EntityPlayerMP) challenger.get());
                    challengerTeam.heal();

                    BattleStruc bs = new BattleStruc(gym, as, leader.getUniqueId(), challenger.get().getUniqueId());
                    DataStruc.gcon.GymBattlers.add(bs);
                    leader.closeInventory();
                    leader.sendMessage(Utils.toText("&7Initiating battle against &b" + challenger.get().getName() + "&7!", true));
                    challenger.get().sendMessage(Utils.toText("&7Gym Leader &b" + leader.getName() + " &7has accepted your challenge against the &b" + gym.Name + " &bGym!", true));
                    BattleUtil.startLeaderBattleWithTempTeam(challenger.get(), leader, leaderPixelmon);
                }
            }
        }).submit(AGP.getInstance());
        Element confirm = Element.of(confirmStack, startBattleAction);

        ItemStack cancelStack = ItemStack.builder().itemType(ItemTypes.DYE).add(Keys.DISPLAY_NAME, Utils.toText("&4Cancel", false)).add(Keys.DYE_COLOR, RED).build();
        Consumer<Action.Click> closeAction = click -> leader.closeInventory();

        Element cancel = Element.of(cancelStack, closeAction);

        Element prev = Element.of(prevStack, prevAction);
        Element next = Element.of(nextStack, nextAction);
        Element mehh = Element.of(backStack);

        view.setElement(48, prev);
        view.setElement(49, mehh);
        view.setElement(50, next);
        view.setElement(52, cancel);
        view.setElement(53, confirm);
    }

    private Element getPokemonElement(ShowdownStruc pokemonCode, boolean isSelected, View view, int page) {
        Pokemon pokemon = null;
        try {
            pokemon = ImportExportConverter.importText(pokemonCode.showdownCode);
        } catch (ShowdownImportException e) {
            System.out.println("Aha crap, someone did a dumb - getPokemonElement");
            return Element.of(ItemStackSnapshot.NONE);
        }
        ItemStack itemStack = Utils.getPixelmonSprite(pokemon);
        itemStack.offer(Keys.DISPLAY_NAME, toText("&d\u2605 &b" + pokemon.getSpecies().name + (!pokemon.getDisplayName().isEmpty() ? "(" + pokemon.getDisplayName() + ")" : "") + "&d \u2605", false));

        ArrayList<Text> lore = new ArrayList<>();
        lore.add(toText("&7Nature: &b" + pokemon.getNature().name(), false));
        lore.add(toText("&7Ability: &b" + pokemon.getAbility().getName(), false));
        lore.add(toText("&7Friendship: &b" + pokemon.getFriendship(), false));
        float ivHP = pokemon.getIVs().get(StatsType.HP);
        float ivAtk = pokemon.getIVs().get(StatsType.Attack);
        float ivDef = pokemon.getIVs().get(StatsType.Defence);
        float ivSpeed = pokemon.getIVs().get(StatsType.Speed);
        float ivSAtk = pokemon.getIVs().get(StatsType.SpecialAttack);
        float ivSDef = pokemon.getIVs().get(StatsType.SpecialDefence);
        int percentage = Math.round(((ivHP + ivDef + ivAtk + ivSpeed + ivSAtk + ivSDef) / 186f) * 100);
        lore.add(toText("&7IVs " + "(&b" + percentage + "%&7):", false));
        lore.add((toText("    &7HP: &b" + (int) ivHP + " &d| &7Atk: &b" + (int) ivAtk + " &d| &7Def: &b" + (int) ivDef, false)));
        lore.add((toText("    &7SAtk: &b" + (int) ivSAtk + " &d| &7SDef: &b" + ivSDef + " &d| &7Spd: &b" + (int) ivSpeed, false)));
        float evHP = pokemon.getEVs().get(StatsType.HP);
        float evAtk = pokemon.getEVs().get(StatsType.Attack);
        float evDef = pokemon.getEVs().get(StatsType.Defence);
        float evSpeed = pokemon.getEVs().get(StatsType.Speed);
        float evSAtk = pokemon.getEVs().get(StatsType.SpecialAttack);
        float evSDef = pokemon.getEVs().get(StatsType.SpecialDefence);
        lore.add(toText("&7EVs:", false));
        lore.add((toText("    &7HP: &b" + (int) evHP + " &d| &7Atk: &b" + (int) evAtk + " &d| &7Def: &b" + (int) evDef, false)));
        lore.add((toText("    &7SAtk: &b" + (int) evSAtk + " &d| &7SDef: &b" + (int) evSDef + " &d| &7Spd: &b" + (int) evSpeed, false)));
        lore.add(toText("&7Moves:", false));
        if (pokemon.getMoveset() != null) {
            for (Attack attack : pokemon.getMoveset().attacks) {
                if (attack != null) {
                    lore.add(toText("    &b" + attack.baseAttack.getUnLocalizedName(), false));
                }
            }
        }
        itemStack.offer(Keys.ITEM_LORE, lore);


        Consumer<Action.Click> clickConsumer = click -> Task.builder().execute(task -> {
            if (isSelected) {
                selectedPokemon.remove(pokemonCode);
                constructChooseTeamPage(leader, challengerUUID, view, gym, arena, page);
            } else {
                if (selectedPokemon.size() < 6)
                    if (!selectedPokemon.contains(pokemonCode)) {
                        selectedPokemon.add(pokemonCode);
                        constructChooseTeamPage(leader, challengerUUID, view, gym, arena, page);
                    }
            }

        }).submit(AGP.getInstance());

        return Element.of(itemStack, clickConsumer);
    }

}
