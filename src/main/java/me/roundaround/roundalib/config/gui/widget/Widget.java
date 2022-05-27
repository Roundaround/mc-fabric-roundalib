package me.roundaround.roundalib.config.gui.widget;

import java.util.List;
import java.util.Optional;

import me.roundaround.roundalib.config.gui.SelectableElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.text.Text;

public interface Widget extends Drawable, Element {
  public default void init() {
  }

  public default void tick() {
  }

  public default List<SelectableElement> getSelectableElements() {
    return List.of();
  }

  public default Optional<SelectableElement> getPrimarySelectableElement() {
    return getSelectableElements().stream().findFirst();
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
