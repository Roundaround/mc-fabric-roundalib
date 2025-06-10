package me.roundaround.roundalib.observable;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

public interface Observable<T> extends AutoCloseable {
  Observable<T> hot();

  Observable<T> cold();

  Observable<T> nonDistinct();

  Observable<T> distinct(BiPredicate<T, T> equalityPredicate);

  T get();

  void emit();

  Subscription subscribe(Observer.P0 observer);

  Subscription subscribe(Observer.P1<T> observer);

  boolean unsubscribe(Observer.P1<T> observer);

  @Override
  void close();

  <S> Observable<S> map(Mapper.P1<T, S> mapper);

  interface Writable<T> extends Observable<T> {
    void set(T value);

    void setAndEmit(T value);

    void setNoEmit(T value);
  }

  public static <T> Observable<T> of(T initial) {
    return new ObservableImpl<T>(initial);
  }

  public static <S1, S2> ComputedImpl<Void, Tuple.P2<S1, S2>> combine(
      Observable<S1> s1,
      Observable<S2> s2) {
    return new ComputedImpl<Void, Tuple.P2<S1, S2>>(
        (computed) -> {
          return Observable.subscribeAll(s1, s2, (v1, v2) -> {
            computed.set(new Tuple.P2<S1, S2>(v1, v2));
          });
        },
        () -> new Tuple.P2<S1, S2>(s1.get(), s2.get()));
  }

  public static <S1, S2, S3> ComputedImpl<Void, Tuple.P3<S1, S2, S3>> combine(
      Observable<S1> s1,
      Observable<S2> s2,
      Observable<S3> s3) {
    return new ComputedImpl<Void, Tuple.P3<S1, S2, S3>>(
        (computed) -> {
          return Observable.subscribeAll(s1, s2, s3, (v1, v2, v3) -> {
            computed.set(new Tuple.P3<S1, S2, S3>(v1, v2, v3));
          });
        },
        () -> new Tuple.P3<S1, S2, S3>(s1.get(), s2.get(), s3.get()));
  }

  public static <S1, S2, S3, S4> ComputedImpl<Void, Tuple.P4<S1, S2, S3, S4>> combine(
      Observable<S1> s1,
      Observable<S2> s2,
      Observable<S3> s3,
      Observable<S4> s4) {
    return new ComputedImpl<Void, Tuple.P4<S1, S2, S3, S4>>(
        (computed) -> {
          return Observable.subscribeAll(s1, s2, s3, s4, (v1, v2, v3, v4) -> {
            computed.set(new Tuple.P4<S1, S2, S3, S4>(v1, v2, v3, v4));
          });
        },
        () -> new Tuple.P4<S1, S2, S3, S4>(s1.get(), s2.get(), s3.get(), s4.get()));
  }

  public static <S1, S2, S3, S4, S5> ComputedImpl<Void, Tuple.P5<S1, S2, S3, S4, S5>> combine(
      Observable<S1> s1,
      Observable<S2> s2,
      Observable<S3> s3,
      Observable<S4> s4,
      Observable<S5> s5) {
    return new ComputedImpl<Void, Tuple.P5<S1, S2, S3, S4, S5>>(
        (computed) -> {
          return Observable.subscribeAll(
              s1, s2, s3, s4, s5,
              (v1, v2, v3, v4, v5) -> {
                computed.set(new Tuple.P5<S1, S2, S3, S4, S5>(
                    v1, v2, v3, v4, v5));
              });
        },
        () -> new Tuple.P5<S1, S2, S3, S4, S5>(
            s1.get(),
            s2.get(),
            s3.get(),
            s4.get(),
            s5.get()));
  }

  public static <S1, S2, S3, S4, S5, S6> ComputedImpl<Void, Tuple.P6<S1, S2, S3, S4, S5, S6>> combine(
      Observable<S1> s1,
      Observable<S2> s2,
      Observable<S3> s3,
      Observable<S4> s4,
      Observable<S5> s5,
      Observable<S6> s6) {
    return new ComputedImpl<Void, Tuple.P6<S1, S2, S3, S4, S5, S6>>(
        (computed) -> {
          return Observable.subscribeAll(
              s1, s2, s3, s4, s5, s6,
              (v1, v2, v3, v4, v5, v6) -> {
                computed.set(new Tuple.P6<S1, S2, S3, S4, S5, S6>(
                    v1, v2, v3, v4, v5, v6));
              });
        },
        () -> new Tuple.P6<S1, S2, S3, S4, S5, S6>(
            s1.get(),
            s2.get(),
            s3.get(),
            s4.get(),
            s5.get(),
            s6.get()));
  }

