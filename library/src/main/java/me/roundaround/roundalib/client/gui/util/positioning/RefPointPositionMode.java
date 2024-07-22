package me.roundaround.roundalib.client.gui.util.positioning;

import me.roundaround.roundalib.client.gui.util.Alignment;
import me.roundaround.roundalib.util.Observable;
import net.minecraft.client.gui.widget.Widget;

public class RefPointPositionMode<T extends Widget> implements PositionMode {
  private final T widget;
  private final Observable<Integer> refX;
  private final Observable<Integer> refY;
  private final Observable<Alignment> alignX = Observable.of(Alignment.START);
  private final Observable<Alignment> alignY = Observable.of(Alignment.START);
  private final Observable.Computed<Integer> absX;
  private final Observable.Computed<Integer> absY;

  public RefPointPositionMode(T widget) {
    this(widget, 0, 0);
  }

  public RefPointPositionMode(T widget, int initialRefX, int initialRefY) {
    this.widget = widget;
    this.refX = Observable.of(initialRefX);
    this.refY = Observable.of(initialRefY);

    this.absX = Observable.computed(
        this.refX, this.alignX, (Integer refX, Alignment alignX) -> alignX.getPos(refX, this.widget.getWidth()));
    this.absY = Observable.computed(
        this.refY, this.alignY, (Integer refY, Alignment alignY) -> alignY.getPos(refY, this.widget.getHeight()));

    this.absX.subscribe(this.widget::setX);
    this.absY.subscribe(this.widget::setY);
  }

  @Override
  public int getX() {
    return this.widget.getX();
  }

  @Override
  public int getY() {
    return this.widget.getY();
  }

  @Override
  public void setX(int x) {
    this.refX.set(x);
  }

  @Override
  public void setY(int y) {
    this.refY.set(y);
  }

  public void setAlignX(Alignment alignX) {
    this.alignX.set(alignX);
  }

  public void setAlignY(Alignment alignY) {
    this.alignY.set(alignY);
  }

  public int getRefX() {
    return this.refX.get();
  }

  public int getRefY() {
    return this.refY.get();
  }

  public int getAbsX() {
    return this.absX.get();
  }

  public int getAbsY() {
    return this.absY.get();
  }

  public void widthChanged() {
    this.absX.recompute();
  }

  public void heightChanged() {
    this.absY.recompute();
  }
}
