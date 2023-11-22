package net.glok.laborcraft;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.glok.laborcraft.entity.ModEntities;
import net.glok.laborcraft.entity.client.DefaultWorkerModel;
import net.glok.laborcraft.entity.client.DefaultWorkerRenderer;
import net.glok.laborcraft.entity.client.ModModelLayers;
import net.glok.laborcraft.entity.client.NPCModel;
import net.glok.laborcraft.entity.client.NPCRenderer;
import net.glok.laborcraft.util.BoxScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class LaborcraftClient implements ClientModInitializer {

  @Override
  public void onInitializeClient() {
    EntityRendererRegistry.register(
      ModEntities.DEFAULT_WORKER,
      DefaultWorkerRenderer::new
    );
    EntityModelLayerRegistry.registerModelLayer(
      ModModelLayers.DEFAULT_WORKER,
      DefaultWorkerModel::getTexturedModelData
    );

    EntityRendererRegistry.register(
      ModEntities.LUMBERJACK,
      DefaultWorkerRenderer::new
    );
    EntityModelLayerRegistry.registerModelLayer(
      ModModelLayers.LUMBERJACK,
      DefaultWorkerModel::getTexturedModelData
    );

    EntityRendererRegistry.register(ModEntities.NPC_ENTITY, NPCRenderer::new);
    EntityModelLayerRegistry.registerModelLayer(
      ModModelLayers.NPC_ENTITY,
      NPCModel::getTexturedModelData
    );

    HandledScreens.register(Laborcraft.BOX_SCREEN_HANDLER, BoxScreen::new);
  }
}
