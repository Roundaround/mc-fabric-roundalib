package me.roundaround.roundalib.event;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Function;

public class EventBus<T> {
  private final Function<List<T>, T> handlerFactory;
  private final LinkedHashSet<T> listeners = new LinkedHashSet<>();

  private T handler;

  public EventBus(Function<List<T>, T> handlerFactory) {
    this.handlerFactory = handlerFactory;
    this.rebuildHandler();
  }

  public void register(T listener) {
    this.listeners.add(listener);
    this.rebuildHandler();
  }

  public T invoker() {
    return this.handler;
  }

  private void rebuildHandler() {
    this.handler = this.handlerFactory.apply(List.copyOf(this.listeners));
  }
}
