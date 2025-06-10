package me.roundaround.roundalib.observable;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;

public class ComputedImpl<Tin, Tout> extends ObservableImpl<Tout> {
  protected Subscription subscription;
  protected Supplier<Tout> supplier;

  ComputedImpl(
      Subscription subscription,
      Supplier<Tout> supplier) {
    this((computed) -> subscription, supplier);
  }

  ComputedImpl(
      Observable<Tin> source,
      Mapper.P1<Tin, Tout> mapper) {
    this(
        (computed) -> source.subscribe((value) -> computed.set(mapper.apply(value))),
        () -> mapper.apply(source.get()));
  }

  ComputedImpl(
      Function<ComputedImpl<Tin, Tout>, Subscription> subscriptionFactory,
      Supplier<Tout> supplier) {
    super(supplier.get());
    this.subscription = subscriptionFactory.apply(this);
    this.supplier = supplier;
  }

  public void disconnect() {
    this.subscription.close();
  }

  public void recompute() {
    this.set(this.supplier.get());
  }

  @Override
  public ComputedImpl<Tin, Tout> hot() {
    super.hot();
    return this;
  }

  @Override
  public ComputedImpl<Tin, Tout> cold() {
    super.cold();
    return this;
  }

  @Override
  public ComputedImpl<Tin, Tout> nonDistinct() {
    super.distinct((a, b) -> Objects.equals(a, b));
    return this;
  }

  @Override
  public ComputedImpl<Tin, Tout> distinct(BiPredicate<Tout, Tout> equalityPredicate) {
    super.distinct(equalityPredicate);
    return this;
  }

  @Override
  public void close() {
    this.disconnect();
    super.close();
  }
}
