package net.glok.laborcraft.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.glok.laborcraft.Laborcraft;
import net.glok.laborcraft.entity.custom.BanditNPCEntity;
import net.glok.laborcraft.entity.custom.ButcherNPCEntity;
import net.glok.laborcraft.entity.custom.FarmerNPCEntity;
import net.glok.laborcraft.entity.custom.GuardNPCEntity;
import net.glok.laborcraft.entity.custom.LumberjackNPCEntity;
import net.glok.laborcraft.entity.custom.MercenaryNPCEntity;
import net.glok.laborcraft.entity.custom.MinerNPCEntity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {

  private static final EntityDimensions npcDimensions = EntityDimensions.fixed(
    0.6F,
    1.95F
  );

  private static final SpawnGroup npcSpawnGroup = SpawnGroup.CREATURE;

  public static final EntityType<LumberjackNPCEntity> LUMBERJACK_NPC_ENTITY = Registry.register(
    Registries.ENTITY_TYPE,
    new Identifier(Laborcraft.MOD_ID, "lumberjack_npc"),
    FabricEntityTypeBuilder
      .create(npcSpawnGroup, LumberjackNPCEntity::new)
      .dimensions(npcDimensions)
      .build()
  );

  public static final EntityType<FarmerNPCEntity> FARMER_NPC_ENTITY = Registry.register(
    Registries.ENTITY_TYPE,
    new Identifier(Laborcraft.MOD_ID, "farmer_npc"),
    FabricEntityTypeBuilder
      .create(npcSpawnGroup, FarmerNPCEntity::new)
      .dimensions(npcDimensions)
      .build()
  );

  public static final EntityType<MinerNPCEntity> MINER_NPC_ENTITY = Registry.register(
    Registries.ENTITY_TYPE,
    new Identifier(Laborcraft.MOD_ID, "miner_npc"),
    FabricEntityTypeBuilder
      .create(npcSpawnGroup, MinerNPCEntity::new)
      .dimensions(npcDimensions)
      .build()
  );

  public static final EntityType<GuardNPCEntity> GUARD_NPC_ENTITY = Registry.register(
    Registries.ENTITY_TYPE,
    new Identifier(Laborcraft.MOD_ID, "guard_npc"),
    FabricEntityTypeBuilder
      .create(npcSpawnGroup, GuardNPCEntity::new)
      .dimensions(npcDimensions)
      .build()
  );
  public static final EntityType<MercenaryNPCEntity> MERCENARY_NPC_ENTITY = Registry.register(
    Registries.ENTITY_TYPE,
    new Identifier(Laborcraft.MOD_ID, "mercenary_npc"),
    FabricEntityTypeBuilder
      .create(npcSpawnGroup, MercenaryNPCEntity::new)
      .dimensions(npcDimensions)
      .build()
  );
  public static final EntityType<BanditNPCEntity> BANDIT_NPC_ENTITY = Registry.register(
    Registries.ENTITY_TYPE,
    new Identifier(Laborcraft.MOD_ID, "bandit_npc"),
    FabricEntityTypeBuilder
      .create(npcSpawnGroup, BanditNPCEntity::new)
      .dimensions(npcDimensions)
      .build()
  );
}
