package net.glok.laborcraft.goals;

import java.util.Arrays;
import net.glok.laborcraft.entity.custom.LumberjackEntity;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LumberjackGoal extends Goal {

  protected final LumberjackEntity mob;
  protected final World world;
  protected final EntityNavigation entityNavigation;

  private static final Block[] LOG_BLOCKS = new Block[] {
    Blocks.OAK_LOG,
    Blocks.SPRUCE_LOG,
    Blocks.BIRCH_LOG,
    Blocks.JUNGLE_LOG,
    Blocks.ACACIA_LOG,
    Blocks.DARK_OAK_LOG,
    Blocks.CHERRY_LOG,
    Blocks.MANGROVE_LOG,
  };

  public LumberjackGoal(LumberjackEntity mob, World world) {
    this.mob = mob;
    this.world = world;
    this.entityNavigation = mob.getNavigation();
  }

  @Override
  public boolean canStart() {
    if (hasAxe() && findLogBlockInRage(20) != null) {
      return true;
    }

    return false;
  }

  public boolean hasAxe() {
    DefaultedList<ItemStack> inventory = mob.getItems();
    for (ItemStack itemStack : inventory) {
      if (itemStack.getItem().toString().contains("axe")) {
        return true;
      }
    }
    return false;
  }

  public BlockPos findLogBlockInRage(double range) {
    BlockPos pos = this.mob.getBlockPos();
    BlockPos nearestLogBlock = null;
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

          if (Arrays.asList(LOG_BLOCKS).contains(block)) {
            double distance = pos.getManhattanDistance(blockPos);
            if (distance < nearestDistance) {
              nearestDistance = distance;
              nearestLogBlock = blockPos;
            }
          }
        }
      }
    }
    return nearestLogBlock;
  }

  public void chopDownLog(BlockPos blockPos) {
    Block block = this.world.getBlockState(blockPos).getBlock();
    if (Arrays.asList(LOG_BLOCKS).contains(block)) {
      this.world.breakBlock(blockPos, true);
    }
  }

  @Override
  public void tick() {
    BlockPos nearestLogBlock = this.findLogBlockInRage(20);
    if (nearestLogBlock != null) {
      this.entityNavigation.startMovingTo(
          nearestLogBlock.getX(),
          nearestLogBlock.getY(),
          nearestLogBlock.getZ(),
          0.5f
        );

      if (this.entityNavigation.isIdle()) {
        this.chopDownLog(nearestLogBlock);
      }
    }
  }
}
