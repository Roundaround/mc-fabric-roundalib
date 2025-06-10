package me.roundaround.roundalib.observable;

public interface Computed<Tin, Tout> extends Observable<Tout> {
  void disconnect();

  void recompute();

  @Override
  default void close() {
    this.disconnect();
  }

  public static <T1, Tout> ComputedImpl<T1, Tout> of(
      Observable<T1> source,
      Mapper.P1<T1, Tout> mapper) {
    return new ComputedImpl<T1, Tout>(source, mapper);
  }

  public static <T1, T2, Tout> ComputedImpl<Tuple.P2<T1, T2>, Tout> of(
      Observable<T1> source1,
      Observable<T2> source2,
      Mapper.P2<T1, T2, Tout> mapper) {
    return new ComputedImpl<Tuple.P2<T1, T2>, Tout>(
        (computed) -> Observable.subscribeAll(source1, source2, (v1, v2) -> computed.set(mapper.apply(v1, v2))),
        () -> mapper.apply(source1.get(), source2.get()));
  }

  public static <T1, T2, T3, Tout> ComputedImpl<Tuple.P3<T1, T2, T3>, Tout> of(
      Observable<T1> source1,
      Observable<T2> source2,
      Observable<T3> source3,
      Mapper.P3<T1, T2, T3, Tout> mapper) {
    return new ComputedImpl<Tuple.P3<T1, T2, T3>, Tout>(
        (computed) -> Observable.subscribeAll(source1, source2, source3,
            (v1, v2, v3) -> computed.set(mapper.apply(v1, v2, v3))),
        () -> mapper.apply(source1.get(), source2.get(), source3.get()));
  }

  public static <T1, T2, T3, T4, Tout> ComputedImpl<Tuple.P4<T1, T2, T3, T4>, Tout> of(
      Observable<T1> source1,
      Observable<T2> source2,
      Observable<T3> source3,
      Observable<T4> source4,
      Mapper.P4<T1, T2, T3, T4, Tout> mapper) {
    return new ComputedImpl<Tuple.P4<T1, T2, T3, T4>, Tout>(
        (computed) -> Observable.subscribeAll(source1, source2, source3, source4,
            (v1, v2, v3, v4) -> computed.set(mapper.apply(v1, v2, v3, v4))),
        () -> mapper.apply(source1.get(), source2.get(), source3.get(), source4.get()));
  }

  public static <T1, T2, T3, T4, T5, Tout> ComputedImpl<Tuple.P5<T1, T2, T3, T4, T5>, Tout> of(
      Observable<T1> source1,
      Observable<T2> source2,
      Observable<T3> source3,
      Observable<T4> source4,
      Observable<T5> source5,
      Mapper.P5<T1, T2, T3, T4, T5, Tout> mapper) {
    return new ComputedImpl<Tuple.P5<T1, T2, T3, T4, T5>, Tout>(
        (computed) -> Observable.subscribeAll(source1, source2, source3, source4, source5,
            (v1, v2, v3, v4, v5) -> computed.set(mapper.apply(v1, v2, v3, v4, v5))),
        () -> mapper.apply(source1.get(), source2.get(), source3.get(), source4.get(), source5.get()));
  }

  public static <T1, T2, T3, T4, T5, T6, Tout> ComputedImpl<Tuple.P6<T1, T2, T3, T4, T5, T6>, Tout> of(
      Observable<T1> source1,
      Observable<T2> source2,
      Observable<T3> source3,
      Observable<T4> source4,
      Observable<T5> source5,
      Observable<T6> source6,
      Mapper.P6<T1, T2, T3, T4, T5, T6, Tout> mapper) {
    return new ComputedImpl<Tuple.P6<T1, T2, T3, T4, T5, T6>, Tout>(
        (computed) -> Observable.subscribeAll(source1, source2, source3, source4, source5, source6,
            (v1, v2, v3, v4, v5, v6) -> computed.set(mapper.apply(v1, v2, v3, v4, v5, v6))),
        () -> mapper.apply(source1.get(), source2.get(), source3.get(), source4.get(), source5.get(), source6.get()));
  }

