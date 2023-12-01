package net.glok.laborcraft.goals;

import net.glok.laborcraft.entity.custom.NPCEntity;
import net.glok.laborcraft.helpers.InventoryHelper;
import net.glok.laborcraft.state.StateMachineGoal.StateEnum;
import net.minecraft.block.Block;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public class DepositItemsInChestGoal extends Goal {

  private final NPCEntity npc;
  private final Item[] itemsToDeposit;
  private InventoryHelper inventoryHelper = new InventoryHelper();

  public DepositItemsInChestGoal(NPCEntity npc, Item[] itemsToDeposit) {
    this.npc = npc;
    this.itemsToDeposit = itemsToDeposit;
  }

  public boolean haveItemsToDeposit() {
    DefaultedList<ItemStack> npcInventory = npc.getItems();
    for (ItemStack itemStack : npcInventory) {
      if (!itemStack.isEmpty()) {
        Item item = itemStack.getItem();
        for (Item depositItem : itemsToDeposit) {
          if (item == depositItem) {
            return true;
          }
        }
      }
    }
    return false;
  }

  public void goToChest() {
    this.npc.getNavigation()
      .startMovingTo(
        this.npc.chestPosition.getX(),
        this.npc.chestPosition.getY(),
        this.npc.chestPosition.getZ(),
        0.5f
      );

    if (this.npc.getNavigation().isIdle()) {
      this.npc.getNavigation()
        .startMovingTo(
          this.npc.chestPosition.getX(),
          this.npc.chestPosition.getY(),
          this.npc.chestPosition.getZ(),
          0.5f
        );
    }
  }

  public boolean isBesideChest() {
    BlockPos.Mutable entityPos = new BlockPos.Mutable();
    entityPos.set(this.npc.getX(), this.npc.getY(), this.npc.getZ());
    if (this.npc.chestPosition == null) {
      return false;
    }
    return entityPos.isWithinDistance(this.npc.chestPosition, 1.5f);
  }

  public void depositItemsInChest() {
    ChestBlockEntity chest = (ChestBlockEntity) this.npc.getWorld()
      .getBlockEntity(this.npc.chestPosition);
    inventoryHelper.transferItemsToChest(
      this.npc.getItems(),
      chest,
      itemsToDeposit
    );
  }

  @Override
  public boolean canStart() {
    if (!this.npc.isChestPositionValid()) return false;

    ChestBlockEntity chest = (ChestBlockEntity) this.npc.getWorld()
      .getBlockEntity(this.npc.chestPosition);

    if (chest == null) return false;
    return (
      haveItemsToDeposit() &&
      this.npc.isChestPositionValid() &&
      inventoryHelper.haveSpaceInChestForItems(chest, itemsToDeposit) &&
      !inventoryHelper.isChestFull(chest) &&
      this.npc.currentState == StateEnum.DEPOSITING &&
      isChestStillThere(this.npc.chestPosition)
    );
  }

  public boolean isChestStillThere(BlockPos chestPos) {
    Block chestBlock =
      this.npc.getEntityWorld().getBlockState(chestPos).getBlock();
    return chestBlock != null && chestBlock instanceof ChestBlock;
  }

  @Override
  public void tick() {
    goToChest();

    if (isBesideChest()) {
      this.npc.getNavigation().stop();
      this.npc.getLookControl()
        .lookAt(
          this.npc.chestPosition.getX(),
          this.npc.chestPosition.getY(),
          this.npc.chestPosition.getZ()
        );
      this.npc.swingHand(Hand.MAIN_HAND);
      depositItemsInChest();
    }
  }

  @Override
  public void stop() {
    this.npc.currentState = StateEnum.IDLE;
  }
}
