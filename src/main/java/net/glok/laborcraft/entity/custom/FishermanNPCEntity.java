package net.glok.laborcraft.entity.custom;

import net.glok.laborcraft.Laborcraft;
import net.glok.laborcraft.goals.FishGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class FishermanNPCEntity extends WorkerNPCEntity {

  public FishermanNPCEntity(
    EntityType<? extends PathAwareEntity> entityType,
    World world
  ) {
    super(
      entityType,
      world,
      new Identifier(
        Laborcraft.MOD_ID,
        "textures/entity/profession/fisherman.png"
      )
    );
  }

  @Override
  protected void initGoals() {
    super.initGoals();
    this.goalSelector.add(3, new FishGoal(this));
  }
}
