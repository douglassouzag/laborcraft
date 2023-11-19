package net.glok.laborcraft.util;

import net.glok.laborcraft.Laborcraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class BoxScreenHandler extends ScreenHandler {

  private final Inventory inventory;

  public BoxScreenHandler(int syncId, PlayerInventory playerInventory) {
    this(syncId, playerInventory, new SimpleInventory(27));
  }

  public BoxScreenHandler(
    int syncId,
    PlayerInventory playerInventory,
    Inventory inventory
  ) {
    super(Laborcraft.BOX_SCREEN_HANDLER, syncId);
    checkSize(inventory, 27);
    this.inventory = inventory;
    inventory.onOpen(playerInventory.player);

    int m;
    int l;
    for (m = 0; m < 3; ++m) {
      for (l = 0; l < 9; ++l) {
        this.addSlot(new Slot(inventory, l + m * 9, 8 + l * 18, 18 + m * 18));
      }
    }
    for (m = 0; m < 3; ++m) {
      for (l = 0; l < 9; ++l) {
        this.addSlot(
            new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 84 + m * 18)
          );
      }
    }
    for (m = 0; m < 9; ++m) {
      this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 142));
    }
  }

  @Override
  public boolean canUse(PlayerEntity player) {
    return this.inventory.canPlayerUse(player);
  }

  @Override
  public ItemStack quickMove(PlayerEntity player, int slot) {
    ItemStack itemStack = ItemStack.EMPTY;
    Slot clickedSlot = this.slots.get(slot);

    if (clickedSlot != null && clickedSlot.hasStack()) {
      ItemStack clickedStack = clickedSlot.getStack();
      itemStack = clickedStack.copy();

      if (slot < this.inventory.size()) {
        if (
          !this.insertItem(
              clickedStack,
              this.inventory.size(),
              this.slots.size(),
              true
            )
        ) {
          return ItemStack.EMPTY;
        }
      } else if (
        !this.insertItem(clickedStack, 0, this.inventory.size(), false)
      ) {
        return ItemStack.EMPTY;
      }

      if (clickedStack.isEmpty()) {
        clickedSlot.setStack(ItemStack.EMPTY);
      } else {
        clickedSlot.markDirty();
      }
    }

    return itemStack;
  }
}
