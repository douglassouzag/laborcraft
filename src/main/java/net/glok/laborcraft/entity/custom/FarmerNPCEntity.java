package net.glok.laborcraft.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.world.World;

public class FarmerNPCEntity extends WorkerNPCEntity {

  public FarmerNPCEntity(
    EntityType<? extends PathAwareEntity> entityType,
    World world
  ) {
    super(entityType, world);
  }
}
