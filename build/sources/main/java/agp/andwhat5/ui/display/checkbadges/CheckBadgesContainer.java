package agp.andwhat5.ui.display.checkbadges;

import agp.andwhat5.config.structs.BadgeStruc;
import agp.andwhat5.config.structs.DataStruc;
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
import net.minecraft.util.text.TextFormatting;

import java.util.List;
import java.util.Optional;

public class CheckBadgesContainer extends AbstractContainer
{
	public CheckBadgesContainer(IInventory chestInventory, EntityPlayer player)
	{
		super(chestInventory, player);
	}

	@Override
	public <E extends List> void fillContents(int rows, E badges)
	{
		for (int i = 0; i < badges.size() && i < 36; i++)
		{
			BadgeStruc badge = (BadgeStruc) badges.get(i);
			String beatenGym = badge.Gym;
			Optional<GymStruc> gym = DataStruc.gcon.GymData.stream().filter(g -> g.Name.equals(beatenGym)).findAny();
			if (gym.isPresent())
			{
				Item item = Item.getByNameOrId(gym.get().Badge);
				item = item == null ? Item.getItemFromBlock(Blocks.BARRIER) : item;
				ItemStack stack = new ItemStack(item);
				if (!stack.hasTagCompound())
				{
					stack.setTagCompound(new NBTTagCompound());
				}
				if (!stack.getTagCompound().hasKey("display"))
				{
					stack.getTagCompound().setTag("display", new NBTTagCompound());
				}
				NBTTagCompound display = stack.getTagCompound().getCompoundTag("display");
				stack.setStackDisplayName(TextFormatting.YELLOW + "\u2605 " + TextFormatting.GREEN + stack.getDisplayName() +
						TextFormatting.YELLOW + " \u2605");
				NBTTagList lore = new NBTTagList();
				lore.appendTag(new NBTTagString(TextFormatting.GRAY + "Gym: " + TextFormatting.YELLOW +
						badge.Gym));
				lore.appendTag(new NBTTagString(TextFormatting.GRAY + "Leader: " + TextFormatting.YELLOW +
						badge.Leader));
				lore.appendTag(new NBTTagString(TextFormatting.GRAY + "Date Obtained: " + TextFormatting.YELLOW
						+ badge.Obtained));
				lore.appendTag(new NBTTagString(TextFormatting.GRAY + "Pokemon:"));

				if (badge.Pokemon.isEmpty())
				{
					lore.appendTag(new NBTTagString("  " + TextFormatting.RED + "Unknown"));
				} else
				{
					String line = "";
					for (int k = 0; k < badge.Pokemon.size(); k++)
					{
						if (k == 2 || k == 5 || k + 1 == badge.Pokemon.size())
						{
							line += badge.Pokemon.get(k);
							if (k == 2 && k + 1 < badge.Pokemon.size())
							{
								lore.appendTag(new NBTTagString("  " + TextFormatting.YELLOW + line));
								line = "";
							}
						} else
						{
							line += badge.Pokemon.get(k) + ", ";
						}
					}
					lore.appendTag(new NBTTagString("  " + TextFormatting.YELLOW + line));
				}
				display.setTag("Lore", lore);

				putStackInSlot(i, stack);
			}
		}

		if (rows == 6)
		{
			Item item = Item.getItemFromBlock(Blocks.STAINED_GLASS_PANE);
			ItemStack stack = new ItemStack(item);
			if (!stack.hasTagCompound())
			{
				stack.setTagCompound(new NBTTagCompound());
			}
			if (!stack.getTagCompound().hasKey("display"))
			{
				stack.getTagCompound().setTag("display", new NBTTagCompound());
			}
			stack.setStackDisplayName(TextFormatting.YELLOW + "");
			for (int i = 36; i < 45; i++)
			{
				putStackInSlot(i, stack);
			}
		}
	}
}
