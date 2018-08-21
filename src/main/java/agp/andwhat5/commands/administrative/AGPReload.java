package agp.andwhat5.commands.administrative;

import agp.andwhat5.AGP;
import agp.andwhat5.PlayerCheck;
import agp.andwhat5.Utils;
import agp.andwhat5.commands.Command;
import agp.andwhat5.config.structs.DataStruc;
import net.minecraft.command.CommandException;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.api.command.CommandSource;

public class AGPReload extends Command {

    public AGPReload() {
        super("/agpreload");
    }

    @Override
    public void execute(MinecraftServer server, CommandSource sender, String[] args) throws CommandException {
        try {
            AGP.getInstance().loadConfig();
            AGP.getInstance().getStorage().shutdown();
            DataStruc.gcon = new DataStruc();
            AGP.getInstance().getStorage().init();
            AGP.getInstance().setupTasks();
            PlayerCheck.cacheNames();
            sender.sendMessage(Utils.toText("&7AGP reloaded successfully", true));
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(Utils.toText("&7AGP failed to reload. See console for details.", true));
        }
    }

}
