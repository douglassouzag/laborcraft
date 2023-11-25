package net.glok.laborcraft.goals;

import java.util.List;
import net.glok.laborcraft.entity.custom.NPCEntity;
import net.glok.laborcraft.helpers.InventoryHelper;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class CollectItemsGoal extends Goal {

  private final NPCEntity npc;
  private final Item[] itemsToCollect;
  private final InventoryHelper inventoryHelper = new InventoryHelper();

  public CollectItemsGoal(NPCEntity npc, Item[] itemsToCollect) {
    this.npc = npc;
    this.itemsToCollect = itemsToCollect;
  }

  public boolean isThereAnyItemsToCollect(Box workArea) {
    World world = npc.getEntityWorld();
    for (ItemEntity itemEntity : world.getEntitiesByClass(
      ItemEntity.class,
      workArea,
      itemEntity -> true
    )) {
      for (Item item : itemsToCollect) {
        if (itemEntity.getStack().getItem() == item) {
          return true;
        }
      }
    }
    return false;
  }

  public ItemEntity findNearesItemToCollect(Box workArea) {
    World world = npc.getEntityWorld();
    ItemEntity nearestItem = null;
    double nearestDistance = Double.MAX_VALUE;
    for (ItemEntity itemEntity : world.getEntitiesByClass(
      ItemEntity.class,
      workArea,
      itemEntity -> true
    )) {
      for (Item item : itemsToCollect) {
        if (itemEntity.getStack().getItem() == item) {
          double distance = npc
            .getBlockPos()
            .getSquaredDistance(itemEntity.getBlockPos());
          if (distance < nearestDistance) {
            nearestItem = itemEntity;
            nearestDistance = distance;
          }
        }
      }
    }
    return nearestItem;
  }

  public void goToItem(ItemEntity itemEntity) {
    npc
      .getNavigation()
      .startMovingTo(
        itemEntity.getX(),
        itemEntity.getY(),
        itemEntity.getZ(),
        0.5
      );
  }

  @Override
  public boolean canStart() {
    return (
      isThereAnyItemsToCollect(npc.workArea) &&
      !inventoryHelper.isInventoryFull(npc.getItems())
    );
  }

  public boolean isNearEnough(ItemEntity itemEntity) {
    return (
      npc.getBlockPos().getManhattanDistance(itemEntity.getBlockPos()) < 3f
    );
  }

  public void collectItemAndPutInInventory(ItemEntity itemEntity) {
    npc
      .getLookControl()
      .lookAt(itemEntity.getX(), itemEntity.getY(), itemEntity.getZ());
    npc.swingHand(Hand.MAIN_HAND);

    if (itemEntity.isAlive()) {
      ItemStack itemStack = itemEntity.getStack();
      if (itemStack != null) {
        Item item = itemStack.getItem();
        if (itemStack.isStackable()) {
          List<ItemStack> items = npc.getItems();
          for (int i = 0; i < items.size(); i++) {
            ItemStack inventoryStack = items.get(i);
            if (inventoryStack.isEmpty()) {
              items.set(i, itemStack.copy());
              itemEntity.discard();
              return;
            } else if (
              inventoryStack.getItem() == item &&
              inventoryStack.getCount() +
              itemStack.getCount() <=
              item.getMaxCount()
            ) {
              inventoryStack.increment(itemStack.getCount());
              itemEntity.discard();
              return;
            }
          }
        }
        npc.getItems().add(itemStack.copy());
        itemEntity.discard();
      }
    }
  }

  @Override
  public void tick() {
    ItemEntity nearestItem = findNearesItemToCollect(npc.workArea);

    if (nearestItem == null) {
      return;
    }

    goToItem(nearestItem);

    if (
      isNearEnough(nearestItem) &&
      !inventoryHelper.isInventoryFull(npc.getItems())
    ) {
      collectItemAndPutInInventory(nearestItem);
    }
  }
}
