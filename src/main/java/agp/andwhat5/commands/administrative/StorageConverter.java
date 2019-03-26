package agp.andwhat5.commands.administrative;

import agp.andwhat5.AGP;
import agp.andwhat5.Utils;
import agp.andwhat5.config.AGPConfig;
import agp.andwhat5.config.structs.DataStruc;
import agp.andwhat5.config.structs.GymStruc;
import agp.andwhat5.config.structs.PlayerStruc;
import agp.andwhat5.storage.FlatFileProvider;
import agp.andwhat5.storage.sql.H2Provider;
import agp.andwhat5.storage.sql.MySQLProvider;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class StorageConverter implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        String type = args.<String>getOne("type").get();
        Optional<String> confirm = args.getOne("confirm");

        if (!confirm.isPresent()) {
            src.sendMessage(Utils.toText("&7Incorrect usage: &b/STC <flatfile|h2|mysql> <confirm>&7.", true));
            return CommandResult.success();
        }

        if (confirm.get().equalsIgnoreCase("confirm")) {
            List<GymStruc> gymData = DataStruc.gcon.GymData;
            HashMap<UUID, PlayerStruc> playerData = DataStruc.gcon.PlayerData;
            try {
                AGP.getInstance().getStorage().shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (type.equalsIgnoreCase(AGPConfig.Storage.storageType)) {
                src.sendMessage(Utils.toText("&7This storage type is already in use!", true));
                return CommandResult.success();
            }

            if (type.equalsIgnoreCase("flatfile")) {
                AGP.getInstance().setStorage(new FlatFileProvider());
            } else if (type.equalsIgnoreCase("h2")) {
                AGP.getInstance().setStorage(new H2Provider(AGPConfig.Storage.GymsTableName, AGPConfig.Storage.BadgesTableName));
            } else if (type.equalsIgnoreCase("mysql")) {
                AGP.getInstance().setStorage(new MySQLProvider(AGPConfig.Storage.GymsTableName, AGPConfig.Storage.BadgesTableName));
            } else {
                throw new CommandException(Text.of("Invalid storage type"));
            }

            try {
                AGP.getInstance().getStorage().init();
            } catch (Exception e) {
                e.printStackTrace();
            }
            AGP.getInstance().getStorage().updateAllGyms(gymData);
            AGP.getInstance().getStorage().updateAllPlayers(playerData);
            Utils.saveAGPData();
        }
        return CommandResult.success();

    }

}
