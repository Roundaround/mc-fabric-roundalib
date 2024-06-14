package me.roundaround.roundalib.config;

@FunctionalInterface
public interface PendingValueListener<D> {
  void onPendingValueChange(D value);
}
