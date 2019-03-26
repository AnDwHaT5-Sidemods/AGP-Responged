package agp.andwhat5.commands.administrative;

import agp.andwhat5.Utils;
import agp.andwhat5.config.AGPConfig;
import agp.andwhat5.config.structs.GymStruc;
import com.pixelmonmod.pixelmon.entities.pixelmon.drops.DropItemHelper;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;

public class GiveBadge implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        Player target = args.<Player>getOne("player").get();
        GymStruc gym = args.<GymStruc>getOne("GymName").get();

        if (!Utils.isGymLeader((Player) src, gym) && !src.hasPermission("agp.headleader")) {//TODO don't assume the sender is a player
            src.sendMessage(Utils.toText("&7You must be a leader of the &b" + gym.Name + " &7Gym to give its badge!", true));
            return CommandResult.success();
        }

        if (Utils.hasBadge(target, gym)) {
            src.sendMessage(Utils.toText("&b" + target.getName() + " &7has already beaten the &b" + gym.Name + " &7Gym!", true));
            return CommandResult.success();
        }

        Utils.giveBadge(target, gym, src.getName());
        if (gym.Money != 0) {
            Utils.addCurrency(target, gym.Money);
        }
        if (AGPConfig.General.physicalBadge) {
            ItemType itemType = Sponge.getRegistry().getType(ItemType.class, gym.Badge).orElse(ItemTypes.POTATO);
            ItemStack itemStack = ItemStack.of(itemType, 1);
            //noinspection ConstantConditions
            DropItemHelper.giveItemStackToPlayer((EntityPlayer) target, (net.minecraft.item.ItemStack) (Object) itemStack);//TODO make helper function for this
        }
        if (!gym.Commands.isEmpty()) {
            gym.Commands.forEach(i -> Sponge.getCommandManager().process((CommandSource) Sponge.getServer(), i.trim().replace("%player%", target.getName()).replace("%leader%", src.getName())));
        }

        src.sendMessage(Utils.toText("&7Successfully gave &b" + target.getName() + " &7the &b" + gym.Name + " &7Gym's badge!", true));
        src.sendMessage(Utils.toText("&7Congratulations, you defeated the &b" + gym.Name + " &7Gym!", true));
        if (AGPConfig.Announcements.winAnnouncement) {
            Sponge.getServer().getBroadcastChannel().send(Utils.toText(AGPConfig.Announcements.winMessage
                    .replace("{gym}", gym.Name).replace("{challenger}", target.getName()).replace("{leader}", src.getName()), true));
        }

        return CommandResult.success();
    }

}