  public static <T1, T2, T3, T4, T5, T6, T7, Tout> ComputedImpl<Tuple.P7<T1, T2, T3, T4, T5, T6, T7>, Tout> of(
      Observable<T1> source1,
      Observable<T2> source2,
      Observable<T3> source3,
      Observable<T4> source4,
      Observable<T5> source5,
      Observable<T6> source6,
      Observable<T7> source7,
      Mapper.P7<T1, T2, T3, T4, T5, T6, T7, Tout> mapper) {
    return new ComputedImpl<Tuple.P7<T1, T2, T3, T4, T5, T6, T7>, Tout>(
        (computed) -> Observable.subscribeAll(source1, source2, source3, source4, source5, source6, source7,
            (v1, v2, v3, v4, v5, v6, v7) -> computed.set(mapper.apply(v1, v2, v3, v4, v5, v6, v7))),
        () -> mapper.apply(source1.get(), source2.get(), source3.get(), source4.get(), source5.get(), source6.get(),
            source7.get()));
  }

  public static <T1, T2, T3, T4, T5, T6, T7, T8, Tout> ComputedImpl<Tuple.P8<T1, T2, T3, T4, T5, T6, T7, T8>, Tout> of(
      Observable<T1> source1,
      Observable<T2> source2,
      Observable<T3> source3,
      Observable<T4> source4,
      Observable<T5> source5,
      Observable<T6> source6,
      Observable<T7> source7,
      Observable<T8> source8,
      Mapper.P8<T1, T2, T3, T4, T5, T6, T7, T8, Tout> mapper) {
    return new ComputedImpl<Tuple.P8<T1, T2, T3, T4, T5, T6, T7, T8>, Tout>(
        (computed) -> Observable.subscribeAll(source1, source2, source3, source4, source5, source6, source7, source8,
            (v1, v2, v3, v4, v5, v6, v7, v8) -> computed.set(mapper.apply(v1, v2, v3, v4, v5, v6, v7, v8))),
        () -> mapper.apply(source1.get(), source2.get(), source3.get(), source4.get(), source5.get(), source6.get(),
            source7.get(), source8.get()));
  }

  public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, Tout> ComputedImpl<Tuple.P9<T1, T2, T3, T4, T5, T6, T7, T8, T9>, Tout> of(
      Observable<T1> source1,
      Observable<T2> source2,
      Observable<T3> source3,
      Observable<T4> source4,
      Observable<T5> source5,
      Observable<T6> source6,
      Observable<T7> source7,
      Observable<T8> source8,
      Observable<T9> source9,
      Mapper.P9<T1, T2, T3, T4, T5, T6, T7, T8, T9, Tout> mapper) {
    return new ComputedImpl<Tuple.P9<T1, T2, T3, T4, T5, T6, T7, T8, T9>, Tout>(
        (computed) -> Observable.subscribeAll(source1, source2, source3, source4, source5, source6, source7, source8,
            source9,
            (v1, v2, v3, v4, v5, v6, v7, v8, v9) -> computed.set(mapper.apply(v1, v2, v3, v4, v5, v6, v7, v8, v9))),
        () -> mapper.apply(source1.get(), source2.get(), source3.get(), source4.get(), source5.get(), source6.get(),
            source7.get(), source8.get(), source9.get()));
  }

  public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, Tout> ComputedImpl<Tuple.P10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>, Tout> of(
      Observable<T1> source1,
      Observable<T2> source2,
      Observable<T3> source3,
      Observable<T4> source4,
      Observable<T5> source5,
      Observable<T6> source6,
      Observable<T7> source7,
      Observable<T8> source8,
      Observable<T9> source9,
      Observable<T10> source10,
      Mapper.P10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, Tout> mapper) {
    return new ComputedImpl<Tuple.P10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>, Tout>(
        (computed) -> Observable.subscribeAll(source1, source2, source3, source4, source5, source6, source7, source8,
            source9, source10,
            (v1, v2, v3, v4, v5, v6, v7, v8, v9, v10) -> computed
                .set(mapper.apply(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10))),
        () -> mapper.apply(source1.get(), source2.get(), source3.get(), source4.get(), source5.get(), source6.get(),
            source7.get(), source8.get(), source9.get(), source10.get()));
  }

