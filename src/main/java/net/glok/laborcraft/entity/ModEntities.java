package net.glok.laborcraft.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.glok.laborcraft.Laborcraft;
import net.glok.laborcraft.entity.custom.FarmerNPCEntity;
import net.glok.laborcraft.entity.custom.LumberjackNPCEntity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {

  public static final EntityType<LumberjackNPCEntity> LUMBERJACK_NPC_ENTITY = Registry.register(
    Registries.ENTITY_TYPE,
    new Identifier(Laborcraft.MOD_ID, "lumberjack_npc"),
    FabricEntityTypeBuilder
      .create(SpawnGroup.CREATURE, LumberjackNPCEntity::new)
      .dimensions(EntityDimensions.fixed(0.6F, 1.95F))
      .build()
  );

  public static final EntityType<FarmerNPCEntity> FARMER_NPC_ENTITY = Registry.register(
    Registries.ENTITY_TYPE,
    new Identifier(Laborcraft.MOD_ID, "farmer_npc"),
    FabricEntityTypeBuilder
      .create(SpawnGroup.CREATURE, FarmerNPCEntity::new)
      .dimensions(EntityDimensions.fixed(0.6F, 1.95F))
      .build()
  );
}
