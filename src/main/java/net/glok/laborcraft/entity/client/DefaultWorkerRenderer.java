package net.glok.laborcraft.entity.client;

import net.glok.laborcraft.Laborcraft;
import net.glok.laborcraft.entity.custom.DefaultWorkerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.ArmorEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class DefaultWorkerRenderer
  extends MobEntityRenderer<DefaultWorkerEntity, PlayerEntityModel<DefaultWorkerEntity>> {

  private static final Identifier TEXTURE = new Identifier(
    Laborcraft.MOD_ID,
    "textures/entity/default_worker.png"
  );

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
  }

  @Override
  public Identifier getTexture(DefaultWorkerEntity entity) {
    return TEXTURE;
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
    // if (mobEntity.isBaby()) {
    //   matrixStack.scale(1f, 1f, 1f);
    // }

    super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
  }
}
