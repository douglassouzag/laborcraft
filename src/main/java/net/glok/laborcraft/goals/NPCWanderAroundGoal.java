package net.glok.laborcraft.goals;

import java.util.EnumSet;
import net.glok.laborcraft.entity.custom.NPCEntity;
import net.glok.laborcraft.state.StateMachineGoal.StateEnum;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class NPCWanderAroundGoal extends Goal {

  public static final int DEFAULT_CHANCE = 120;
  protected final NPCEntity mob;
  protected double targetX;
  protected double targetY;
  protected double targetZ;
  protected final double speed;
  protected int chance;
  protected boolean ignoringChance;
  private final boolean canDespawn;

  public NPCWanderAroundGoal(NPCEntity mob, double speed) {
    this(mob, speed, 120);
  }

  public NPCWanderAroundGoal(NPCEntity mob, double speed, int chance) {
    this(mob, speed, chance, true);
  }

  public NPCWanderAroundGoal(
    NPCEntity entity,
    double speed,
    int chance,
    boolean canDespawn
  ) {
    this.mob = entity;
    this.speed = speed;
    this.chance = chance;
    this.canDespawn = canDespawn;
    this.setControls(EnumSet.of(Control.MOVE));
  }

  public boolean canStart() {
    if (!(this.mob.currentState == StateEnum.WANDERING)) {
      return false;
    }

    if (this.mob.hasPassengers()) {
      return false;
    } else {
      if (!this.ignoringChance) {
        if (this.canDespawn && this.mob.getDespawnCounter() >= 100) {
          return false;
        }

        if (this.mob.getRandom().nextInt(toGoalTicks(this.chance)) != 0) {
          return false;
        }
      }

      Vec3d vec3d = this.getWanderTarget();
      if (vec3d == null) {
        return false;
      } else {
        this.targetX = vec3d.x;
        this.targetY = vec3d.y;
        this.targetZ = vec3d.z;
        this.ignoringChance = false;
        return true;
      }
    }
  }

  @Nullable
  protected Vec3d getWanderTarget() {
    return NoPenaltyTargeting.find(this.mob, 10, 7);
  }

  public boolean shouldContinue() {
    return !this.mob.getNavigation().isIdle() && !this.mob.hasPassengers();
  }

  public void start() {
    this.mob.getNavigation()
      .startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
  }

  public void stop() {
    this.mob.getNavigation().stop();
    super.stop();
  }

  public void ignoreChanceOnce() {
    this.ignoringChance = true;
  }

  public void setChance(int chance) {
    this.chance = chance;
  }
}
