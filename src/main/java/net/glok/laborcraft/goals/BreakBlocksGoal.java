package net.glok.laborcraft.goals;

import java.util.Arrays;
import net.glok.laborcraft.entity.custom.DefaultWorkerEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public class BreakBlocksGoal extends Goal {

  protected final DefaultWorkerEntity mob;
  protected final EntityNavigation entityNavigation;
  protected final World world;
  protected final DefaultedList<ItemStack> inventory;

  protected final Block[] blocksToBreak;

  protected final Item[] workTools;

  public BreakBlocksGoal(
    DefaultWorkerEntity mob,
    Block[] blocksToBreak,
    Item[] workTools
  ) {
    this.mob = mob;
    this.entityNavigation = mob.getNavigation();
    this.world = mob.getWorld();
    this.inventory = mob.getItems();
    this.blocksToBreak = blocksToBreak;
    this.workTools = workTools;
  }

  public boolean hasWorkTool() {
    for (ItemStack item : inventory) {
      for (Item tool : workTools) {
        if (item.getItem() == tool && !isToolLowDurability(item)) {
          return true;
        }
      }
    }
    return false;
  }

  public boolean isToolLowDurability(ItemStack item) {
    return item.getDamage() > item.getMaxDamage() - 10;
  }

  public void unequipCurrentTool() {
    mob.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
  }

  public Item equipBestWorkTool() {
    Item bestWorkTool = null;
    int bestDurability = 0;

    for (ItemStack item : inventory) {
      if (isWorkTool(item)) {
        int durability = item.getMaxDamage() - item.getDamage();
        if (bestWorkTool == null || durability > bestDurability) {
          bestWorkTool = item.getItem();
          bestDurability = durability;
        }
      }
    }

    if (bestWorkTool != null) {
      mob.equipStack(EquipmentSlot.MAINHAND, new ItemStack(bestWorkTool));
    }

    return bestWorkTool;
  }

  private boolean isWorkTool(ItemStack item) {
    for (Item tool : workTools) {
      if (item.getItem() == tool) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean canStart() {
    return hasWorkTool();
  }

  public BlockPos searchForNearestBlockToBreakInRange(double range) {
    BlockPos pos = this.mob.getBlockPos();
    BlockPos nearestBlockToBreak = null;
    double nearestDistance = Double.MAX_VALUE;
    for (
      int x = (int) pos.getX() - (int) range;
      x < pos.getX() + (int) range;
      x++
    ) {
      for (
        int y = (int) pos.getY() - (int) range;
        y < pos.getY() + (int) range;
        y++
      ) {
        for (
          int z = (int) pos.getZ() - (int) range;
          z < pos.getZ() + (int) range;
          z++
        ) {
          BlockPos blockPos = new BlockPos(x, y, z);
          Block block = this.world.getBlockState(blockPos).getBlock();

          if (
            Arrays.asList(blocksToBreak).contains(block) &&
            isBlockInWorkChunk(blockPos)
          ) {
            double distance = pos.getManhattanDistance(blockPos);
            if (distance < nearestDistance) {
              nearestDistance = distance;
              nearestBlockToBreak = blockPos;
            }
          }
        }
      }
    }
    return nearestBlockToBreak;
  }

  private boolean isBlockInWorkChunk(BlockPos blockPos) {
    ChunkPos chunkPos = new ChunkPos(blockPos);
    return chunkPos.equals(this.mob.workChunk);
  }

  private void goTo(BlockPos blockPos) {
    entityNavigation.startMovingTo(
      blockPos.getX(),
      blockPos.getY(),
      blockPos.getZ(),
      0.5f
    );
  }

  public boolean isNearEnough(BlockPos blockPos) {
    return this.mob.getBlockPos().getManhattanDistance(blockPos) < 3.5f;
  }

  public void damageWorkTool() {
    ItemStack item = mob.getEquippedStack(EquipmentSlot.MAINHAND);
    for (ItemStack itemStack : inventory) {
      if (itemStack.getItem() == item.getItem()) {
        itemStack.damage(
          1,
          mob,
          entity -> {
            entity.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
          }
        );
      }
    }
  }

  public void breakBlockWithTool(BlockPos blockPos) {
    this.world.breakBlock(blockPos, true, mob);
    damageWorkTool();
  }

  @Override
  public void tick() {
    if (this.mob.boss != null) return;
    equipBestWorkTool();

    BlockPos blockToBreakPos = searchForNearestBlockToBreakInRange(16f);

    if (blockToBreakPos != null) {
      goTo(blockToBreakPos);

      if (isNearEnough(blockToBreakPos)) {
        breakBlockWithTool(blockToBreakPos);
      }
    }
  }

  @Override
  public void stop() {
    unequipCurrentTool();
  }
}
