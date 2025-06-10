package me.roundaround.roundalib.observable;

import java.util.function.BiPredicate;

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

  void setAndEmit(T value);

  void setNoEmit(T value);

  public static <T> Subject<T> of(T initial) {
    return new SubjectImpl<T>(initial);
  }
}