  public static <S1, S2, S3, S4, S5, S6, S7> ComputedImpl<Void, Tuple.P7<S1, S2, S3, S4, S5, S6, S7>> combine(
      Observable<S1> s1,
      Observable<S2> s2,
      Observable<S3> s3,
      Observable<S4> s4,
      Observable<S5> s5,
      Observable<S6> s6,
      Observable<S7> s7) {
    return new ComputedImpl<Void, Tuple.P7<S1, S2, S3, S4, S5, S6, S7>>(
        (computed) -> {
          return Observable.subscribeAll(
              s1, s2, s3, s4, s5, s6, s7,
              (v1, v2, v3, v4, v5, v6, v7) -> {
                computed.set(new Tuple.P7<S1, S2, S3, S4, S5, S6, S7>(
                    v1, v2, v3, v4, v5, v6, v7));
              });
        },
        () -> new Tuple.P7<S1, S2, S3, S4, S5, S6, S7>(
            s1.get(),
            s2.get(),
            s3.get(),
            s4.get(),
            s5.get(),
            s6.get(),
            s7.get()));
  }

  public static <S1, S2, S3, S4, S5, S6, S7, S8> ComputedImpl<Void, Tuple.P8<S1, S2, S3, S4, S5, S6, S7, S8>> combine(
      Observable<S1> s1,
      Observable<S2> s2,
      Observable<S3> s3,
      Observable<S4> s4,
      Observable<S5> s5,
      Observable<S6> s6,
      Observable<S7> s7,
      Observable<S8> s8) {
    return new ComputedImpl<Void, Tuple.P8<S1, S2, S3, S4, S5, S6, S7, S8>>(
        (computed) -> {
          return Observable.subscribeAll(
              s1, s2, s3, s4, s5, s6, s7, s8,
              (v1, v2, v3, v4, v5, v6, v7, v8) -> {
                computed.set(new Tuple.P8<S1, S2, S3, S4, S5, S6, S7, S8>(
                    v1, v2, v3, v4, v5, v6, v7, v8));
              });
        },
        () -> new Tuple.P8<S1, S2, S3, S4, S5, S6, S7, S8>(
            s1.get(),
            s2.get(),
            s3.get(),
            s4.get(),
            s5.get(),
            s6.get(),
            s7.get(),
            s8.get()));
  }

  public static <S1, S2, S3, S4, S5, S6, S7, S8, S9> ComputedImpl<Void, Tuple.P9<S1, S2, S3, S4, S5, S6, S7, S8, S9>> combine(
      Observable<S1> s1,
      Observable<S2> s2,
      Observable<S3> s3,
      Observable<S4> s4,
      Observable<S5> s5,
      Observable<S6> s6,
      Observable<S7> s7,
      Observable<S8> s8,
      Observable<S9> s9) {
    return new ComputedImpl<Void, Tuple.P9<S1, S2, S3, S4, S5, S6, S7, S8, S9>>(
        (computed) -> {
          return Observable.subscribeAll(
              s1, s2, s3, s4, s5, s6, s7, s8, s9,
              (v1, v2, v3, v4, v5, v6, v7, v8, v9) -> {
                computed.set(new Tuple.P9<S1, S2, S3, S4, S5, S6, S7, S8, S9>(
                    v1, v2, v3, v4, v5, v6, v7, v8, v9));
              });
        },
        () -> new Tuple.P9<S1, S2, S3, S4, S5, S6, S7, S8, S9>(
            s1.get(),
            s2.get(),
            s3.get(),
            s4.get(),
            s5.get(),
            s6.get(),
            s7.get(),
            s8.get(),
            s9.get()));
  }

