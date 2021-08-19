package me.roundaround.roundalib.config.value;

public interface ListOptionValue<T extends ListOptionValue> {
    String getId();
    String getDisplayString();
    T getFromId(String id);
    T getNext();
    T getPrev();
}