  public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, Tout> ComputedImpl<Tuple.P11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>, Tout> of(
      Observable<T1> source1,
      Observable<T2> source2,
      Observable<T3> source3,
      Observable<T4> source4,
      Observable<T5> source5,
      Observable<T6> source6,
      Observable<T7> source7,
      Observable<T8> source8,
      Observable<T9> source9,
      Observable<T10> source10,
      Observable<T11> source11,
      Mapper.P11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, Tout> mapper) {
    return new ComputedImpl<Tuple.P11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>, Tout>(
        (computed) -> Observable.subscribeAll(source1, source2, source3, source4, source5, source6, source7, source8,
            source9, source10, source11,
            (v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11) -> computed
                .set(mapper.apply(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11))),
        () -> mapper.apply(source1.get(), source2.get(), source3.get(), source4.get(), source5.get(), source6.get(),
            source7.get(), source8.get(), source9.get(), source10.get(), source11.get()));
  }

  public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, Tout> ComputedImpl<Tuple.P12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>, Tout> of(
      Observable<T1> source1,
      Observable<T2> source2,
      Observable<T3> source3,
      Observable<T4> source4,
      Observable<T5> source5,
      Observable<T6> source6,
      Observable<T7> source7,
      Observable<T8> source8,
      Observable<T9> source9,
      Observable<T10> source10,
      Observable<T11> source11,
      Observable<T12> source12,
      Mapper.P12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, Tout> mapper) {
    return new ComputedImpl<Tuple.P12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>, Tout>(
        (computed) -> Observable.subscribeAll(source1, source2, source3, source4, source5, source6, source7, source8,
            source9, source10, source11, source12,
            (v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12) -> computed
                .set(mapper.apply(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12))),
        () -> mapper.apply(source1.get(), source2.get(), source3.get(), source4.get(), source5.get(), source6.get(),
            source7.get(), source8.get(), source9.get(), source10.get(), source11.get(), source12.get()));
  }

  public static <T1, Tout> ComputedSubjectImpl<T1, Tout> writable(
      Observable<T1> source,
      Mapper.P1<T1, Tout> mapper) {
    return new ComputedSubjectImpl<T1, Tout>(source, mapper);
  }

  public static <T1, T2, Tout> ComputedSubjectImpl<Tuple.P2<T1, T2>, Tout> writable(
      Observable<T1> source1,
      Observable<T2> source2,
      Mapper.P2<T1, T2, Tout> mapper) {
    return new ComputedSubjectImpl<Tuple.P2<T1, T2>, Tout>(
        (computed) -> Observable.subscribeAll(source1, source2, (v1, v2) -> computed.set(mapper.apply(v1, v2))),
        () -> mapper.apply(source1.get(), source2.get()));
  }

  public static <T1, T2, T3, Tout> ComputedSubjectImpl<Tuple.P3<T1, T2, T3>, Tout> writable(
      Observable<T1> source1,
      Observable<T2> source2,
      Observable<T3> source3,
      Mapper.P3<T1, T2, T3, Tout> mapper) {
    return new ComputedSubjectImpl<Tuple.P3<T1, T2, T3>, Tout>(
        (computed) -> Observable.subscribeAll(source1, source2, source3,
            (v1, v2, v3) -> computed.set(mapper.apply(v1, v2, v3))),
        () -> mapper.apply(source1.get(), source2.get(), source3.get()));
  }

  public static <T1, T2, T3, T4, Tout> ComputedSubjectImpl<Tuple.P4<T1, T2, T3, T4>, Tout> writable(
      Observable<T1> source1,
      Observable<T2> source2,
      Observable<T3> source3,
      Observable<T4> source4,
      Mapper.P4<T1, T2, T3, T4, Tout> mapper) {
    return new ComputedSubjectImpl<Tuple.P4<T1, T2, T3, T4>, Tout>(
        (computed) -> Observable.subscribeAll(source1, source2, source3, source4,
            (v1, v2, v3, v4) -> computed.set(mapper.apply(v1, v2, v3, v4))),
        () -> mapper.apply(source1.get(), source2.get(), source3.get(), source4.get()));
  }

