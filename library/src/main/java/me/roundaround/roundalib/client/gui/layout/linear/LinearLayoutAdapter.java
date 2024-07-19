package me.roundaround.roundalib.client.gui.layout.linear;

import me.roundaround.roundalib.client.gui.util.Alignment;
import me.roundaround.roundalib.client.gui.util.Axis;
import me.roundaround.roundalib.client.gui.util.Spacing;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.Widget;

@Environment(EnvType.CLIENT)
public interface LinearLayoutAdapter extends Widget {
  default Spacing getDefaultLinearLayoutMargin() {
    return Spacing.zero();
  }

  default void onSetLinearLayoutAlignment(Axis flowAxis, Alignment alignment) {
  }

  default void setLinearLayoutX(int x) {
    this.setX(x);
  }

  default void setLinearLayoutY(int y) {
    this.setY(y);
  }
}
