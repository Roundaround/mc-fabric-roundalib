package me.roundaround.roundalib.observable;

@FunctionalInterface
public interface Subscription extends AutoCloseable {
  void close();
}
