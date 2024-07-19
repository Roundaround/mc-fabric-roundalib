package me.roundaround.roundalib.util;

import java.util.*;

public class Observable<T> {
  protected static final Object PRESENT = new Object();
  protected static final Object EMPTY = new Object();

  protected T value;
  protected final WeakHashMap<Observer<T>, Object> observers = new WeakHashMap<>();

  protected Observable(T initial) {
    this.value = initial;
  }

  public static <T> Observable<T> of(T initial) {
    return new Observable<>(initial);
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

  public void clear() {
    this.observers.clear();
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