  public static <S1, S2, S3, S4, S5, S6, S7, S8, S9, S10> ComputedImpl<Void, Tuple.P10<S1, S2, S3, S4, S5, S6, S7, S8, S9, S10>> combine(
      Observable<S1> s1,
      Observable<S2> s2,
      Observable<S3> s3,
      Observable<S4> s4,
      Observable<S5> s5,
      Observable<S6> s6,
      Observable<S7> s7,
      Observable<S8> s8,
      Observable<S9> s9,
      Observable<S10> s10) {
    return new ComputedImpl<Void, Tuple.P10<S1, S2, S3, S4, S5, S6, S7, S8, S9, S10>>(
        (computed) -> {
          return Observable.subscribeAll(
              s1, s2, s3, s4, s5, s6, s7, s8, s9, s10,
              (v1, v2, v3, v4, v5, v6, v7, v8, v9, v10) -> {
                computed.set(new Tuple.P10<S1, S2, S3, S4, S5, S6, S7, S8, S9, S10>(
                    v1, v2, v3, v4, v5, v6, v7, v8, v9, v10));
              });
        },
        () -> new Tuple.P10<S1, S2, S3, S4, S5, S6, S7, S8, S9, S10>(
            s1.get(),
            s2.get(),
            s3.get(),
            s4.get(),
            s5.get(),
            s6.get(),
            s7.get(),
            s8.get(),
            s9.get(),
            s10.get()));
  }

  public static <S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11> ComputedImpl<Void, Tuple.P11<S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11>> combine(
      Observable<S1> s1,
      Observable<S2> s2,
      Observable<S3> s3,
      Observable<S4> s4,
      Observable<S5> s5,
      Observable<S6> s6,
      Observable<S7> s7,
      Observable<S8> s8,
      Observable<S9> s9,
      Observable<S10> s10,
      Observable<S11> s11) {
    return new ComputedImpl<Void, Tuple.P11<S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11>>(
        (computed) -> {
          return Observable.subscribeAll(
              s1, s2, s3, s4, s5, s6, s7, s8, s9, s10, s11,
              (v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11) -> {
                computed.set(new Tuple.P11<S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11>(
                    v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11));
              });
        },
        () -> new Tuple.P11<S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11>(
            s1.get(),
            s2.get(),
            s3.get(),
            s4.get(),
            s5.get(),
            s6.get(),
            s7.get(),
            s8.get(),
            s9.get(),
            s10.get(),
            s11.get()));
  }

  public static <S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11, S12> ComputedImpl<Void, Tuple.P12<S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11, S12>> combine(
      Observable<S1> s1,
      Observable<S2> s2,
      Observable<S3> s3,
      Observable<S4> s4,
      Observable<S5> s5,
      Observable<S6> s6,
      Observable<S7> s7,
      Observable<S8> s8,
      Observable<S9> s9,
      Observable<S10> s10,
      Observable<S11> s11,
      Observable<S12> s12) {
    return new ComputedImpl<Void, Tuple.P12<S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11, S12>>(
        (computed) -> {
          return Observable.subscribeAll(
              s1, s2, s3, s4, s5, s6, s7, s8, s9, s10, s11, s12,
              (v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12) -> {
                computed.set(new Tuple.P12<S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11, S12>(
                    v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12));
              });
        },
        () -> new Tuple.P12<S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11, S12>(
            s1.get(),
            s2.get(),
            s3.get(),
            s4.get(),
            s5.get(),
            s6.get(),
            s7.get(),
            s8.get(),
            s9.get(),
            s10.get(),
            s11.get(),
            s12.get()));
  }

  public static <S1> Subscription subscribeAll(
      Observable<S1> s1,
      Observer.P1<S1> observer) {
    return s1.subscribe(observer);
  }

  public static <S1, S2> Subscription subscribeAll(
      Observable<S1> s1,
      Observable<S2> s2,
      Observer.P2<S1, S2> observer) {
    Observer.P0 shared = () -> observer.handle(s1.get(), s2.get());
    List<Subscription> subscriptions = Stream.of(s1, s2)
        .map((observable) -> observable.subscribe(shared))
        .toList();
    return () -> subscriptions.forEach(Subscription::close);
  }