  public static <T1, T2, T3, T4, T5, Tout> ComputedSubjectImpl<Tuple.P5<T1, T2, T3, T4, T5>, Tout> writable(
      Observable<T1> source1,
      Observable<T2> source2,
      Observable<T3> source3,
      Observable<T4> source4,
      Observable<T5> source5,
      Mapper.P5<T1, T2, T3, T4, T5, Tout> mapper) {
    return new ComputedSubjectImpl<Tuple.P5<T1, T2, T3, T4, T5>, Tout>(
        (computed) -> Observable.subscribeAll(source1, source2, source3, source4, source5,
            (v1, v2, v3, v4, v5) -> computed.set(mapper.apply(v1, v2, v3, v4, v5))),
        () -> mapper.apply(source1.get(), source2.get(), source3.get(), source4.get(), source5.get()));
  }

  public static <T1, T2, T3, T4, T5, T6, Tout> ComputedSubjectImpl<Tuple.P6<T1, T2, T3, T4, T5, T6>, Tout> writable(
      Observable<T1> source1,
      Observable<T2> source2,
      Observable<T3> source3,
      Observable<T4> source4,
      Observable<T5> source5,
      Observable<T6> source6,
      Mapper.P6<T1, T2, T3, T4, T5, T6, Tout> mapper) {
    return new ComputedSubjectImpl<Tuple.P6<T1, T2, T3, T4, T5, T6>, Tout>(
        (computed) -> Observable.subscribeAll(source1, source2, source3, source4, source5, source6,
            (v1, v2, v3, v4, v5, v6) -> computed.set(mapper.apply(v1, v2, v3, v4, v5, v6))),
        () -> mapper.apply(source1.get(), source2.get(), source3.get(), source4.get(), source5.get(), source6.get()));
  }

  public static <T1, T2, T3, T4, T5, T6, T7, Tout> ComputedSubjectImpl<Tuple.P7<T1, T2, T3, T4, T5, T6, T7>, Tout> writable(
      Observable<T1> source1,
      Observable<T2> source2,
      Observable<T3> source3,
      Observable<T4> source4,
      Observable<T5> source5,
      Observable<T6> source6,
      Observable<T7> source7,
      Mapper.P7<T1, T2, T3, T4, T5, T6, T7, Tout> mapper) {
    return new ComputedSubjectImpl<Tuple.P7<T1, T2, T3, T4, T5, T6, T7>, Tout>(
        (computed) -> Observable.subscribeAll(source1, source2, source3, source4, source5, source6, source7,
            (v1, v2, v3, v4, v5, v6, v7) -> computed.set(mapper.apply(v1, v2, v3, v4, v5, v6, v7))),
        () -> mapper.apply(source1.get(), source2.get(), source3.get(), source4.get(), source5.get(), source6.get(),
            source7.get()));
  }

  public static <T1, T2, T3, T4, T5, T6, T7, T8, Tout> ComputedSubjectImpl<Tuple.P8<T1, T2, T3, T4, T5, T6, T7, T8>, Tout> writable(
      Observable<T1> source1,
      Observable<T2> source2,
      Observable<T3> source3,
      Observable<T4> source4,
      Observable<T5> source5,
      Observable<T6> source6,
      Observable<T7> source7,
      Observable<T8> source8,
      Mapper.P8<T1, T2, T3, T4, T5, T6, T7, T8, Tout> mapper) {
    return new ComputedSubjectImpl<Tuple.P8<T1, T2, T3, T4, T5, T6, T7, T8>, Tout>(
        (computed) -> Observable.subscribeAll(source1, source2, source3, source4, source5, source6, source7, source8,
            (v1, v2, v3, v4, v5, v6, v7, v8) -> computed.set(mapper.apply(v1, v2, v3, v4, v5, v6, v7, v8))),
        () -> mapper.apply(source1.get(), source2.get(), source3.get(), source4.get(), source5.get(), source6.get(),
            source7.get(), source8.get()));
  }

