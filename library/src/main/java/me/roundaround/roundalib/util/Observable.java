package me.roundaround.roundalib.util;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.function.Function;

public class Observable<T> {
  protected static final Object PRESENT = new Object();
  protected static final Object EMPTY = new Object();

  protected final WeakHashMap<Observer<T>, Object> observers = new WeakHashMap<>();

  protected T value;
  protected Unsubscriber sourceUnsubscriber;

  protected Observable(T initial) {
    this.value = initial;
  }

  public static <T> Observable<T> of(T initial) {
    return new Observable<>(initial);
  }

  public static <S, T> Observable<T> computed(Observable<S> source, Function<S, T> compute) {
    return source.computed(compute);
  }

  public static Unsubscriber subscribeToAll(Callback callback, Observable<?>... observables) {
    List<Unsubscriber> unsubscribers = Arrays.stream(observables)
        .map((observable) -> observable.subscribe((value) -> callback.handle()))
        .toList();
    return () -> {
      unsubscribers.forEach(Unsubscriber::unsubscribe);
    };
  }

  public T get() {
    return this.value;
  }

  public void set(T value) {
    if (Objects.equals(this.value, value)) {
      return;
    }

    this.value = value;
    this.emit();
  }

  public void setAndEmit(T value) {
    this.value = value;
    this.emit();
  }

  public void setNoEmit(T value) {
    this.value = value;
  }

  public void emit() {
    this.observers.keySet().forEach((observer) -> observer.handle(this.value));
  }

  public Unsubscriber subscribe(Observer<T> observer) {
    this.observers.put(observer, PRESENT);
    return () -> this.unsubscribe(observer);
  }

  public void unsubscribe(Observer<T> observer) {
    this.observers.remove(observer);
  }

  public Unsubscriber getSourceUnsubscriber() {
    return this.sourceUnsubscriber;
  }

  public void clear() {
    this.observers.clear();
  }

  public <S> Observable<S> computed(Function<T, S> compute) {
    Observable<S> computed = Observable.of(compute.apply(this.get()));
    computed.sourceUnsubscriber = this.subscribe((value) -> computed.set(compute.apply(value)));
    return computed;
  }

  @FunctionalInterface
  public interface Observer<T> {
    void handle(T value);
  }

  @FunctionalInterface
  public interface Callback {
    void handle();
  }

  @FunctionalInterface
  public interface Unsubscriber {
    void unsubscribe();
  }
}
