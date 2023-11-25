package net.glok.laborcraft.goals;

import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.ai.NavigationConditions;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;

public class SmartDoorInteractGoal extends Goal {

  protected MobEntity mob;
  protected BlockPos doorPos;
  protected boolean doorValid;

  private final boolean delayedClose;
  private int ticksLeft;

  public SmartDoorInteractGoal(MobEntity mob, boolean delayedClose) {
    this.doorPos = BlockPos.ORIGIN;
    this.mob = mob;
    this.delayedClose = delayedClose;
  }

  protected boolean isDoorOpen() {
    if (!this.doorValid) {
      return false;
    } else {
      BlockState blockState = this.mob.getWorld().getBlockState(this.doorPos);
      if (!(blockState.getBlock() instanceof DoorBlock)) {
        this.doorValid = false;
        return false;
      } else {
        return (Boolean) blockState.get(DoorBlock.OPEN);
      }
    }
  }

  protected void setDoorOpen(boolean open) {
    if (this.doorValid) {
      BlockState blockState = this.mob.getWorld().getBlockState(this.doorPos);
      if (blockState.getBlock() instanceof DoorBlock) {
        ((DoorBlock) blockState.getBlock()).setOpen(
            this.mob,
            this.mob.getWorld(),
            blockState,
            this.doorPos,
            open
          );
      }
    }
  }

  public boolean canStart() {
    if (!NavigationConditions.hasMobNavigation(this.mob)) {
      return false;
    } else if (!this.mob.horizontalCollision) {
      return false;
    } else {
      MobNavigation mobNavigation = (MobNavigation) this.mob.getNavigation();
      Path path = mobNavigation.getCurrentPath();
      if (
        path != null && !path.isFinished() && mobNavigation.canEnterOpenDoors()
      ) {
        for (
          int i = 0;
          i < Math.min(path.getCurrentNodeIndex() + 2, path.getLength());
          ++i
        ) {
          PathNode pathNode = path.getNode(i);
          this.doorPos = new BlockPos(pathNode.x, pathNode.y + 1, pathNode.z);
          if (
            !(
              this.mob.squaredDistanceTo(
                  (double) this.doorPos.getX(),
                  this.mob.getY(),
                  (double) this.doorPos.getZ()
                ) >
              2.25
            )
          ) {
            this.doorValid =
              DoorBlock.canOpenByHand(this.mob.getWorld(), this.doorPos);
            if (this.doorValid) {
              return true;
            }
          }
        }

        this.doorPos = this.mob.getBlockPos().up();
        this.doorValid =
          DoorBlock.canOpenByHand(this.mob.getWorld(), this.doorPos);
        return this.doorValid;
      } else {
        return false;
      }
    }
  }

  public boolean shouldRunEveryTick() {
    return true;
  }

  public boolean shouldContinue() {
    return this.delayedClose && this.ticksLeft > 0 && super.shouldContinue();
  }

  public void start() {
    this.ticksLeft = 20;
    this.setDoorOpen(true);
  }

  public void stop() {
    this.setDoorOpen(false);
  }

  public void tick() {
    --this.ticksLeft;
  }
}
