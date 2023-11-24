package net.glok.laborcraft.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public abstract class AgressiveNPCEntity extends NPCEntity {

  public AgressiveNPCEntity(
    EntityType<? extends PathAwareEntity> entityType,
    World world,
    Identifier texture
  ) {
    super(entityType, world, texture);
  }
}
