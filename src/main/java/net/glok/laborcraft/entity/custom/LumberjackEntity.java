package net.glok.laborcraft.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.AttackGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.world.World;

public class LumberjackEntity extends DefaultWorkerEntity {

  public LumberjackEntity(
    EntityType<? extends DefaultWorkerEntity> entityType,
    World world
  ) {
    super(entityType, world);
    this.occupation = "Lumberjack";

    this.goalSelector.add(0, new AttackGoal(this));
    this.targetSelector.add(
        0,
        new ActiveTargetGoal<ChickenEntity>(
          (MobEntity) this,
          ChickenEntity.class,
          false
        )
      );
  }
}
