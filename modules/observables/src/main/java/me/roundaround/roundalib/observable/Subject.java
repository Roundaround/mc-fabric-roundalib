package me.roundaround.roundalib.observable;

import java.util.function.BiPredicate;
import java.util.function.Function;

public interface Subject<T> extends Observable<T> {
  @Override
  Subject<T> hot();

  @Override
  Subject<T> cold();

  @Override
  Subject<T> nonDistinct();

  @Override
  Subject<T> distinct(BiPredicate<T, T> equalityPredicate);

  void set(T value);

  void setWithForceEmit(T value);

  void setWithNoEmit(T value);

  default void update(Function<T, T> updater) {
    this.set(updater.apply(this.get()));
  }

  default void updateWithForceEmit(Function<T, T> updater) {
    this.setWithForceEmit(updater.apply(this.get()));
  }

  default void updateWithNoEmit(Function<T, T> updater) {
    this.setWithNoEmit(updater.apply(this.get()));
  }

  public static <T> Subject<T> of(T initial) {
    return new SubjectImpl<T>(initial);
  }
}
