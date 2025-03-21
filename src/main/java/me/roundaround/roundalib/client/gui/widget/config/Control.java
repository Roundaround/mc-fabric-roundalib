package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.client.gui.util.GuiUtil;
import me.roundaround.roundalib.client.gui.layout.linear.LinearLayoutWidget;
import me.roundaround.roundalib.client.gui.util.Axis;
import me.roundaround.roundalib.config.option.ConfigOption;
import me.roundaround.roundalib.util.Observable;
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

    this.spacing(GuiUtil.PADDING / 2).mainAxisContentAlignEnd().defaultOffAxisContentAlignCenter();
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

  public Observable.Subscription initSubscriptions() {
    return Observable.subscribeToAll(this.getOption().pendingValue, this.getOption().isDisabled, this::update);
  }

  public void tick() {
  }

  protected void update(D value, boolean isDisabled) {
  }

  @Environment(EnvType.CLIENT)
  @FunctionalInterface
  public interface ControlFactory<D, O extends ConfigOption<D>> {
    Control<D, O> create(MinecraftClient client, O option, int width, int height);
  }
}
