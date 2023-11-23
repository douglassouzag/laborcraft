package net.glok.laborcraft;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.glok.laborcraft.entity.ModEntities;
import net.glok.laborcraft.entity.client.ModModelLayers;
import net.glok.laborcraft.entity.client.NPCModel;
import net.glok.laborcraft.entity.client.NPCRenderer;
import net.glok.laborcraft.util.BoxScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class LaborcraftClient implements ClientModInitializer {

  @Override
  public void onInitializeClient() {
    EntityModelLayerRegistry.registerModelLayer(
      ModModelLayers.NPC_ENTITY,
      NPCModel::getTexturedModelData
    );

    EntityRendererRegistry.register(
      ModEntities.LUMBERJACK_NPC_ENTITY,
      NPCRenderer::new
    );

    EntityRendererRegistry.register(
      ModEntities.FARMER_NPC_ENTITY,
      NPCRenderer::new
    );

    HandledScreens.register(Laborcraft.BOX_SCREEN_HANDLER, BoxScreen::new);
  }
}
