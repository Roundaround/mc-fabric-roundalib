package me.roundaround.roundalib.client.gui.util;

import java.util.Objects;
import java.util.WeakHashMap;
import java.util.function.Consumer;

public class Observable<T> {
  protected static final Object PRESENT = new Object();

  protected T value;
  protected final WeakHashMap<Consumer<T>, Object> subscribers = new WeakHashMap<>();

  protected Observable(T initial) {
    this.value = initial;
  }

  public static <T> Observable<T> of(T initial) {
    return new Observable<>(initial);
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
    this.subscribers.keySet().forEach((callback) -> callback.accept(this.value));
  }

  public void subscribe(Consumer<T> callback) {
    this.subscribers.put(callback, PRESENT);
  }

  public void unsubscribe(Consumer<T> callback) {
    this.subscribers.remove(callback);
  }

  public void clear() {
    this.subscribers.clear();
  }
}
