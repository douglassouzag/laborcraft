package net.glok.laborcraft.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.glok.laborcraft.Laborcraft;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class BoxScreen extends HandledScreen<BoxScreenHandler> {

  private static final Identifier TEXTURE = new Identifier(
    Laborcraft.MOD_ID,
    "textures/gui/container/npc_inventory.png"
  );

  private LivingEntity entity;

  public BoxScreen(
    BoxScreenHandler handler,
    PlayerInventory inventory,
    Text title
  ) {
    super(handler, inventory, title);
    this.backgroundHeight = 240;
    this.playerInventoryTitleY = 147;
    this.entity = handler.livingEntity;
  }

  @Override
  protected void drawBackground(
    DrawContext context,
    float delta,
    int mouseX,
    int mouseY
  ) {
    RenderSystem.setShader(GameRenderer::getPositionTexProgram);
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    RenderSystem.setShaderTexture(0, TEXTURE);
    int x = (width - backgroundWidth) / 2;
    int y = (height - backgroundHeight) / 2;

    int i = this.x;
    int j = this.y;

    context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
    drawEntity(
      context,
      i + 33,
      j + 75,
      30,
      (float) (i + 51) - mouseX,
      (float) (j + 75 - 50) - mouseY,
      this.entity
    );
  }

  public static void drawEntity(
    DrawContext context,
    int x,
    int y,
    int size,
    float mouseX,
    float mouseY,
    LivingEntity entity
  ) {
    float f = (float) Math.atan((double) (mouseX / 40.0F));
    float g = (float) Math.atan((double) (mouseY / 40.0F));
    Quaternionf quaternionf = (new Quaternionf()).rotateZ(3.1415927F);
    Quaternionf quaternionf2 =
      (new Quaternionf()).rotateX(g * 20.0F * 0.017453292F);
    quaternionf.mul(quaternionf2);
    float h = entity.bodyYaw;
    float i = entity.getYaw();
    float j = entity.getPitch();
    float k = entity.prevHeadYaw;
    float l = entity.headYaw;
    entity.bodyYaw = 180.0F + f * 20.0F;
    entity.setYaw(180.0F + f * 40.0F);
    entity.setPitch(-g * 20.0F);
    entity.headYaw = entity.getYaw();
    entity.prevHeadYaw = entity.getYaw();
    drawEntity(context, x, y, size, quaternionf, quaternionf2, entity);
    entity.bodyYaw = h;
    entity.setYaw(i);
    entity.setPitch(j);
    entity.prevHeadYaw = k;
    entity.headYaw = l;
  }

  public static void drawEntity(
    DrawContext context,
    int x,
    int y,
    int size,
    Quaternionf quaternionf,
    @Nullable Quaternionf quaternionf2,
    LivingEntity entity
  ) {
    context.getMatrices().push();
    context.getMatrices().translate((double) x, (double) y, 50.0);
    context
      .getMatrices()
      .multiplyPositionMatrix(
        (new Matrix4f()).scaling((float) size, (float) size, (float) (-size))
      );
    context.getMatrices().multiply(quaternionf);
    DiffuseLighting.method_34742();
    EntityRenderDispatcher entityRenderDispatcher = MinecraftClient
      .getInstance()
      .getEntityRenderDispatcher();
    if (quaternionf2 != null) {
      quaternionf2.conjugate();
      entityRenderDispatcher.setRotation(quaternionf2);
    }

    entityRenderDispatcher.setRenderShadows(false);
    RenderSystem.runAsFancy(() -> {
      entityRenderDispatcher.render(
        entity,
        0.0,
        0.0,
        0.0,
        0.0F,
        1.0F,
        context.getMatrices(),
        context.getVertexConsumers(),
        15728880
      );
    });
    context.draw();
    entityRenderDispatcher.setRenderShadows(true);
    context.getMatrices().pop();
    DiffuseLighting.enableGuiDepthLighting();
  }

  @Override
  protected void init() {
    super.init();
    titleX = 62;
    titleY = 12;
  }
}
