package net.glok.laborcraft.entity.custom;

import net.glok.laborcraft.Laborcraft;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class MercenaryNPCEntity extends WorkerNPCEntity {

  public MercenaryNPCEntity(
    EntityType<? extends PathAwareEntity> entityType,
    World world
  ) {
    super(
      entityType,
      world,
      new Identifier(
        Laborcraft.MOD_ID,
        "textures/entity/profession/mercenary.png"
      )
    );
  }
}
