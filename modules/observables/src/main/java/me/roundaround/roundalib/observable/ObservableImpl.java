package me.roundaround.roundalib.observable;

import java.util.HashMap;
import java.util.Objects;
import java.util.function.BiPredicate;

public class ObservableImpl<T> implements Observable<T> {
  protected static final Object PRESENT = new Object();

  protected final HashMap<Observer.P1<T>, Object> observers = new HashMap<>();

  protected T value;
  protected boolean isHot = true;
  protected BiPredicate<T, T> equals = Objects::equals;

  ObservableImpl(T initial) {
    this.value = initial;
  }

  @Override
  public ObservableImpl<T> hot() {
    this.isHot = true;
    return this;
  }

  @Override
  public ObservableImpl<T> cold() {
    this.isHot = false;
    return this;
  }

  @Override
  public ObservableImpl<T> nonDistinct() {
    this.equals = (a, b) -> false;
    return this;
  }

  @Override
  public ObservableImpl<T> distinct(BiPredicate<T, T> equalityPredicate) {
    this.equals = equalityPredicate;
    return this;
  }

  @Override
  public T get() {
    return this.value;
  }

  @Override
  public void emit() {
    this.observers.keySet().forEach((observer) -> observer.handle(this.value));
  }

  @Override
  public Subscription subscribe(Observer.P0 observer) {
    if (this.isHot) {
      observer.handle();
    }
    return this.subscribe((value) -> observer.handle());
  }

  @Override
  public Subscription subscribe(Observer.P1<T> observer) {
    if (this.isHot) {
      observer.handle(this.value);
    }
    this.observers.put(observer, PRESENT);
    return () -> this.unsubscribe(observer);
  }

  @Override
  public boolean unsubscribe(Observer.P1<T> observer) {
    return this.observers.remove(observer) != null;
  }

  @Override
  public void close() {
    this.observers.clear();
  }

  @Override
  public <S> ComputedImpl<T, S> map(Mapper.P1<T, S> mapper) {
    return new ComputedImpl<T, S>(this, mapper);
  }

  void set(T value) {
    if (this.equals.test(this.value, value)) {
      return;
    }
    this.value = value;
    this.emit();
  }

  void setAndEmit(T value) {
    this.value = value;
    this.emit();
  }

  void setNoEmit(T value) {
    this.value = value;
  }

  public static <T> ObservableImpl<T> of(T initial) {
    return new ObservableImpl<T>(initial);
  }
}
