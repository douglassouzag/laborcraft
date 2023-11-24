package net.glok.laborcraft.entity.client;

import net.glok.laborcraft.entity.custom.NPCEntity;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.feature.StuckArrowsFeatureRenderer;
import net.minecraft.client.render.entity.feature.StuckStingersFeatureRenderer;
import net.minecraft.client.render.entity.model.ArmorEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.util.Identifier;

public class NPCRenderer
  extends MobEntityRenderer<NPCEntity, NPCModel<NPCEntity>> {

  public NPCRenderer(Context context) {
    super(
      context,
      new NPCModel<>(context.getPart(EntityModelLayers.PLAYER), false),
      0.6F
    );
    this.addFeature(
        new HeldItemFeatureRenderer<NPCEntity, NPCModel<NPCEntity>>(
          this,
          context.getHeldItemRenderer()
        )
      );
    this.addFeature(
        new ArmorFeatureRenderer<NPCEntity, NPCModel<NPCEntity>, ArmorEntityModel<NPCEntity>>(
          this,
          new ArmorEntityModel<NPCEntity>(
            context.getPart(EntityModelLayers.PLAYER_INNER_ARMOR)
          ),
          new ArmorEntityModel<NPCEntity>(
            context.getPart(EntityModelLayers.PLAYER_OUTER_ARMOR)
          ),
          context.getModelManager()
        )
      );
    this.addFeature(
        new StuckArrowsFeatureRenderer<NPCEntity, NPCModel<NPCEntity>>(
          context,
          this
        )
      );
    this.addFeature(
        new HeadFeatureRenderer<NPCEntity, NPCModel<NPCEntity>>(
          this,
          context.getModelLoader(),
          context.getHeldItemRenderer()
        )
      );
    this.addFeature(
        new ElytraFeatureRenderer<NPCEntity, NPCModel<NPCEntity>>(
          this,
          context.getModelLoader()
        )
      );
    this.addFeature(
        new StuckStingersFeatureRenderer<NPCEntity, NPCModel<NPCEntity>>(this)
      );
  }

  @Override
  public Identifier getTexture(NPCEntity entity) {
    return entity.texture;
  }
}
