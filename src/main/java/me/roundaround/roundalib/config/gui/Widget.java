package me.roundaround.roundalib.config.gui;

import java.util.List;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.text.Text;

public interface Widget extends Drawable, Element {
  public default void init() {
  }

  public default void tick() {
  }

  public default <S extends Element & Selectable> List<S> getSelectableElements() {
    return List.of();
  }

  public default List<Text> getTooltip(int mouseX, int mouseY, float delta) {
    return List.of();
  }

  public default boolean onMouseClicked(double mouseX, double mouseY, int button) {
    return false;
  }

  public default boolean onMouseReleased(double mouseX, double mouseY, int button) {
    return false;
  }

  public default boolean onMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
    return false;
  }

  public default boolean onMouseScrolled(double mouseX, double mouseY, double amount) {
    return false;
  }

  public int getWidth();

  public int getHeight();

  public int getTop();

  public int getBottom();

  public int getLeft();

  public int getRight();

  public void moveTop(int top);
}
