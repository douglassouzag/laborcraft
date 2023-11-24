package net.glok.laborcraft.goals;

import net.minecraft.entity.ai.NavigationConditions;
import net.minecraft.entity.ai.goal.DoorInteractGoal;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;

public class SmartDoorInteractGoal extends DoorInteractGoal {

  private final boolean delayedClose;
  private int ticksLeft;

  public SmartDoorInteractGoal(MobEntity mob, boolean delayedClose) {
    super(mob);
    this.mob = mob;
    this.delayedClose = delayedClose;
  }

  @Override
  public boolean shouldContinue() {
    return this.delayedClose && this.ticksLeft > 0 && super.shouldContinue();
  }

  @Override
  public void start() {
    this.ticksLeft = 30;
    this.setDoorOpen(true);
  }

  @Override
  public void stop() {
    if (mob.getRandom().nextBoolean()) this.setDoorOpen(false);
  }

  @Override
  public boolean canStart() {
    if (
      !NavigationConditions.hasMobNavigation(this.mob) ||
      !this.mob.horizontalCollision
    ) return false;

    MobNavigation mobNavigation = (MobNavigation) this.mob.getNavigation();
    Path path = mobNavigation.getCurrentPath();
    if (
      path == null || path.isFinished() || !mobNavigation.canEnterOpenDoors()
    ) return false;

    for (
      int i = 0;
      i < Math.min(path.getCurrentNodeIndex() + 2, path.getLength());
      ++i
    ) {
      PathNode pathNode = path.getNode(i);
      this.doorPos = new BlockPos(pathNode.x, pathNode.y + 1, pathNode.z);
      if (
        this.mob.squaredDistanceTo(
            this.doorPos.getX(),
            this.mob.getY(),
            this.doorPos.getZ()
          ) >
        2.0
      ) continue;
      this.doorValid = true;
      return true;
    }
    this.doorPos = this.mob.getBlockPos().up();
    this.doorValid = true;
    return true;
  }

  @Override
  public void tick() {
    --this.ticksLeft;
    super.tick();
  }
}
