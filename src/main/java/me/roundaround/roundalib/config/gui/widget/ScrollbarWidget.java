package me.roundaround.roundalib.config.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;

import me.roundaround.roundalib.config.gui.Scrollable;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class ScrollbarWidget extends AbstractWidget<Scrollable> {
  private final double scrollSpeed;

  private boolean scrolling;
  private double scrollAmount;
  private int maxPosition;

  public ScrollbarWidget(
      Scrollable parent, double scrollSpeed, int top, int left, int height, int width) {
    super(parent, top, left, height, width);
    this.scrollSpeed = scrollSpeed;
  }

  @Override
  public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
    int maxScroll = getMaxScroll();
    if (maxScroll <= 0) {
      return;
    }

    RenderSystem.disableTexture();
    RenderSystem.setShader(GameRenderer::getPositionColorProgram);

    int handleHeight = (int) ((float) height * height / maxPosition);
    handleHeight = MathHelper.clamp(handleHeight, 32, height - 8);

    int handleTop = (int) Math.round(scrollAmount) * (height - handleHeight) / maxScroll + top;
    if (handleTop < top) {
      handleTop = top;
    }

    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferBuilder = tessellator.getBuffer();

    bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

    // Shadow
    bufferBuilder.vertex(left, handleTop + handleHeight - 1, 0).color(128, 128, 128, 255).next();
    bufferBuilder.vertex(right, handleTop + handleHeight - 1, 0).color(128, 128, 128, 255).next();
    bufferBuilder.vertex(right, handleTop, 0).color(128, 128, 128, 255).next();
    bufferBuilder.vertex(left, handleTop, 0).color(128, 128, 128, 255).next();

    // Main face
    bufferBuilder
        .vertex(left, handleTop + handleHeight - 2, 0)
        .color(192, 192, 192, 255)
        .next();
    bufferBuilder
        .vertex(right - 1, handleTop + handleHeight - 2, 0)
        .color(192, 192, 192, 255)
        .next();
    bufferBuilder.vertex(right - 1, handleTop, 0).color(192, 192, 192, 255).next();
    bufferBuilder.vertex(left, handleTop, 0).color(192, 192, 192, 255).next();

    tessellator.draw();

    RenderSystem.enableTexture();
  }

  @Override
  public boolean onMouseClicked(double mouseX, double mouseY, int button) {
    scrolling = button == 0;
    return scrolling;
  }

  @Override
  public boolean mouseReleased(double mouseX, double mouseY, int button) {
    if (button == 0) {
      scrolling = false;
    }
    return super.mouseReleased(mouseX, mouseY, button);
  }

  @Override
  public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
    if (!scrolling || button != 0) {
      return false;
    }

    if (mouseY < top) {
      setScrollAmount(0);
    } else if (mouseY > bottom) {
      setScrollAmount(getMaxScroll());
    } else {
      double percent = Math.max(1, getMaxScroll());
      int bottom = height;
      int top = MathHelper.clamp(((int) ((float) bottom * bottom / maxPosition)), 32, bottom - 8);
      double scaled = Math.max(1, percent / (bottom - top));
      setScrollAmount(scrollAmount + deltaY * scaled);
    }

    return true;
  }

  @Override
  public boolean onMouseScrolled(double mouseX, double mouseY, double amount) {
    setScrollAmount(scrollAmount - amount * scrollSpeed);
    return true;
  }

  public void setMaxPosition(int maxPosition) {
    this.maxPosition = maxPosition;
  }

  public void scroll(double amount) {
    setScrollAmount(scrollAmount + amount);
  }

  private int getMaxScroll() {
    return Math.max(0, maxPosition - (height - 4));
  }

  private void setScrollAmount(double amount) {
    scrollAmount = MathHelper.clamp(amount, 0, getMaxScroll());
    parent.setScrollAmount(scrollAmount);
  }
}
