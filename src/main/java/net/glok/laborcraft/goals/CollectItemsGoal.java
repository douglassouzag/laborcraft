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
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;

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
  public boolean shouldContinue() {
    ItemEntity[] items =
      this.getClosestItemEntities().toArray(new ItemEntity[0]);

    for (ItemEntity item : items) {
      if (isItemReachable(item)) {
        return true;
      }
    }

    return false;
  }

  @Override
  public boolean canStart() {
    return (
      isThereSlotEmptyOnInventory() && this.mob.isBreakingBlocks == false
    );
  }

  public boolean isItemInWorkChunk(ItemEntity item) {
    ChunkPos chunkPos = this.mob.workChunk;
    BlockPos itemPos = item.getBlockPos();
    Chunk itemChunk = this.mob.getWorld().getChunk(itemPos);
    Chunk worChunk = this.mob.getWorld().getChunk(chunkPos.getStartPos());

    if (itemChunk == worChunk) {
      return true;
    }

    return false;
  }

  public boolean isItemReachable(ItemEntity item) {
    return this.mob.getNavigation().isValidPosition(item.getBlockPos());
  }

  public void collectNearbyItem(ItemEntity[] items) {
    DefaultedList<ItemStack> inventory = this.mob.getItems();
    double thresholdDistance = 1.8f;

    Arrays.sort(
      items,
      Comparator.comparingInt(item -> item.getStack().getItem().getMaxCount())
    );

    for (ItemEntity item : items) {
      if (
        item.getStack().isEmpty() ||
        !isItemInWorkChunk(item) ||
        !isItemReachable(item)
      ) {
        continue;
      }
      this.mob.getNavigation().startMovingTo(item, 0.5);

      if (this.mob.distanceTo(item) <= thresholdDistance) {
        ItemStack itemStackToCollect = item.getStack();
        int maxStackSize = itemStackToCollect.getMaxCount();

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
            this.mob.getWorld()
              .playSound(
                null,
                item.getBlockPos(),
                SoundEvents.ENTITY_ITEM_PICKUP,
                SoundCategory.AMBIENT,
                0.6f,
                1f
              );

            item.discard();
            break;
          }
        }

        if (itemStackToCollect.isEmpty()) {
          break;
        }
      } else {
        continue;
      }
    }
  }

  @Override
  public void tick() {
    this.mob.isCollectingItems = true;

    ItemEntity[] items =
      this.getClosestItemEntities().toArray(new ItemEntity[0]);

    this.collectNearbyItem(items);
  }

  @Override
  public void stop() {
    this.mob.isCollectingItems = false;
  }
}
