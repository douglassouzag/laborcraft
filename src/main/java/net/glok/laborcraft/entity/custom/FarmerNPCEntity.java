package net.glok.laborcraft.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class FarmerNPCEntity extends NPCWorkerEntity {

  public FarmerNPCEntity(
    EntityType<? extends PathAwareEntity> entityType,
    World world
  ) {
    super(entityType, world);
  }

  protected void initGoals() {
    this.goalSelector.add(1, new SwimGoal(this));
    this.goalSelector.add(2, new MeleeAttackGoal(this, 0.6f, true));
    this.goalSelector.add(
        3,
        new LookAtEntityGoal(this, PlayerEntity.class, 8.0F)
      );
    this.goalSelector.add(4, new LookAroundGoal(this));

    this.targetSelector.add(
        1,
        (new RevengeGoal(this, new Class[0])).setGroupRevenge(new Class[0])
      );

    this.targetSelector.add(
        2,
        new ActiveTargetGoal<>(this, AbstractSkeletonEntity.class, false)
      );
    this.targetSelector.add(
        3,
        new ActiveTargetGoal<>(this, ZombieEntity.class, false)
      );
    this.targetSelector.add(
        4,
        new ActiveTargetGoal<>(this, SpiderEntity.class, false)
      );
  }
}
