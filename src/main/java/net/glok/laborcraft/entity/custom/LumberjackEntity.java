package net.glok.laborcraft.entity.custom;

import net.glok.laborcraft.goals.LumberjackGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.AttackGoal;
import net.minecraft.world.World;

public class LumberjackEntity extends DefaultWorkerEntity {

  public LumberjackEntity(
    EntityType<? extends DefaultWorkerEntity> entityType,
    World world
  ) {
    super(entityType, world);
    this.occupation = "Lumberjack";

    this.goalSelector.add(0, new AttackGoal(this));

    this.goalSelector.add(1, new LumberjackGoal(this, world));
  }
}
