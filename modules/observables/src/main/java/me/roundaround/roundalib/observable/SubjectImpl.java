package me.roundaround.roundalib.observable;

import java.util.Objects;
import java.util.function.BiPredicate;

public class SubjectImpl<T> extends ObservableImpl<T> implements Subject<T> {
  SubjectImpl(T initial) {
    super(initial);
  }

  @Override
  public SubjectImpl<T> hot() {
    super.hot();
    return this;
  }

  @Override
  public SubjectImpl<T> cold() {
    super.cold();
    return this;
  }

  @Override
  public SubjectImpl<T> nonDistinct() {
    super.distinct((a, b) -> Objects.equals(a, b));
    return this;
  }

  @Override
  public SubjectImpl<T> distinct(BiPredicate<T, T> equalityPredicate) {
    super.distinct(equalityPredicate);
    return this;
  }

  @Override
  public void set(T value) {
    super.set(value);
  }

  @Override
  public void setWithForceEmit(T value) {
    super.setWithForceEmit(value);
  }

  @Override
  public void setWithNoEmit(T value) {
    super.setWithNoEmit(value);
  }

  public Observable<T> readOnly() {
    return new ComputedImpl<T, T>(this, (value) -> value);
  }

  public static <T> SubjectImpl<T> of(T initial) {
    return new SubjectImpl<T>(initial);
  }
}
