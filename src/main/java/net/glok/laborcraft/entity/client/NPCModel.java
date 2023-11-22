package net.glok.laborcraft.entity.client;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.LivingEntity;

public class NPCModel<T extends LivingEntity> extends PlayerEntityModel<T> {

  public NPCModel(ModelPart root, boolean thinArms) {
    super(root, thinArms);
  }

  public static TexturedModelData getTexturedModelData() {
    ModelData modelData = PlayerEntityModel.getModelData(Dilation.NONE, 0.0F);

    return TexturedModelData.of(modelData, 64, 64);
  }
}
