package me.roundaround.roundalib.observable;

public interface Mapper {
  @FunctionalInterface
  interface P0<T> extends Mapper {
    T apply();
  }

  @FunctionalInterface
  interface P1<S1, T> extends Mapper {
    T apply(S1 s1);
  }

  @FunctionalInterface
  interface P2<S1, S2, T> extends Mapper {
    T apply(S1 s1, S2 s2);
  }

  @FunctionalInterface
  interface P3<S1, S2, S3, T> extends Mapper {
    T apply(S1 s1, S2 s2, S3 s3);
  }

  @FunctionalInterface
  interface P4<S1, S2, S3, S4, T> extends Mapper {
    T apply(S1 s1, S2 s2, S3 s3, S4 s4);
  }

  @FunctionalInterface
  interface P5<S1, S2, S3, S4, S5, T> extends Mapper {
    T apply(
        S1 s1,
        S2 s2,
        S3 s3,
        S4 s4,
        S5 s5);
  }

  @FunctionalInterface
  interface P6<S1, S2, S3, S4, S5, S6, T> extends Mapper {
    T apply(
        S1 s1,
        S2 s2,
        S3 s3,
        S4 s4,
        S5 s5,
        S6 s6);
  }

  @FunctionalInterface
  interface P7<S1, S2, S3, S4, S5, S6, S7, T> extends Mapper {
    T apply(
        S1 s1,
        S2 s2,
        S3 s3,
        S4 s4,
        S5 s5,
        S6 s6,
        S7 s7);
  }

  @FunctionalInterface
  interface P8<S1, S2, S3, S4, S5, S6, S7, S8, T> extends Mapper {
    T apply(
        S1 s1,
        S2 s2,
        S3 s3,
        S4 s4,
        S5 s5,
        S6 s6,
        S7 s7,
        S8 s8);
  }

  @FunctionalInterface
  interface P9<S1, S2, S3, S4, S5, S6, S7, S8, S9, T> extends Mapper {
    T apply(
        S1 s1,
        S2 s2,
        S3 s3,
        S4 s4,
        S5 s5,
        S6 s6,
        S7 s7,
        S8 s8,
        S9 s9);
  }

  @FunctionalInterface
  interface P10<S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, T> extends Mapper {
    T apply(
        S1 s1,
        S2 s2,
        S3 s3,
        S4 s4,
        S5 s5,
        S6 s6,
        S7 s7,
        S8 s8,
        S9 s9,
        S10 s10);
  }

  @FunctionalInterface
  interface P11<S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11, T> extends Mapper {
    T apply(
        S1 s1,
        S2 s2,
        S3 s3,
        S4 s4,
        S5 s5,
        S6 s6,
        S7 s7,
        S8 s8,
        S9 s9,
        S10 s10,
        S11 s11);
  }

  @FunctionalInterface
  interface P12<S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11, S12, T> extends Mapper {
    T apply(
        S1 s1,
        S2 s2,
        S3 s3,
        S4 s4,
        S5 s5,
        S6 s6,
        S7 s7,
        S8 s8,
        S9 s9,
        S10 s10,
        S11 s11,
        S12 s12);
  }
}
