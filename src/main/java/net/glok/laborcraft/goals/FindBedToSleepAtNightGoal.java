package net.glok.laborcraft.goals;

import net.glok.laborcraft.entity.custom.NPCEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;

public class FindBedToSleepAtNightGoal extends Goal {

  private final NPCEntity npc;

  public FindBedToSleepAtNightGoal(NPCEntity npc) {
    this.npc = npc;
  }

  private boolean isNightTime() {
    return this.npc.getWorld().isNight();
  }

  private void goToBed(BlockPos bedPos) {
    this.npc.getNavigation()
      .startMovingTo(bedPos.getX(), bedPos.getY(), bedPos.getZ(), 0.5f);

    // Check if the NPC is stuck
    if (this.npc.getNavigation().isIdle()) {
      System.out.println("NPC is stuck");
      this.npc.getNavigation()
        .startMovingTo(bedPos.getX(), bedPos.getY(), bedPos.getZ(), 0.5f);
    }
  }

  private boolean isNearEnoughBed(BlockPos bedPos) {
    BlockPos.Mutable entityPos = new BlockPos.Mutable();
    entityPos.set(this.npc.getX(), this.npc.getY(), this.npc.getZ());
    if (bedPos == null) {
      return false;
    }
    return entityPos.isWithinDistance(bedPos, 1.5f);
  }

  private void layDownInBed(BlockPos bedPos) {
    this.npc.getNavigation().stop();
    this.npc.sleep(bedPos);
  }

  private boolean isSleeping() {
    return this.npc.isSleeping();
  }

  private void wakeUp() {
    this.npc.wakeUp();
  }

  @Override
  public boolean canStart() {
    return isNightTime() && this.npc.bedPosition != null;
  }

  @Override
  public void tick() {
    if (isNearEnoughBed(this.npc.bedPosition)) {
      if (!isSleeping()) {
        layDownInBed(this.npc.bedPosition);
      }
    } else {
      goToBed(this.npc.bedPosition);
    }
  }

  @Override
  public void stop() {
    wakeUp();
  }
}
