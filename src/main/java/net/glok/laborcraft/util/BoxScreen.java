package net.glok.laborcraft.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BoxScreen extends HandledScreen<BoxScreenHandler> {

  //A path to the gui texture. In this example we use the texture from the dispenser
  private static final Identifier TEXTURE = new Identifier(
    "minecraft",
    "textures/gui/container/shulker_box.png"
  );

  public BoxScreen(
    BoxScreenHandler handler,
    PlayerInventory inventory,
    Text title
  ) {
    super(handler, inventory, title);
  }

  @Override
  protected void init() {
    super.init();
    titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
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

    context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
  }
}
