package net.glok.laborcraft;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.glok.laborcraft.entity.ModEntities;
import net.glok.laborcraft.entity.custom.DefaultWorkerEntity;
import net.glok.laborcraft.entity.custom.LumberjackEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Laborcraft implements ModInitializer {

  public static final String MOD_ID = "laborcraft";
  public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

  @Override
  public void onInitialize() {
    FabricDefaultAttributeRegistry.register(
      ModEntities.DEFAULT_WORKER,
      DefaultWorkerEntity.createDefaultWorkerAttributes()
    );
    FabricDefaultAttributeRegistry.register(
      ModEntities.LUMBERJACK,
      LumberjackEntity.createDefaultWorkerAttributes()
    );
  }
}
