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
                this.addSlot(new Slot(froggyInventory, index, 121 + c * 18, 19 + r * 18) {
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

        for (int i = 0; i < 4; i++) {
            final int armorSlotIndex = 27 + i;
            final net.minecraft.world.entity.EquipmentSlot slotType = getArmorSlotType(i);
            this.addSlot(new Slot(froggyInventory, armorSlotIndex, 249, 89 + i * 18) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    net.minecraft.world.entity.EquipmentSlot itemSlot = null;
                    //? if >=1.21.1 {
                    itemSlot = FroggyTamedMenu.this.froggy.getEquipmentSlotForItem(stack);
                    //?} else {
                    /* itemSlot = net.minecraft.world.entity.LivingEntity.getEquipmentSlotForItem(stack); */
                    //?}
                    return itemSlot == slotType;
                }

                @Override
                public com.mojang.datafixers.util.Pair<net.minecraft.resources.ResourceLocation, net.minecraft.resources.ResourceLocation> getNoItemIcon() {
                    net.minecraft.resources.ResourceLocation emptyIcon = null;
                    switch (slotType) {
                        case HEAD: emptyIcon = net.minecraft.world.inventory.InventoryMenu.EMPTY_ARMOR_SLOT_HELMET; break;
                        case CHEST: emptyIcon = net.minecraft.world.inventory.InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE; break;
                        case LEGS: emptyIcon = net.minecraft.world.inventory.InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS; break;
                        case FEET: emptyIcon = net.minecraft.world.inventory.InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS; break;
                    }
                    return com.mojang.datafixers.util.Pair.of(net.minecraft.world.inventory.InventoryMenu.BLOCK_ATLAS, emptyIcon);
                }
            });
        }

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 9; c++) {
                this.addSlot(new Slot(playerInventory, c + r * 9 + 9, 66 + c * 18, 87 + r * 18));
              }
        }

        for (int c = 0; c < 9; c++) {
            this.addSlot(new Slot(playerInventory, c, 66 + c * 18, 145));
        }
    }

    private static net.minecraft.world.entity.EquipmentSlot getArmorSlotType(int index) {
        switch (index) {
            case 0: return net.minecraft.world.entity.EquipmentSlot.HEAD;
            case 1: return net.minecraft.world.entity.EquipmentSlot.CHEST;
            case 2: return net.minecraft.world.entity.EquipmentSlot.LEGS;
            case 3: return net.minecraft.world.entity.EquipmentSlot.FEET;
            default: throw new IllegalArgumentException("Invalid armor slot index: " + index);
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
            
            if (slotIndex < 31) {
                if (!this.moveItemStackTo(itemStack2, 31, 67, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                boolean moved = false;
                net.minecraft.world.entity.EquipmentSlot armorSlot = null;
                //? if >=1.21.1 {
                armorSlot = this.froggy.getEquipmentSlotForItem(itemStack2);
                //?} else {
                /* armorSlot = net.minecraft.world.entity.LivingEntity.getEquipmentSlotForItem(itemStack2); */
                //?}
                
                boolean isArmor = false;
                //? if >=1.21.1 {
                isArmor = armorSlot != null && armorSlot.getType() == net.minecraft.world.entity.EquipmentSlot.Type.HUMANOID_ARMOR;
                //?} else {
                /* isArmor = armorSlot != null && armorSlot.getType() == net.minecraft.world.entity.EquipmentSlot.Type.ARMOR; */
                //?}
                
                if (isArmor) {
                    int targetArmorSlot = -1;
                    switch (armorSlot) {
                        case HEAD: targetArmorSlot = 27; break;
                        case CHEST: targetArmorSlot = 28; break;
                        case LEGS: targetArmorSlot = 29; break;
                        case FEET: targetArmorSlot = 30; break;
                    }
                    if (targetArmorSlot != -1) {
                        moved = this.moveItemStackTo(itemStack2, targetArmorSlot, targetArmorSlot + 1, false);
                    }
                }
                
                if (!moved) {
                    int inventorySize = this.froggy.getInventorySize();
                    if (inventorySize > 0) {
                        moved = this.moveItemStackTo(itemStack2, 0, inventorySize, false);
                    }
                }
                
                if (!moved) {
                    if (slotIndex < 58) {
                        if (!this.moveItemStackTo(itemStack2, 58, 67, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else {
                        if (!this.moveItemStackTo(itemStack2, 31, 58, false)) {
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