package me.roundaround.roundalib.config.gui;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class Scrollbar extends AbstractWidget<Scrollable> {
  private final double scrollSpeed;

  private boolean scrolling;
  private double scrollAmount;
  private int maxPosition;

  public Scrollbar(
      Scrollable parent, double scrollSpeed, int top, int left, int height, int width) {
    super(parent, top, left, height, width);
    this.scrollSpeed = scrollSpeed;
  }

  @Override
  public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
    int maxScroll = this.getMaxScroll();
    if (maxScroll <= 0) {
      return;
    }

    RenderSystem.disableTexture();
    RenderSystem.setShader(GameRenderer::getPositionColorShader);

    int handleHeight = (int) ((float) this.height * this.height / this.maxPosition);
    handleHeight = MathHelper.clamp(handleHeight, 32, this.height - 8);

    int handleTop = (int) Math.round(this.scrollAmount) * (this.height - handleHeight) / maxScroll + this.top;
    if (handleTop < this.top) {
      handleTop = this.top;
    }

    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferBuilder = tessellator.getBuffer();

    bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

    // Shadow
    bufferBuilder.vertex(this.left, handleTop + handleHeight - 1, 0).color(128, 128, 128, 255).next();
    bufferBuilder.vertex(this.right, handleTop + handleHeight - 1, 0).color(128, 128, 128, 255).next();
    bufferBuilder.vertex(this.right, handleTop, 0).color(128, 128, 128, 255).next();
    bufferBuilder.vertex(this.left, handleTop, 0).color(128, 128, 128, 255).next();

    // Main face
    bufferBuilder
        .vertex(this.left, handleTop + handleHeight - 2, 0)
        .color(192, 192, 192, 255)
        .next();
    bufferBuilder
        .vertex(this.right - 1, handleTop + handleHeight - 2, 0)
        .color(192, 192, 192, 255)
        .next();
    bufferBuilder.vertex(this.right - 1, handleTop, 0).color(192, 192, 192, 255).next();
    bufferBuilder.vertex(this.left, handleTop, 0).color(192, 192, 192, 255).next();

    tessellator.draw();

    RenderSystem.enableTexture();
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    this.scrolling = button == 0 && this.isMouseOver(mouseX, mouseY);
    return this.scrolling;
  }

  @Override
  public boolean mouseDragged(
      double mouseX, double mouseY, int button, double deltaX, double deltaY) {
    if (button == 0 && this.scrolling) {
      if (mouseY < (double) this.top) {
        this.setScrollAmount(0.0D);
      } else if (mouseY > (double) this.bottom) {
        this.setScrollAmount(this.getMaxScroll());
      } else {
        double percent = Math.max(1, this.getMaxScroll());
        int bottom = this.height;
        int top = MathHelper.clamp(((int) ((float) bottom * bottom / this.maxPosition)), 32, bottom - 8);
        double scaled = Math.max(1, percent / (bottom - top));
        this.setScrollAmount(this.scrollAmount + deltaY * scaled);
      }

      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean onMouseScrolled(double mouseX, double mouseY, double amount) {
    this.setScrollAmount(this.scrollAmount - amount * this.scrollSpeed);
    return true;
  }

  public void setMaxPosition(int maxPosition) {
    this.maxPosition = maxPosition;
  }

  public void scroll(double amount) {
    setScrollAmount(scrollAmount + amount);
  }

  private int getMaxScroll() {
    return Math.max(0, this.maxPosition - (this.height - 4));
  }

  private void setScrollAmount(double amount) {
    this.scrollAmount = MathHelper.clamp(amount, 0, this.getMaxScroll());
    this.parent.setScrollAmount(this.scrollAmount);
  }
}