  public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, Tout> ComputedSubjectImpl<Tuple.P9<T1, T2, T3, T4, T5, T6, T7, T8, T9>, Tout> writable(
      Observable<T1> source1,
      Observable<T2> source2,
      Observable<T3> source3,
      Observable<T4> source4,
      Observable<T5> source5,
      Observable<T6> source6,
      Observable<T7> source7,
      Observable<T8> source8,
      Observable<T9> source9,
      Mapper.P9<T1, T2, T3, T4, T5, T6, T7, T8, T9, Tout> mapper) {
    return new ComputedSubjectImpl<Tuple.P9<T1, T2, T3, T4, T5, T6, T7, T8, T9>, Tout>(
        (computed) -> Observable.subscribeAll(source1, source2, source3, source4, source5, source6, source7, source8,
            source9,
            (v1, v2, v3, v4, v5, v6, v7, v8, v9) -> computed.set(mapper.apply(v1, v2, v3, v4, v5, v6, v7, v8, v9))),
        () -> mapper.apply(source1.get(), source2.get(), source3.get(), source4.get(), source5.get(), source6.get(),
            source7.get(), source8.get(), source9.get()));
  }

  public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, Tout> ComputedSubjectImpl<Tuple.P10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>, Tout> writable(
      Observable<T1> source1,
      Observable<T2> source2,
      Observable<T3> source3,
      Observable<T4> source4,
      Observable<T5> source5,
      Observable<T6> source6,
      Observable<T7> source7,
      Observable<T8> source8,
      Observable<T9> source9,
      Observable<T10> source10,
      Mapper.P10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, Tout> mapper) {
    return new ComputedSubjectImpl<Tuple.P10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>, Tout>(
        (computed) -> Observable.subscribeAll(source1, source2, source3, source4, source5, source6, source7, source8,
            source9, source10,
            (v1, v2, v3, v4, v5, v6, v7, v8, v9, v10) -> computed
                .set(mapper.apply(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10))),
        () -> mapper.apply(source1.get(), source2.get(), source3.get(), source4.get(), source5.get(), source6.get(),
            source7.get(), source8.get(), source9.get(), source10.get()));
  }

  public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, Tout> ComputedSubjectImpl<Tuple.P11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>, Tout> writable(
      Observable<T1> source1,
      Observable<T2> source2,
      Observable<T3> source3,
      Observable<T4> source4,
      Observable<T5> source5,
      Observable<T6> source6,
      Observable<T7> source7,
      Observable<T8> source8,
      Observable<T9> source9,
      Observable<T10> source10,
      Observable<T11> source11,
      Mapper.P11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, Tout> mapper) {
    return new ComputedSubjectImpl<Tuple.P11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>, Tout>(
        (computed) -> Observable.subscribeAll(source1, source2, source3, source4, source5, source6, source7, source8,
            source9, source10, source11,
            (v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11) -> computed
                .set(mapper.apply(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11))),
        () -> mapper.apply(source1.get(), source2.get(), source3.get(), source4.get(), source5.get(), source6.get(),
            source7.get(), source8.get(), source9.get(), source10.get(), source11.get()));
  }

  public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, Tout> ComputedSubjectImpl<Tuple.P12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>, Tout> writable(
      Observable<T1> source1,
      Observable<T2> source2,
      Observable<T3> source3,
      Observable<T4> source4,
      Observable<T5> source5,
      Observable<T6> source6,
      Observable<T7> source7,
      Observable<T8> source8,
      Observable<T9> source9,
      Observable<T10> source10,
      Observable<T11> source11,
      Observable<T12> source12,
      Mapper.P12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, Tout> mapper) {
    return new ComputedSubjectImpl<Tuple.P12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>, Tout>(
        (computed) -> Observable.subscribeAll(source1, source2, source3, source4, source5, source6, source7, source8,
            source9, source10, source11, source12,
            (v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12) -> computed
                .set(mapper.apply(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12))),
        () -> mapper.apply(source1.get(), source2.get(), source3.get(), source4.get(), source5.get(), source6.get(),
            source7.get(), source8.get(), source9.get(), source10.get(), source11.get(), source12.get()));
  }
}
