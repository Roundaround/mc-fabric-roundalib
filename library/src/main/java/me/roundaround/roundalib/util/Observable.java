package me.roundaround.roundalib.util;

import java.util.List;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Observable<T> {
  protected static final Object PRESENT = new Object();
  protected static final Object EMPTY = new Object();

  protected final WeakHashMap<Observer<T>, Object> observers = new WeakHashMap<>();

  protected T value;

  protected Observable(T initial) {
    this.value = initial;
  }

  public static <T> Observable<T> of(T initial) {
    return new Observable<>(initial);
  }

  public static <S, T> Computed<T> computed(Observable<S> source, Mapper<S, T> mapper) {
    Computed<T> computed = Computed.of(() -> mapper.apply(source.get()));
    computed.sourceUnsubscriber = source.subscribe((value) -> computed.set(mapper.apply(value)));
    return computed;
  }

  public static <S1, S2, T> Computed<T> computed(
      Observable<S1> source1, Observable<S2> source2, Mapper2<S1, S2, T> mapper
  ) {
    Computed<T> computed = Computed.of(() -> mapper.apply(source1.get(), source2.get()));
    computed.sourceUnsubscriber = subscribeToAll(
        source1, source2, false, (value1, value2) -> computed.set(mapper.apply(value1, value2)));
    return computed;
  }

  public static <S1, S2, S3, T> Computed<T> computed(
      Observable<S1> source1, Observable<S2> source2, Observable<S3> source3, Mapper3<S1, S2, S3, T> mapper
  ) {
    Computed<T> computed = Computed.of(() -> mapper.apply(source1.get(), source2.get(), source3.get()));
    computed.sourceUnsubscriber = subscribeToAll(source1, source2, source3, false,
        (value1, value2, value3) -> computed.set(mapper.apply(value1, value2, value3))
    );
    return computed;
  }

  public static <S1, S2, S3, S4, T> Computed<T> computed(
      Observable<S1> source1,
      Observable<S2> source2,
      Observable<S3> source3,
      Observable<S4> source4,
      Mapper4<S1, S2, S3, S4, T> mapper
  ) {
    Computed<T> computed = Computed.of(() -> mapper.apply(source1.get(), source2.get(), source3.get(), source4.get()));
    computed.sourceUnsubscriber = subscribeToAll(source1, source2, source3, source4, false,
        (value1, value2, value3, value4) -> computed.set(mapper.apply(value1, value2, value3, value4))
    );
    return computed;
  }

  public static <S1, S2, S3, S4, S5, T> Computed<T> computed(
      Observable<S1> source1,
      Observable<S2> source2,
      Observable<S3> source3,
      Observable<S4> source4,
      Observable<S5> source5,
      Mapper5<S1, S2, S3, S4, S5, T> mapper
  ) {
    Computed<T> computed = Computed.of(
        () -> mapper.apply(source1.get(), source2.get(), source3.get(), source4.get(), source5.get()));
    computed.sourceUnsubscriber = subscribeToAll(source1, source2, source3, source4, source5, false,
        (value1, value2, value3, value4, value5) -> computed.set(mapper.apply(value1, value2, value3, value4, value5))
    );
    return computed;
  }

  public static <S1, S2> Unsubscriber subscribeToAll(
      Observable<S1> source1, Observable<S2> source2, Observer2<S1, S2> observer
  ) {
    return subscribeToAll(source1, source2, true, observer);
  }

  public static <S1, S2> Unsubscriber subscribeToAll(
      Observable<S1> source1, Observable<S2> source2, boolean runImmediately, Observer2<S1, S2> observer
  ) {
    NoParamObserver commonObserver = () -> observer.handle(source1.get(), source2.get());
    if (runImmediately) {
      commonObserver.handle();
    }
    List<Unsubscriber> unsubscribers = Stream.of(source1, source2)
        .map((observable) -> observable.subscribe(commonObserver))
        .toList();
    return () -> unsubscribers.forEach(Unsubscriber::unsubscribe);
  }

  public static <S1, S2, S3> Unsubscriber subscribeToAll(
      Observable<S1> source1, Observable<S2> source2, Observable<S3> source3, Observer3<S1, S2, S3> observer
  ) {
    return subscribeToAll(source1, source2, source3, true, observer);
  }

  public static <S1, S2, S3> Unsubscriber subscribeToAll(
      Observable<S1> source1,
      Observable<S2> source2,
      Observable<S3> source3,
      boolean runImmediately,
      Observer3<S1, S2, S3> observer
  ) {
    NoParamObserver commonObserver = () -> observer.handle(source1.get(), source2.get(), source3.get());
    if (runImmediately) {
      commonObserver.handle();
    }
    List<Unsubscriber> unsubscribers = Stream.of(source1, source2, source3)
        .map((observable) -> observable.subscribe(commonObserver))
        .toList();
    return () -> unsubscribers.forEach(Unsubscriber::unsubscribe);
  }

  public static <S1, S2, S3, S4> Unsubscriber subscribeToAll(
      Observable<S1> source1,
      Observable<S2> source2,
      Observable<S3> source3,
      Observable<S4> source4,
      Observer4<S1, S2, S3, S4> observer
  ) {
    return subscribeToAll(source1, source2, source3, source4, true, observer);
  }

  public static <S1, S2, S3, S4> Unsubscriber subscribeToAll(
      Observable<S1> source1,
      Observable<S2> source2,
      Observable<S3> source3,
      Observable<S4> source4,
      boolean runImmediately,
      Observer4<S1, S2, S3, S4> observer
  ) {
    NoParamObserver commonObserver = () -> observer.handle(source1.get(), source2.get(), source3.get(), source4.get());
    if (runImmediately) {
      commonObserver.handle();
    }
    List<Unsubscriber> unsubscribers = Stream.of(source1, source2, source3, source4)
        .map((observable) -> observable.subscribe(commonObserver))
        .toList();
    return () -> unsubscribers.forEach(Unsubscriber::unsubscribe);
  }

  public static <S1, S2, S3, S4, S5> Unsubscriber subscribeToAll(
      Observable<S1> source1,
      Observable<S2> source2,
      Observable<S3> source3,
      Observable<S4> source4,
      Observable<S5> source5,
      Observer5<S1, S2, S3, S4, S5> observer
  ) {
    return subscribeToAll(source1, source2, source3, source4, source5, true, observer);
  }

  public static <S1, S2, S3, S4, S5> Unsubscriber subscribeToAll(
      Observable<S1> source1,
      Observable<S2> source2,
      Observable<S3> source3,
      Observable<S4> source4,
      Observable<S5> source5,
      boolean runImmediately,
      Observer5<S1, S2, S3, S4, S5> observer
  ) {
    NoParamObserver commonObserver = () -> observer.handle(
        source1.get(), source2.get(), source3.get(), source4.get(), source5.get());
    if (runImmediately) {
      commonObserver.handle();
    }
    List<Unsubscriber> unsubscribers = Stream.of(source1, source2, source3, source4, source5)
        .map((observable) -> observable.subscribe(commonObserver))
        .toList();
    return () -> unsubscribers.forEach(Unsubscriber::unsubscribe);
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

  public Unsubscriber subscribe(boolean runImmediately, NoParamObserver observer) {
    if (runImmediately) {
      observer.handle();
    }
    return this.subscribe((value) -> observer.handle());
  }

  public Unsubscriber subscribe(NoParamObserver observer) {
    return this.subscribe(true, observer);
  }

  public Unsubscriber subscribe(boolean runImmediately, Observer<T> observer) {
    if (runImmediately) {
      observer.handle(this.value);
    }
    this.observers.put(observer, PRESENT);
    return () -> this.unsubscribe(observer);
  }

  public Unsubscriber subscribe(Observer<T> observer) {
    return this.subscribe(true, observer);
  }

  public void unsubscribe(Observer<T> observer) {
    this.observers.remove(observer);
  }

  public void clear() {
    this.observers.clear();
  }

  @FunctionalInterface
  public interface NoParamObserver {
    void handle();
  }

  @FunctionalInterface
  public interface Observer<T> {
    void handle(T value);
  }

  @FunctionalInterface
  public interface Observer2<T1, T2> {
    void handle(T1 value1, T2 value2);
  }

  @FunctionalInterface
  public interface Observer3<T1, T2, T3> {
    void handle(T1 value1, T2 value2, T3 value3);
  }

  @FunctionalInterface
  public interface Observer4<T1, T2, T3, T4> {
    void handle(T1 value1, T2 value2, T3 value3, T4 value4);
  }

  @FunctionalInterface
  public interface Observer5<T1, T2, T3, T4, T5> {
    void handle(T1 value1, T2 value2, T3 value3, T4 value4, T5 value5);
  }

  @FunctionalInterface
  public interface Unsubscriber {
    void unsubscribe();
  }

  @FunctionalInterface
  public interface Mapper<S, T> {
    T apply(S source);
  }

  @FunctionalInterface
  public interface Mapper2<S1, S2, T> {
    T apply(S1 source1, S2 source2);
  }

  @FunctionalInterface
  public interface Mapper3<S1, S2, S3, T> {
    T apply(S1 source1, S2 source2, S3 source3);
  }

  @FunctionalInterface
  public interface Mapper4<S1, S2, S3, S4, T> {
    T apply(S1 source1, S2 source2, S3 source3, S4 source4);
  }

  @FunctionalInterface
  public interface Mapper5<S1, S2, S3, S4, S5, T> {
    T apply(S1 source1, S2 source2, S3 source3, S4 source4, S5 source5);
  }

  public static class Computed<T> extends Observable<T> {
    protected Unsubscriber sourceUnsubscriber;
    protected Supplier<T> computeHandler;

    private Computed(Supplier<T> computeHandler) {
      super(computeHandler.get());
      this.computeHandler = computeHandler;
    }

    private static <T> Computed<T> of(Supplier<T> computeHandler) {
      return new Computed<>(computeHandler);
    }

    public void unsubscribeFromSource() {
      this.sourceUnsubscriber.unsubscribe();
    }

    public void recompute() {
      this.set(this.computeHandler.get());
    }
  }
}
