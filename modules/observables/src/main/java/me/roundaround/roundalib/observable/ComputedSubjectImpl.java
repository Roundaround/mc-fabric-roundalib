package me.roundaround.roundalib.observable;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;

public class ComputedSubjectImpl<Tin, Tout> extends ComputedImpl<Tin, Tout> implements Subject<Tout> {
  ComputedSubjectImpl(
      Observable<Tin> source,
      Mapper.P1<Tin, Tout> mapper) {
    super(source, mapper);
  }

  ComputedSubjectImpl(
      Function<ComputedImpl<Tin, Tout>, Subscription> subscriptionFactory,
      Supplier<Tout> supplier) {
    super(subscriptionFactory, supplier);
  }

  @Override
  public ComputedSubjectImpl<Tin, Tout> hot() {
    super.hot();
    return this;
  }

  @Override
  public ComputedSubjectImpl<Tin, Tout> cold() {
    super.cold();
    return this;
  }

  @Override
  public ComputedSubjectImpl<Tin, Tout> nonDistinct() {
    super.distinct((a, b) -> Objects.equals(a, b));
    return this;
  }

  @Override
  public ComputedSubjectImpl<Tin, Tout> distinct(BiPredicate<Tout, Tout> equalityPredicate) {
    super.distinct(equalityPredicate);
    return this;
  }

  @Override
  public void set(Tout value) {
    super.set(value);
  }

  @Override
  public void setAndEmit(Tout value) {
    super.setAndEmit(value);
  }

  @Override
  public void setNoEmit(Tout value) {
    super.setNoEmit(value);
  }
}