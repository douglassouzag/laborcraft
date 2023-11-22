package net.glok.laborcraft.entity.client;

import net.glok.laborcraft.entity.custom.DefaultWorkerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.feature.StuckArrowsFeatureRenderer;
import net.minecraft.client.render.entity.feature.StuckStingersFeatureRenderer;
import net.minecraft.client.render.entity.model.ArmorEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class DefaultWorkerRenderer
  extends MobEntityRenderer<DefaultWorkerEntity, PlayerEntityModel<DefaultWorkerEntity>> {

  public DefaultWorkerRenderer(EntityRendererFactory.Context context) {
    super(
      context,
      new PlayerEntityModel<>(context.getPart(EntityModelLayers.PLAYER), false),
      0.6F
    );
    this.addFeature(
        new HeldItemFeatureRenderer<DefaultWorkerEntity, PlayerEntityModel<DefaultWorkerEntity>>(
          this,
          context.getHeldItemRenderer()
        )
      );
    this.addFeature(
        new ArmorFeatureRenderer<DefaultWorkerEntity, PlayerEntityModel<DefaultWorkerEntity>, ArmorEntityModel<DefaultWorkerEntity>>(
          this,
          new ArmorEntityModel<DefaultWorkerEntity>(
            context.getPart(EntityModelLayers.PLAYER_INNER_ARMOR)
          ),
          new ArmorEntityModel<DefaultWorkerEntity>(
            context.getPart(EntityModelLayers.PLAYER_OUTER_ARMOR)
          ),
          context.getModelManager()
        )
      );
    this.addFeature(
        new StuckArrowsFeatureRenderer<DefaultWorkerEntity, PlayerEntityModel<DefaultWorkerEntity>>(
          context,
          this
        )
      );
    this.addFeature(
        new HeadFeatureRenderer<DefaultWorkerEntity, PlayerEntityModel<DefaultWorkerEntity>>(
          this,
          context.getModelLoader(),
          context.getHeldItemRenderer()
        )
      );
    this.addFeature(
        new ElytraFeatureRenderer<DefaultWorkerEntity, PlayerEntityModel<DefaultWorkerEntity>>(
          this,
          context.getModelLoader()
        )
      );
    this.addFeature(
        new StuckStingersFeatureRenderer<DefaultWorkerEntity, PlayerEntityModel<DefaultWorkerEntity>>(
          this
        )
      );
  }

  @Override
  public Identifier getTexture(DefaultWorkerEntity entity) {
    return entity.skin;
  }

  @Override
  public void render(
    DefaultWorkerEntity mobEntity,
    float f,
    float g,
    MatrixStack matrixStack,
    VertexConsumerProvider vertexConsumerProvider,
    int i
  ) {
    super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
  }
}
