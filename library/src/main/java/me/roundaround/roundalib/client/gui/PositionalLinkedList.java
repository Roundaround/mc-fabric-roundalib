package me.roundaround.roundalib.client.gui;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class PositionalLinkedList<E extends PositionalEntry> implements Iterable<E> {
  private final LinkedList<E> entries = new LinkedList<>();
  private int totalHeight;
  private double averageItemHeight;

  public List<E> asList() {
    return List.copyOf(this.entries);
  }

  public void add(E entry) {
    this.entries.add(entry);
    this.totalHeight += entry.getHeight();
    this.averageItemHeight = (double) this.totalHeight / (double) this.entries.size();
  }

  public E get(int index) {
    return this.entries.get(index);
  }

  public int size() {
    return this.entries.size();
  }

  public boolean isEmpty() {
    return this.entries.isEmpty();
  }

  public void clear() {
    this.entries.clear();

    this.totalHeight = 0;
    this.averageItemHeight = 0;
  }

  public int getTotalHeight() {
    return this.totalHeight;
  }

  public double getAverageItemHeight() {
    return this.averageItemHeight;
  }

  public E getEntryAtPosition(double y) {
    for (E entry : this.entries) {
      if (y >= entry.getTop() && y <= entry.getBottom()) {
        return entry;
      }
    }

    return null;
  }

  public int indexOf(E entry) {
    return this.entries.indexOf(entry);
  }

  @Override
  public Iterator<E> iterator() {
    return this.entries.iterator();
  }
}
