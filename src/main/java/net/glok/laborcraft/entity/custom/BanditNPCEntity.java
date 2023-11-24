package net.glok.laborcraft.entity.custom;

import net.glok.laborcraft.Laborcraft;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class BanditNPCEntity extends AgressiveNPCEntity {

  public BanditNPCEntity(
    EntityType<? extends PathAwareEntity> entityType,
    World world
  ) {
    super(
      entityType,
      world,
      new Identifier(Laborcraft.MOD_ID, "textures/entity/profession/bandit.png")
    );
  }

  @Override
  protected void initGoals() {
    super.initGoals();
    this.targetSelector.add(
        2,
        new ActiveTargetGoal<PassiveNPCEntity>(
          this,
          PassiveNPCEntity.class,
          true
        )
      );
  }
}
