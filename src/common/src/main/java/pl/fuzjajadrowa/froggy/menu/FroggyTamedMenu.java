package pl.fuzjajadrowa.froggy.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import pl.fuzjajadrowa.froggy.entity.FroggyTamedEntity;
import pl.fuzjajadrowa.froggy.registry.FroggyMenus;

public class FroggyTamedMenu extends AbstractContainerMenu {
    private final FroggyTamedEntity froggy;

    public FroggyTamedMenu(int windowId, Inventory playerInventory, int entityId) {
        this(windowId, playerInventory, (FroggyTamedEntity) playerInventory.player.level().getEntity(entityId));
    }

    public FroggyTamedMenu(int windowId, Inventory playerInventory, FroggyTamedEntity froggy) {
        super(FroggyMenus.FROGGY_TAMED.get(), windowId);
        this.froggy = froggy;
        
        net.minecraft.world.SimpleContainer froggyInventory = froggy.getInventory();

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 9; c++) {
                final int index = c + r * 9;
                this.addSlot(new Slot(froggyInventory, index, 120 + c * 18, 18 + r * 18) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return index < FroggyTamedMenu.this.froggy.getInventorySize();
                    }

                    @Override
                    public boolean mayPickup(Player player) {
                        return index < FroggyTamedMenu.this.froggy.getInventorySize();
                    }
                });
            }
        }

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 9; c++) {
                this.addSlot(new Slot(playerInventory, c + r * 9 + 9, 65 + c * 18, 86 + r * 18));
              }
        }

        for (int c = 0; c < 9; c++) {
            this.addSlot(new Slot(playerInventory, c, 65 + c * 18, 145));
        }
    }

    public FroggyTamedEntity getFroggy() {
        return this.froggy;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.froggy != null && this.froggy.isAlive() && this.froggy.distanceTo(player) < 8.0F;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack itemStack2 = slot.getItem();
            itemStack = itemStack2.copy();
            
            if (slotIndex < 27) {
                if (!this.moveItemStackTo(itemStack2, 27, 63, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                int inventorySize = this.froggy.getInventorySize();
                if (inventorySize > 0) {
                    if (!this.moveItemStackTo(itemStack2, 0, inventorySize, false)) {
                        if (slotIndex < 54) {
                            if (!this.moveItemStackTo(itemStack2, 54, 63, false)) {
                                return ItemStack.EMPTY;
                            }
                        } else {
                            if (!this.moveItemStackTo(itemStack2, 27, 54, false)) {
                                return ItemStack.EMPTY;
                            }
                        }
                    }
                } else {
                    if (slotIndex < 54) {
                        if (!this.moveItemStackTo(itemStack2, 54, 63, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else {
                        if (!this.moveItemStackTo(itemStack2, 27, 54, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                }
            }

            if (itemStack2.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemStack2);
        }
        return itemStack;
    }
}