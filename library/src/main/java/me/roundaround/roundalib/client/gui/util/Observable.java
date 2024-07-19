package me.roundaround.roundalib.client.gui.util;

import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public class Observable<T> {
  protected T value;
  protected final LinkedHashMap<UUID, Consumer<T>> subscribers = new LinkedHashMap<>();

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

  public UUID subscribe(UUID refId, Consumer<T> callback) {
    this.subscribers.put(refId, callback);
    return refId;
  }

  public UUID subscribe(Consumer<T> callback) {
    return this.subscribe(this.getRandomUUID(), callback);
  }

  public boolean unsubscribe(UUID refId) {
    boolean exists = this.subscribers.containsKey(refId);
    this.subscribers.remove(refId);
    return exists;
  }

  public void clear() {
    this.subscribers.clear();
  }

  public void emit() {
    this.subscribers.values().forEach((callback) -> callback.accept(this.value));
  }

  protected UUID getRandomUUID() {
    UUID uuid = UUID.randomUUID();
    while (this.subscribers.containsKey(uuid)) {
      uuid = UUID.randomUUID();
    }
    return uuid;
  }
}
