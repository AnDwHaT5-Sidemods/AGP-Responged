package agp.andwhat5.ui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.util.List;

public abstract class AbstractContainer extends Container {
    //TODO: Migrate to sponge... please...

    private IInventory chestInventory;

    private int numRows;

    public AbstractContainer(IInventory chestInventory, EntityPlayer player) {
        this.chestInventory = chestInventory;
        chestInventory.openInventory(player);

        numRows = chestInventory.getSizeInventory() / 9;
        for (int j = 0; j < numRows; ++j) // our inv
        {
            for (int k = 0; k < 9; ++k) {
                this.addSlotToContainer(new Slot(chestInventory, k + j * 9, 8 + k * 18, 18 + j * 18));
            }
        }
        int i = (this.numRows - 4) * 18; // player inv
        for (int l = 0; l < 3; ++l) {
            for (int j1 = 0; j1 < 9; ++j1) {
                this.addSlotToContainer(new Slot(player.inventory, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + i));
            }
        }
        for (int i1 = 0; i1 < 9; ++i1) // player hot bar
        {
            this.addSlotToContainer(new Slot(player.inventory, i1, 8 + i1 * 18, 161 + i));
        }
    }

    @Override
    public ItemStack slotClick(int index, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        if (index > (numRows * 9) - 1 || index < 0) {
            //Taken from Container#slotClick to bypass a bug
            if (clickTypeIn == ClickType.QUICK_MOVE && (dragType == 0 || dragType == 1)) {
                if (index < 0) {
                    return ItemStack.EMPTY;
                }

                Slot slot5 = this.inventorySlots.get(index);

                if (slot5 == null || !slot5.canTakeStack(player)) {
                    return ItemStack.EMPTY;
                }

                //Avoids an infinite loop from Container#slotClick
                return this.transferStackInSlot(player, index);
            }

            return super.slotClick(index, dragType, clickTypeIn, player);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        if (index > (numRows * 9) - 1 || index < 0) {
            return super.transferStackInSlot(playerIn, index);
        }
        return null;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return this.chestInventory.isUsableByPlayer(playerIn);
    }

    public int getSizeOfInv() {
        return this.numRows * 9;
    }

    public abstract <E extends List> void fillContents(int rows, E contents);
}
