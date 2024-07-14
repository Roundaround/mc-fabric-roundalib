package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.widget.LinearLayoutWidget;
import me.roundaround.roundalib.config.option.ConfigOption;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

public abstract class Control<D, O extends ConfigOption<D>> extends LinearLayoutWidget {
  protected final MinecraftClient client;
  protected final O option;

  protected boolean valid;

  protected Control(MinecraftClient client, O option, int width, int height) {
    super(Axis.HORIZONTAL, width, height);

    this.client = client;
    this.option = option;

    this.spacing(GuiUtil.PADDING / 2);
    this.getMainPositioner().alignRight().alignVerticalCenter();
  }

  public O getOption() {
    return this.option;
  }

  public boolean isValid() {
    return this.valid;
  }

  public void markValid() {
    this.valid = true;
  }

  public void markInvalid() {
    this.valid = false;
  }

  public void tick() {
  }

  protected void update() {
  }

  @Environment(EnvType.CLIENT)
  @FunctionalInterface
  public interface ControlFactory<D, O extends ConfigOption<D>> {
    Control<D, O> create(MinecraftClient client, O option, int width, int height);
  }
}
