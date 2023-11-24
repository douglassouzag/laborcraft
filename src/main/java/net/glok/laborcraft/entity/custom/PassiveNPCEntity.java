package net.glok.laborcraft.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public abstract class PassiveNPCEntity extends NPCEntity {

  public PassiveNPCEntity(
    EntityType<? extends PathAwareEntity> entityType,
    World world,
    Identifier texture
  ) {
    super(entityType, world, texture);
  }

  @Override
  protected void initGoals() {
    super.initGoals();

    this.goalSelector.add(0, new SwimGoal(this));
    this.goalSelector.add(1, new EscapeDangerGoal(this, 0.8f));
  }
}
