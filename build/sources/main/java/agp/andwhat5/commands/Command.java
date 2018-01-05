package agp.andwhat5.commands;

import agp.andwhat5.Utils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.UUID;

public abstract class Command extends CommandBase
{
	protected String name;
	protected String usage;
	protected int permissionLevel;

	public Command(String name, String usage)
	{
		this(name, usage, 4);
	}

	public Command(String name, String usage, int permissionLevel)
	{
		this.name = name;
		this.usage = usage;
		this.permissionLevel = permissionLevel;
	}

	@Override
	public String getName()
	{
		return this.name;
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return this.usage;
	}

	@Override
	public int getRequiredPermissionLevel()
	{
		return this.permissionLevel;
	}

	/**
	 * Sends the sender the usage message for this command
	 *
	 * @param sender commandSender
	 */
	public void sendUsage(ICommandSender sender)
	{
		sender.sendMessage(Utils.toText("&7Incorrect Usage: &b" + getUsage(sender), true));
	}

	/**
	 * Checks if the commandSender is a player and returns the entityPlayer if true.
	 * If not, it throws a CommandException
	 *
	 * @param sender commandSender
	 * @return the sender as EntityPlayerMP
	 * @throws CommandException
	 */
	public static EntityPlayerMP requireEntityPlayer(ICommandSender sender) throws CommandException
	{
		if (sender instanceof EntityPlayerMP)
		{
			return (EntityPlayerMP) sender;
		}
		throw getException("You must be a player to use this command!");
	}

	/**
	 * Checks if the user is online. If they are it returns the entityPlayer.
	 * If not, it throws a CommandException stating the player can not be found.
	 *
	 * @param username The username or uuid string to check.
	 * @return the EntityPlayerMP
	 * @throws CommandException
	 */
	public static EntityPlayerMP requireEntityPlayer(String username) throws CommandException
	{
		EntityPlayerMP player = getEntityPlayer(username);
		if (player != null) return player;
		throw getException("That player can not be found!");
	}

	/**
	 * Gets the entityPlayer if they are online else returns null
	 *
	 * @param username The username or uuid string to lookup
	 * @return EntityPlayerMP or null if they are not online
	 * @throws IllegalArgumentException if we cant parse the UUID
	 */
	public static EntityPlayerMP getEntityPlayer(String username)
	{
		if (username == null) return null;
		if (username.length() == 36)
		{
			UUID uuid = UUID.fromString(username);
			return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(uuid);
		}
		return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(username);
	}

	/**
	 * Creates a new CommandException that doesn't have a StackTrace.
	 *
	 * @param message The message to send to the commandSender
	 * @return the CommandException to be thrown.
	 */
	private static CommandException getException(String message)
	{
		return new CommandException(TextFormatting.RED + message)
		{
			@Override
			public synchronized Throwable fillInStackTrace()
			{
				return this;
			}
		};
	}
}
