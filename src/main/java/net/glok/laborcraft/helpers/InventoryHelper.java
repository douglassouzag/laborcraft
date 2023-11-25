package net.glok.laborcraft.helpers;

import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class InventoryHelper {

  private static final int MAX_STACK_SIZE = 64;

  public void transferItems(
    DefaultedList<ItemStack> transferFrom,
    DefaultedList<ItemStack> transferTo,
    Item[] itemFilter
  ) {
    for (int i = 0; i < transferFrom.size(); i++) {
      ItemStack itemStackFrom = transferFrom.get(i);
      if (
        itemStackFrom.isEmpty() ||
        !isInFilter(itemStackFrom.getItem(), itemFilter)
      ) {
        continue;
      }

      boolean isAdded = false;
      if (itemStackFrom.getMaxCount() > 1) {
        for (ItemStack itemStackTo : transferTo) {
          if (ItemStack.areItemsEqual(itemStackTo, itemStackFrom)) {
            int transferCount = Math.min(
              itemStackFrom.getCount(),
              MAX_STACK_SIZE - itemStackTo.getCount()
            );
            itemStackTo.increment(transferCount);
            itemStackFrom.decrement(transferCount);
            isAdded = true;
            break;
          }
        }
      }

      if (!isAdded && itemStackFrom.getCount() <= MAX_STACK_SIZE) {
        transferTo.add(itemStackFrom);
        transferFrom.set(i, ItemStack.EMPTY);
      }
    }
  }

  public void transferItemsToChest(
    DefaultedList<ItemStack> transferFrom,
    ChestBlockEntity chest,
    Item[] itemFilter
  ) {
    int chestSize = chest.size();
    for (int i = 0; i < transferFrom.size(); i++) {
      ItemStack itemStackFrom = transferFrom.get(i);
      if (
        itemStackFrom.isEmpty() ||
        !isInFilter(itemStackFrom.getItem(), itemFilter)
      ) {
        continue;
      }

      if (itemStackFrom.getMaxCount() > 1) {
        for (int j = 0; j < chestSize; j++) {
          ItemStack itemStackTo = chest.getStack(j);
          if (ItemStack.areItemsEqual(itemStackTo, itemStackFrom)) {
            int transferCount = Math.min(
              itemStackFrom.getCount(),
              MAX_STACK_SIZE - itemStackTo.getCount()
            );
            itemStackTo.increment(transferCount);
            itemStackFrom.decrement(transferCount);
            if (itemStackFrom.getCount() == 0) {
              break;
            }
          }
        }
      }

      if (
        itemStackFrom.getCount() > 0 &&
        itemStackFrom.getCount() <= MAX_STACK_SIZE
      ) {
        for (int j = 0; j < chestSize; j++) {
          ItemStack itemStackTo = chest.getStack(j);
          if (itemStackTo.isEmpty()) {
            chest.setStack(j, itemStackFrom);
            transferFrom.set(i, ItemStack.EMPTY);
            break;
          }
        }
      }
    }
  }

  public void transferItemsFromChest(
    ChestBlockEntity chest,
    DefaultedList<ItemStack> transferTo,
    Item[] itemFilter
  ) {
    int chestSize = chest.size();
    for (int i = 0; i < chestSize; i++) {
      ItemStack itemStackFrom = chest.getStack(i);
      if (
        itemStackFrom.isEmpty() ||
        !isInFilter(itemStackFrom.getItem(), itemFilter)
      ) {
        continue;
      }

      boolean isAdded = false;
      if (itemStackFrom.getMaxCount() > 1) {
        for (ItemStack itemStackTo : transferTo) {
          if (ItemStack.areItemsEqual(itemStackTo, itemStackFrom)) {
            int transferCount = Math.min(
              itemStackFrom.getCount(),
              MAX_STACK_SIZE - itemStackTo.getCount()
            );
            itemStackTo.increment(transferCount);
            itemStackFrom.decrement(transferCount);
            isAdded = true;
            break;
          }
        }
      }

      if (!isAdded && itemStackFrom.getCount() <= MAX_STACK_SIZE) {
        transferTo.add(itemStackFrom);
        chest.setStack(i, ItemStack.EMPTY);
      }
    }
  }

  public boolean haveSpaceInChestForItems(
    ChestBlockEntity chest,
    Item[] items
  ) {
    int chestSize = chest.size();
    for (Item item : items) {
      ItemStack itemStack = new ItemStack(item);
      for (int i = 0; i < chestSize; i++) {
        ItemStack itemStackInChest = chest.getStack(i);
        if (itemStackInChest.isEmpty()) {
          return true;
        }
        if (
          itemStack.isStackable() &&
          ItemStack.areItemsEqual(itemStackInChest, itemStack) &&
          itemStackInChest.getCount() < MAX_STACK_SIZE
        ) {
          return true;
        }
      }
    }
    return false;
  }

  public boolean isChestFull(ChestBlockEntity chest) {
    int chestSize = chest.size();
    for (int i = 0; i < chestSize; i++) {
      ItemStack itemStackInChest = chest.getStack(i);
      if (
        itemStackInChest.isEmpty() ||
        (
          itemStackInChest.isStackable() &&
          itemStackInChest.getCount() < MAX_STACK_SIZE
        )
      ) {
        return false;
      }
    }
    return true;
  }

  private boolean isInFilter(Item item, Item[] itemFilter) {
    for (Item filterItem : itemFilter) {
      if (item == filterItem) {
        return true;
      }
    }
    return false;
  }
}
