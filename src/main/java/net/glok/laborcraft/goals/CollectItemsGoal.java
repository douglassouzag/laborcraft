package net.glok.laborcraft.goals;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import net.glok.laborcraft.entity.custom.DefaultWorkerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Box;

public class CollectItemsGoal extends Goal {

  protected final DefaultWorkerEntity mob;
  protected final Item[] itemsToLookFor;

  public CollectItemsGoal(DefaultWorkerEntity mob, Item[] itemsToLookFor) {
    this.mob = mob;
    this.itemsToLookFor = itemsToLookFor;
  }

  public List<ItemEntity> getClosestItemEntities() {
    Box boundingBox = new Box(
      this.mob.workChunk.getStartX(),
      -64,
      this.mob.workChunk.getStartZ(),
      this.mob.workChunk.getEndX(),
      320,
      this.mob.workChunk.getEndZ()
    );

    List<ItemEntity> closestItemEntities =
      this.mob.getWorld()
        .getEntitiesByType(
          EntityType.ITEM,
          boundingBox,
          entity -> {
            for (Item item : this.itemsToLookFor) {
              if (entity.getStack().getItem() == item) {
                return true;
              }
            }
            return false;
          }
        );

    return closestItemEntities;
  }

  public boolean isThereSlotEmptyOnInventory() {
    DefaultedList<ItemStack> inventory = this.mob.getItems();
    for (ItemStack itemStack : inventory) {
      if (itemStack.isEmpty()) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean canStart() {
    return isThereSlotEmptyOnInventory();
  }

  public void collectNearbyItem(ItemEntity[] items) {
    DefaultedList<ItemStack> inventory = this.mob.getItems();

    Arrays.sort(
      items,
      Comparator.comparingInt(item -> item.getStack().getItem().getMaxCount())
    );

    for (ItemEntity item : items) {
      if (item.getStack().isEmpty()) {
        continue;
      }
      this.mob.getNavigation().startMovingTo(item, 0.5);

      ItemStack itemStackToCollect = item.getStack();
      int maxStackSize = itemStackToCollect.getMaxCount();

      // Look for stackable items in the inventory
      for (ItemStack inventoryStack : inventory) {
        if (inventoryStack.isEmpty()) {
          continue;
        }
        if (
          inventoryStack.getItem() == itemStackToCollect.getItem() &&
          inventoryStack.getCount() < maxStackSize
        ) {
          int spaceLeftInStack = maxStackSize - inventoryStack.getCount();
          int amountToTransfer = Math.min(
            spaceLeftInStack,
            itemStackToCollect.getCount()
          );

          // Transfer items to stackable slot in inventory
          inventoryStack.increment(amountToTransfer);
          itemStackToCollect.decrement(amountToTransfer);

          if (itemStackToCollect.isEmpty()) {
            item.discard();
            break;
          }
        }
      }

      for (int i = 0; i < inventory.size(); i++) {
        ItemStack emptySlot = inventory.get(i);
        if (emptySlot.isEmpty()) {
          inventory.set(i, itemStackToCollect);
          item.discard();
          break;
        }
      }

      if (itemStackToCollect.isEmpty()) {
        break;
      }
    }
  }

  @Override
  public void tick() {
    ItemEntity[] items =
      this.getClosestItemEntities().toArray(new ItemEntity[0]);

    this.collectNearbyItem(items);
  }
}