  public static <S1, S2, S3> Subscription subscribeAll(
      Observable<S1> s1,
      Observable<S2> s2,
      Observable<S3> s3,
      Observer.P3<S1, S2, S3> observer) {
    Observer.P0 shared = () -> observer.handle(s1.get(), s2.get(), s3.get());
    List<Subscription> subscriptions = Stream.of(s1, s2, s3)
        .map((observable) -> observable.subscribe(shared))
        .toList();
    return () -> subscriptions.forEach(Subscription::close);
  }

  public static <S1, S2, S3, S4> Subscription subscribeAll(
      Observable<S1> s1,
      Observable<S2> s2,
      Observable<S3> s3,
      Observable<S4> s4,
      Observer.P4<S1, S2, S3, S4> observer) {
    Observer.P0 shared = () -> observer.handle(
        s1.get(),
        s2.get(),
        s3.get(),
        s4.get());
    List<Subscription> subscriptions = Stream.of(
        s1,
        s2,
        s3,
        s4)
        .map((observable) -> observable.subscribe(shared))
        .toList();
    return () -> subscriptions.forEach(Subscription::close);
  }

  public static <S1, S2, S3, S4, S5> Subscription subscribeAll(
      Observable<S1> s1,
      Observable<S2> s2,
      Observable<S3> s3,
      Observable<S4> s4,
      Observable<S5> s5,
      Observer.P5<S1, S2, S3, S4, S5> observer) {
    Observer.P0 shared = () -> observer.handle(
        s1.get(),
        s2.get(),
        s3.get(),
        s4.get(),
        s5.get());
    List<Subscription> subscriptions = Stream.of(
        s1,
        s2,
        s3,
        s4,
        s5)
        .map((observable) -> observable.subscribe(shared))
        .toList();
    return () -> subscriptions.forEach(Subscription::close);
  }

  public static <S1, S2, S3, S4, S5, S6> Subscription subscribeAll(
      Observable<S1> s1,
      Observable<S2> s2,
      Observable<S3> s3,
      Observable<S4> s4,
      Observable<S5> s5,
      Observable<S6> s6,
      Observer.P6<S1, S2, S3, S4, S5, S6> observer) {
    Observer.P0 shared = () -> observer.handle(
        s1.get(),
        s2.get(),
        s3.get(),
        s4.get(),
        s5.get(),
        s6.get());
    List<Subscription> subscriptions = Stream.of(
        s1,
        s2,
        s3,
        s4,
        s5,
        s6)
        .map((observable) -> observable.subscribe(shared))
        .toList();
    return () -> subscriptions.forEach(Subscription::close);
  }

  public static <S1, S2, S3, S4, S5, S6, S7> Subscription subscribeAll(
      Observable<S1> s1,
      Observable<S2> s2,
      Observable<S3> s3,
      Observable<S4> s4,
      Observable<S5> s5,
      Observable<S6> s6,
      Observable<S7> s7,
      Observer.P7<S1, S2, S3, S4, S5, S6, S7> observer) {
    Observer.P0 shared = () -> observer.handle(
        s1.get(),
        s2.get(),
        s3.get(),
        s4.get(),
        s5.get(),
        s6.get(),
        s7.get());
    List<Subscription> subscriptions = Stream.of(
        s1,
        s2,
        s3,
        s4,
        s5,
        s6,
        s7)
        .map((observable) -> observable.subscribe(shared))
        .toList();
    return () -> subscriptions.forEach(Subscription::close);
  }

  public static <S1, S2, S3, S4, S5, S6, S7, S8> Subscription subscribeAll(
      Observable<S1> s1,
      Observable<S2> s2,
      Observable<S3> s3,
      Observable<S4> s4,
      Observable<S5> s5,
      Observable<S6> s6,
      Observable<S7> s7,
      Observable<S8> s8,
      Observer.P8<S1, S2, S3, S4, S5, S6, S7, S8> observer) {
    Observer.P0 shared = () -> observer.handle(
        s1.get(),
        s2.get(),
        s3.get(),
        s4.get(),
        s5.get(),
        s6.get(),
        s7.get(),
        s8.get());
    List<Subscription> subscriptions = Stream.of(
        s1,
        s2,
        s3,
        s4,
        s5,
        s6,
        s7,
        s8)
        .map((observable) -> observable.subscribe(shared))
        .toList();
    return () -> subscriptions.forEach(Subscription::close);
  }

