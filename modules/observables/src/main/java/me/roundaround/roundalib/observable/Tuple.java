package me.roundaround.roundalib.observable;

public interface Tuple {
  public record P2<T1, T2>(T1 t1, T2 t2) implements Tuple {
  }

  public record P3<T1, T2, T3>(T1 t1, T2 t2, T3 t3) implements Tuple {
  }

  public record P4<T1, T2, T3, T4>(T1 t1, T2 t2, T3 t3, T4 t4) implements Tuple {
  }

  public record P5<T1, T2, T3, T4, T5>(
      T1 t1,
      T2 t2,
      T3 t3,
      T4 t4,
      T5 t5) implements Tuple {
  }

  public record P6<T1, T2, T3, T4, T5, T6>(
      T1 t1,
      T2 t2,
      T3 t3,
      T4 t4,
      T5 t5,
      T6 t6) implements Tuple {
  }

  public record P7<T1, T2, T3, T4, T5, T6, T7>(
      T1 t1,
      T2 t2,
      T3 t3,
      T4 t4,
      T5 t5,
      T6 t6,
      T7 t7) implements Tuple {
  }

  public record P8<T1, T2, T3, T4, T5, T6, T7, T8>(
      T1 t1,
      T2 t2,
      T3 t3,
      T4 t4,
      T5 t5,
      T6 t6,
      T7 t7,
      T8 t8) implements Tuple {
  }

  public record P9<T1, T2, T3, T4, T5, T6, T7, T8, T9>(
      T1 t1,
      T2 t2,
      T3 t3,
      T4 t4,
      T5 t5,
      T6 t6,
      T7 t7,
      T8 t8,
      T9 t9) implements Tuple {
  }

  public record P10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>(
      T1 t1,
      T2 t2,
      T3 t3,
      T4 t4,
      T5 t5,
      T6 t6,
      T7 t7,
      T8 t8,
      T9 t9,
      T10 t10) implements Tuple {
  }

  public record P11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>(
      T1 t1,
      T2 t2,
      T3 t3,
      T4 t4,
      T5 t5,
      T6 t6,
      T7 t7,
      T8 t8,
      T9 t9,
      T10 t10,
      T11 t11) implements Tuple {
  }

  public record P12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>(
      T1 t1,
      T2 t2,
      T3 t3,
      T4 t4,
      T5 t5,
      T6 t6,
      T7 t7,
      T8 t8,
      T9 t9,
      T10 t10,
      T11 t11,
      T12 t12) implements Tuple {
  }
}
