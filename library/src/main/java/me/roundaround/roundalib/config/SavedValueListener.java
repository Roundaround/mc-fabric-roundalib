package me.roundaround.roundalib.config;

@FunctionalInterface
public interface SavedValueListener<D> {
  void onSavedValueChange(D value);
}
