package me.roundaround.roundalib.config.value;

public interface ListOptionValue {
    String getId();
    String getDisplayString();
    ListOptionValue getFromId(String id);
    ListOptionValue getNext();
    ListOptionValue getPrev();
}
