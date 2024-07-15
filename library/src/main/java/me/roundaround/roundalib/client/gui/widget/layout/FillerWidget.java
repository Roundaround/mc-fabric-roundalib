package me.roundaround.roundalib.client.gui.widget.layout;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.Widget;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class FillerWidget extends SizableLayoutWidget {
  public FillerWidget(int width, int height) {
    super(width, height);
  }

  public static FillerWidget empty() {
    return new FillerWidget(0, 0);
  }

  public static FillerWidget ofWidth(int width) {
    return new FillerWidget(width, 0);
  }

  public static FillerWidget ofHeight(int height) {
    return new FillerWidget(0, height);
  }

  public static FillerWidget ofSize(int width, int height) {
    return new FillerWidget(width, height);
  }

  @Override
  public void forEachElement(Consumer<Widget> consumer) {
  }

  @Override
  public void forEachChild(Consumer<ClickableWidget> consumer) {
  }
}
