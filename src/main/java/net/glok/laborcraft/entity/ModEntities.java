package net.glok.laborcraft.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.glok.laborcraft.Laborcraft;
import net.glok.laborcraft.entity.custom.DefaultWorkerEntity;
import net.glok.laborcraft.entity.custom.LumberjackEntity;
import net.glok.laborcraft.entity.custom.NPCEntity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {

  public static final EntityType<DefaultWorkerEntity> DEFAULT_WORKER = Registry.register(
    Registries.ENTITY_TYPE,
    new Identifier(Laborcraft.MOD_ID, "default_worker"),
    FabricEntityTypeBuilder
      .create(SpawnGroup.CREATURE, DefaultWorkerEntity::new)
      .dimensions(EntityDimensions.fixed(0.6F, 1.95F))
      .build()
  );

  public static final EntityType<LumberjackEntity> LUMBERJACK = Registry.register(
    Registries.ENTITY_TYPE,
    new Identifier(Laborcraft.MOD_ID, "lumberjack"),
    FabricEntityTypeBuilder
      .create(SpawnGroup.CREATURE, LumberjackEntity::new)
      .dimensions(EntityDimensions.fixed(0.6F, 1.95F))
      .build()
  );

  public static final EntityType<NPCEntity> NPC_ENTITY = Registry.register(
    Registries.ENTITY_TYPE,
    new Identifier(Laborcraft.MOD_ID, "npc"),
    FabricEntityTypeBuilder
      .create(SpawnGroup.CREATURE, NPCEntity::new)
      .dimensions(EntityDimensions.fixed(0.6F, 1.95F))
      .build()
  );
}
