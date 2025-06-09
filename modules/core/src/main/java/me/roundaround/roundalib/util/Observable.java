package me.roundaround.roundalib.util;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Observable<T> {
  protected static final Object PRESENT = new Object();
  protected static final Object EMPTY = new Object();

  protected final WeakHashMap<Observer<T>, Object> observers = new WeakHashMap<>();
  protected final HashMap<Observer<T>, Object> hardReferences = new HashMap<>();

  protected T value;
  protected BiFunction<T, T, Boolean> equalityFunction;

  protected Observable(T initial) {
    this(initial, Objects::equals);
  }

  protected Observable(T initial, BiFunction<T, T, Boolean> equalityFunction) {
    this.value = initial;
    this.equalityFunction = equalityFunction;
  }

  public static <T> Observable<T> of(T initial) {
    return new Observable<>(initial);
  }

  public static <T> Observable<T> of(T initial, BiFunction<T, T, Boolean> equalityFunction) {
    return new Observable<>(initial, equalityFunction);
  }

  public static <S, T> Computed<T> computed(
      Observable<S> source, Mapper<S, T> mapper) {
    return computed(source, mapper, SubscribeOptions.create());
  }

  public static <S, T> Computed<T> computed(
      Observable<S> source, Mapper<S, T> mapper, BiFunction<T, T, Boolean> equalityFunction) {
    return computed(source, mapper, equalityFunction, SubscribeOptions.create());
  }

  public static <S, T> Computed<T> computed(
      Observable<S> source, Mapper<S, T> mapper, SubscribeOptions options) {
    Computed<T> computed = new Computed<>(() -> mapper.apply(source.get()));
    computed.sourceSubscription = source.subscribe((value) -> computed.set(mapper.apply(value)), options);
    return computed;
  }

  public static <S, T> Computed<T> computed(
      Observable<S> source, Mapper<S, T> mapper, BiFunction<T, T, Boolean> equalityFunction, SubscribeOptions options) {
    Computed<T> computed = new Computed<>(() -> mapper.apply(source.get()), equalityFunction);
    computed.sourceSubscription = source.subscribe((value) -> computed.set(mapper.apply(value)), options);
    return computed;
  }

  public static <S1, S2, T> Computed<T> computed(
      Observable<S1> source1, Observable<S2> source2, Mapper2<S1, S2, T> mapper) {
    return computed(source1, source2, mapper, SubscribeOptions.create());
  }

  public static <S1, S2, T> Computed<T> computed(
      Observable<S1> source1, Observable<S2> source2, Mapper2<S1, S2, T> mapper,
      BiFunction<T, T, Boolean> equalityFunction) {
    return computed(source1, source2, mapper, equalityFunction, SubscribeOptions.create());
  }

  public static <S1, S2, T> Computed<T> computed(
      Observable<S1> source1, Observable<S2> source2, Mapper2<S1, S2, T> mapper, SubscribeOptions options) {
    Computed<T> computed = new Computed<>(() -> mapper.apply(source1.get(), source2.get()));
    computed.sourceSubscription = subscribeToAll(
        source1, source2, (value1, value2) -> computed.set(mapper.apply(value1, value2)), options);
    return computed;
  }

  public static <S1, S2, T> Computed<T> computed(
      Observable<S1> source1, Observable<S2> source2, Mapper2<S1, S2, T> mapper,
      BiFunction<T, T, Boolean> equalityFunction, SubscribeOptions options) {
    Computed<T> computed = new Computed<>(() -> mapper.apply(source1.get(), source2.get()), equalityFunction);
    computed.sourceSubscription = subscribeToAll(
        source1, source2, (value1, value2) -> computed.set(mapper.apply(value1, value2)), options);
    return computed;
  }

  public static <S1, S2, S3, T> Computed<T> computed(
      Observable<S1> source1, Observable<S2> source2, Observable<S3> source3, Mapper3<S1, S2, S3, T> mapper) {
    return computed(source1, source2, source3, mapper, SubscribeOptions.create());
  }

  public static <S1, S2, S3, T> Computed<T> computed(
      Observable<S1> source1, Observable<S2> source2, Observable<S3> source3, Mapper3<S1, S2, S3, T> mapper,
      BiFunction<T, T, Boolean> equalityFunction) {
    return computed(source1, source2, source3, mapper, equalityFunction, SubscribeOptions.create());
  }

  public static <S1, S2, S3, T> Computed<T> computed(
      Observable<S1> source1,
      Observable<S2> source2,
      Observable<S3> source3,
      Mapper3<S1, S2, S3, T> mapper,
      SubscribeOptions options) {
    Computed<T> computed = new Computed<>(() -> mapper.apply(source1.get(), source2.get(), source3.get()));
    computed.sourceSubscription = subscribeToAll(source1, source2, source3,
        (value1, value2, value3) -> computed.set(mapper.apply(value1, value2, value3)), options);
    return computed;
  }

  public static <S1, S2, S3, T> Computed<T> computed(
      Observable<S1> source1,
      Observable<S2> source2,
      Observable<S3> source3,
      Mapper3<S1, S2, S3, T> mapper,
      BiFunction<T, T, Boolean> equalityFunction,
      SubscribeOptions options) {
    Computed<T> computed = new Computed<>(() -> mapper.apply(source1.get(), source2.get(), source3.get()),
        equalityFunction);
    computed.sourceSubscription = subscribeToAll(source1, source2, source3,
        (value1, value2, value3) -> computed.set(mapper.apply(value1, value2, value3)), options);
    return computed;
  }

  public static <S1, S2, S3, S4, T> Computed<T> computed(
      Observable<S1> source1,
      Observable<S2> source2,
      Observable<S3> source3,
      Observable<S4> source4,
      Mapper4<S1, S2, S3, S4, T> mapper) {
    return computed(source1, source2, source3, source4, mapper, SubscribeOptions.create());
  }

  public static <S1, S2, S3, S4, T> Computed<T> computed(
      Observable<S1> source1,
      Observable<S2> source2,
      Observable<S3> source3,
      Observable<S4> source4,
      Mapper4<S1, S2, S3, S4, T> mapper,
      BiFunction<T, T, Boolean> equalityFunction) {
    return computed(source1, source2, source3, source4, mapper, equalityFunction, SubscribeOptions.create());
  }

  public static <S1, S2, S3, S4, T> Computed<T> computed(
      Observable<S1> source1,
      Observable<S2> source2,
      Observable<S3> source3,
      Observable<S4> source4,
      Mapper4<S1, S2, S3, S4, T> mapper,
      SubscribeOptions options) {
    Computed<T> computed = new Computed<>(
        () -> mapper.apply(source1.get(), source2.get(), source3.get(), source4.get()));
    computed.sourceSubscription = subscribeToAll(source1, source2, source3, source4,
        (value1, value2, value3, value4) -> computed.set(mapper.apply(value1, value2, value3, value4)), options);
    return computed;
  }

  public static <S1, S2, S3, S4, T> Computed<T> computed(
      Observable<S1> source1,
      Observable<S2> source2,
      Observable<S3> source3,
      Observable<S4> source4,
      Mapper4<S1, S2, S3, S4, T> mapper,
      BiFunction<T, T, Boolean> equalityFunction,
      SubscribeOptions options) {
    Computed<T> computed = new Computed<>(
        () -> mapper.apply(source1.get(), source2.get(), source3.get(), source4.get()), equalityFunction);
    computed.sourceSubscription = subscribeToAll(source1, source2, source3, source4,
        (value1, value2, value3, value4) -> computed.set(mapper.apply(value1, value2, value3, value4)), options);
    return computed;
  }

  public static <S1, S2, S3, S4, S5, T> Computed<T> computed(
      Observable<S1> source1,
      Observable<S2> source2,
      Observable<S3> source3,
      Observable<S4> source4,
      Observable<S5> source5,
      Mapper5<S1, S2, S3, S4, S5, T> mapper) {
    return computed(source1, source2, source3, source4, source5, mapper, SubscribeOptions.create());
  }

  public static <S1, S2, S3, S4, S5, T> Computed<T> computed(
      Observable<S1> source1,
      Observable<S2> source2,
      Observable<S3> source3,
      Observable<S4> source4,
      Observable<S5> source5,
      Mapper5<S1, S2, S3, S4, S5, T> mapper,
      BiFunction<T, T, Boolean> equalityFunction) {
    return computed(source1, source2, source3, source4, source5, mapper, equalityFunction, SubscribeOptions.create());
  }

  public static <S1, S2, S3, S4, S5, T> Computed<T> computed(
      Observable<S1> source1,
      Observable<S2> source2,
      Observable<S3> source3,
      Observable<S4> source4,
      Observable<S5> source5,
      Mapper5<S1, S2, S3, S4, S5, T> mapper,
      SubscribeOptions options) {
    Computed<T> computed = new Computed<>(
        () -> mapper.apply(source1.get(), source2.get(), source3.get(), source4.get(), source5.get()));
    computed.sourceSubscription = subscribeToAll(source1, source2, source3, source4, source5,
        (value1, value2, value3, value4, value5) -> computed.set(mapper.apply(value1, value2, value3, value4, value5)),
        options);
    return computed;
  }

  public static <S1, S2, S3, S4, S5, T> Computed<T> computed(
      Observable<S1> source1,
      Observable<S2> source2,
      Observable<S3> source3,
      Observable<S4> source4,
      Observable<S5> source5,
      Mapper5<S1, S2, S3, S4, S5, T> mapper,
      BiFunction<T, T, Boolean> equalityFunction,
      SubscribeOptions options) {
    Computed<T> computed = new Computed<>(
        () -> mapper.apply(source1.get(), source2.get(), source3.get(), source4.get(), source5.get()),
        equalityFunction);
    computed.sourceSubscription = subscribeToAll(source1, source2, source3, source4, source5,
        (value1, value2, value3, value4, value5) -> computed.set(mapper.apply(value1, value2, value3, value4, value5)),
        options);
    return computed;
  }

  public static <S1, S2> Subscription subscribeToAll(
      Observable<S1> source1, Observable<S2> source2, Observer2<S1, S2> observer) {
    return subscribeToAll(source1, source2, observer, SubscribeOptions.create());
  }

  public static <S1, S2> Subscription subscribeToAll(
      Observable<S1> source1, Observable<S2> source2, Observer2<S1, S2> observer, SubscribeOptions options) {
    NoParamObserver commonObserver = () -> observer.handle(source1.get(), source2.get());
    if (options.emitImmediately()) {
      commonObserver.handle();
    }
    List<Subscription> subscriptions = Stream.of(source1, source2)
        .map((observable) -> observable.subscribe(commonObserver,
            options.toBuilder().emittingImmediately(false).build()))
        .toList();
    return () -> subscriptions.forEach(Subscription::unsubscribe);
  }

  public static <S1, S2, S3> Subscription subscribeToAll(
      Observable<S1> source1, Observable<S2> source2, Observable<S3> source3, Observer3<S1, S2, S3> observer) {
    return subscribeToAll(source1, source2, source3, observer, SubscribeOptions.create());
  }

  public static <S1, S2, S3> Subscription subscribeToAll(
      Observable<S1> source1,
      Observable<S2> source2,
      Observable<S3> source3,
      Observer3<S1, S2, S3> observer,
      SubscribeOptions options) {
    NoParamObserver commonObserver = () -> observer.handle(source1.get(), source2.get(), source3.get());
    if (options.emitImmediately()) {
      commonObserver.handle();
    }
    List<Subscription> subscriptions = Stream.of(source1, source2, source3)
        .map((observable) -> observable.subscribe(commonObserver,
            options.toBuilder().emittingImmediately(false).build()))
        .toList();
    return () -> subscriptions.forEach(Subscription::unsubscribe);
  }

  public static <S1, S2, S3, S4> Subscription subscribeToAll(
      Observable<S1> source1,
      Observable<S2> source2,
      Observable<S3> source3,
      Observable<S4> source4,
      Observer4<S1, S2, S3, S4> observer) {
    return subscribeToAll(source1, source2, source3, source4, observer, SubscribeOptions.create());
  }

  public static <S1, S2, S3, S4> Subscription subscribeToAll(
      Observable<S1> source1,
      Observable<S2> source2,
      Observable<S3> source3,
      Observable<S4> source4,
      Observer4<S1, S2, S3, S4> observer,
      SubscribeOptions options) {
    NoParamObserver commonObserver = () -> observer.handle(source1.get(), source2.get(), source3.get(), source4.get());
    if (options.emitImmediately()) {
      commonObserver.handle();
    }
    List<Subscription> subscriptions = Stream.of(source1, source2, source3, source4)
        .map((observable) -> observable.subscribe(commonObserver,
            options.toBuilder().emittingImmediately(false).build()))
        .toList();
    return () -> subscriptions.forEach(Subscription::unsubscribe);
  }

  public static <S1, S2, S3, S4, S5> Subscription subscribeToAll(
      Observable<S1> source1,
      Observable<S2> source2,
      Observable<S3> source3,
      Observable<S4> source4,
      Observable<S5> source5,
      Observer5<S1, S2, S3, S4, S5> observer) {
    return subscribeToAll(source1, source2, source3, source4, source5, observer, SubscribeOptions.create());
  }

  public static <S1, S2, S3, S4, S5> Subscription subscribeToAll(
      Observable<S1> source1,
      Observable<S2> source2,
      Observable<S3> source3,
      Observable<S4> source4,
      Observable<S5> source5,
      Observer5<S1, S2, S3, S4, S5> observer,
      SubscribeOptions options) {
    NoParamObserver commonObserver = () -> observer.handle(
        source1.get(), source2.get(), source3.get(), source4.get(), source5.get());
    if (options.emitImmediately()) {
      commonObserver.handle();
    }
    List<Subscription> subscriptions = Stream.of(source1, source2, source3, source4, source5)
        .map((observable) -> observable.subscribe(commonObserver,
            options.toBuilder().emittingImmediately(false).build()))
        .toList();
    return () -> subscriptions.forEach(Subscription::unsubscribe);
  }

  public T get() {
    return this.value;
  }

  public void set(T value) {
    if (this.equalityFunction.apply(this.value, value)) {
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

  public Subscription subscribe(NoParamObserver observer) {
    return this.subscribe((value) -> observer.handle());
  }

  public Subscription subscribe(NoParamObserver observer, SubscribeOptions options) {
    return this.subscribe((value) -> observer.handle(), SubscribeOptions.create());
  }

  public Subscription subscribe(Observer<T> observer) {
    return this.subscribe(observer, SubscribeOptions.create());
  }

  public Subscription subscribe(Observer<T> observer, SubscribeOptions options) {
    if (options.emitImmediately()) {
      observer.handle(this.value);
    }
    if (options.keepHardReference()) {
      this.hardReferences.put(observer, PRESENT);
    }
    this.observers.put(observer, PRESENT);
    return () -> this.unsubscribe(observer);
  }

  public void unsubscribe(Observer<T> observer) {
    this.observers.remove(observer);
    this.hardReferences.remove(observer);
  }

  public void clear() {
    this.observers.clear();
    this.hardReferences.clear();
  }

  public void setEqualityFunction(BiFunction<T, T, Boolean> equalityFunction) {
    this.equalityFunction = equalityFunction;
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
  public interface Subscription {
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
    protected Subscription sourceSubscription;
    protected Supplier<T> computeHandler;

    private Computed(Supplier<T> computeHandler) {
      this(computeHandler, Objects::equals);
    }

    private Computed(Supplier<T> computeHandler, BiFunction<T, T, Boolean> equalityFunction) {
      super(computeHandler.get(), equalityFunction);
      this.computeHandler = computeHandler;
    }

    public void unsubscribeFromSource() {
      this.sourceSubscription.unsubscribe();
    }

    public void recompute() {
      this.set(this.computeHandler.get());
    }
  }

  public record SubscribeOptions(boolean emitImmediately, boolean keepHardReference) {
    public Builder toBuilder() {
      return new Builder(this);
    }

    public static SubscribeOptions create(boolean emitImmediately, boolean keepHardReference) {
      return new Builder().emittingImmediately(emitImmediately).keepHardReference(keepHardReference).build();
    }

    public static SubscribeOptions notEmittingImmediately() {
      return new Builder().notEmittingImmediately().build();
    }

    public static SubscribeOptions withoutHardReference() {
      return new Builder().withoutHardReference().build();
    }

    public static SubscribeOptions create() {
      return new Builder().build();
    }

    public static Builder builder() {
      return new Builder();
    }

    public static class Builder {
      private boolean emitImmediately = true;
      private boolean keepHardReference = true;

      private Builder() {
      }

      private Builder(SubscribeOptions options) {
        this.emitImmediately = options.emitImmediately();
        this.keepHardReference = options.keepHardReference();
      }

      public Builder notEmittingImmediately() {
        return this.emittingImmediately(false);
      }

      public Builder emittingImmediately(boolean emitImmediately) {
        this.emitImmediately = emitImmediately;
        return this;
      }

      public Builder withoutHardReference() {
        return this.keepHardReference(false);
      }

      public Builder keepHardReference(boolean keepHardReference) {
        this.keepHardReference = keepHardReference;
        return this;
      }

      public SubscribeOptions build() {
        return new SubscribeOptions(this.emitImmediately, this.keepHardReference);
      }
    }
  }
}