  public static <S1, S2, S3, S4, S5, S6, S7, S8, S9> Subscription subscribeAll(
      Observable<S1> s1,
      Observable<S2> s2,
      Observable<S3> s3,
      Observable<S4> s4,
      Observable<S5> s5,
      Observable<S6> s6,
      Observable<S7> s7,
      Observable<S8> s8,
      Observable<S9> s9,
      Observer.P9<S1, S2, S3, S4, S5, S6, S7, S8, S9> observer) {
    Observer.P0 shared = () -> observer.handle(
        s1.get(),
        s2.get(),
        s3.get(),
        s4.get(),
        s5.get(),
        s6.get(),
        s7.get(),
        s8.get(),
        s9.get());
    List<Subscription> subscriptions = Stream.of(
        s1,
        s2,
        s3,
        s4,
        s5,
        s6,
        s7,
        s8,
        s9)
        .map((observable) -> observable.subscribe(shared))
        .toList();
    return () -> subscriptions.forEach(Subscription::close);
  }

  public static <S1, S2, S3, S4, S5, S6, S7, S8, S9, S10> Subscription subscribeAll(
      Observable<S1> s1,
      Observable<S2> s2,
      Observable<S3> s3,
      Observable<S4> s4,
      Observable<S5> s5,
      Observable<S6> s6,
      Observable<S7> s7,
      Observable<S8> s8,
      Observable<S9> s9,
      Observable<S10> s10,
      Observer.P10<S1, S2, S3, S4, S5, S6, S7, S8, S9, S10> observer) {
    Observer.P0 shared = () -> observer.handle(
        s1.get(),
        s2.get(),
        s3.get(),
        s4.get(),
        s5.get(),
        s6.get(),
        s7.get(),
        s8.get(),
        s9.get(),
        s10.get());
    List<Subscription> subscriptions = Stream.of(
        s1,
        s2,
        s3,
        s4,
        s5,
        s6,
        s7,
        s8,
        s9,
        s10)
        .map((observable) -> observable.subscribe(shared))
        .toList();
    return () -> subscriptions.forEach(Subscription::close);
  }

  public static <S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11> Subscription subscribeAll(
      Observable<S1> s1,
      Observable<S2> s2,
      Observable<S3> s3,
      Observable<S4> s4,
      Observable<S5> s5,
      Observable<S6> s6,
      Observable<S7> s7,
      Observable<S8> s8,
      Observable<S9> s9,
      Observable<S10> s10,
      Observable<S11> s11,
      Observer.P11<S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11> observer) {
    Observer.P0 shared = () -> observer.handle(
        s1.get(),
        s2.get(),
        s3.get(),
        s4.get(),
        s5.get(),
        s6.get(),
        s7.get(),
        s8.get(),
        s9.get(),
        s10.get(),
        s11.get());
    List<Subscription> subscriptions = Stream.of(
        s1,
        s2,
        s3,
        s4,
        s5,
        s6,
        s7,
        s8,
        s9,
        s10,
        s11)
        .map((observable) -> observable.subscribe(shared))
        .toList();
    return () -> subscriptions.forEach(Subscription::close);
  }

  public static <S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11, S12> Subscription subscribeAll(
      Observable<S1> s1,
      Observable<S2> s2,
      Observable<S3> s3,
      Observable<S4> s4,
      Observable<S5> s5,
      Observable<S6> s6,
      Observable<S7> s7,
      Observable<S8> s8,
      Observable<S9> s9,
      Observable<S10> s10,
      Observable<S11> s11,
      Observable<S12> s12,
      Observer.P12<S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11, S12> observer) {
    Observer.P0 shared = () -> observer.handle(
        s1.get(),
        s2.get(),
        s3.get(),
        s4.get(),
        s5.get(),
        s6.get(),
        s7.get(),
        s8.get(),
        s9.get(),
        s10.get(),
        s11.get(),
        s12.get());
    List<Subscription> subscriptions = Stream.of(
        s1,
        s2,
        s3,
        s4,
        s5,
        s6,
        s7,
        s8,
        s9,
        s10,
        s11,
        s12)
        .map((observable) -> observable.subscribe(shared))
        .toList();
    return () -> subscriptions.forEach(Subscription::close);
  }
}
