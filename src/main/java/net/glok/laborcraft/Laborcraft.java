package net.glok.laborcraft;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.glok.laborcraft.entity.ModEntities;
import net.glok.laborcraft.entity.custom.DefaultWorkerEntity;
import net.glok.laborcraft.entity.custom.LumberjackEntity;
import net.glok.laborcraft.util.BoxScreenHandler;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Laborcraft implements ModInitializer {

  public static final String MOD_ID = "laborcraft";
  public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
  public static ScreenHandlerType<BoxScreenHandler> BOX_SCREEN_HANDLER;
  public static ScreenHandlerType<BoxScreenHandler> BOX_SCREEN_HANDLER_2;
  public static final Identifier BOX = new Identifier(MOD_ID, "box_block");

  static {
    BOX_SCREEN_HANDLER =
      ScreenHandlerRegistry.registerSimple(BOX, BoxScreenHandler::new);
  }

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
