package me.roundaround.roundalib.observable;

public interface Observer {
  @FunctionalInterface
  public interface P0 extends Observer {
    void handle();
  }

  @FunctionalInterface
  public interface P1<T1> extends Observer {
    void handle(T1 v1);
  }

  @FunctionalInterface
  public interface P2<T1, T2> extends Observer {
    void handle(T1 v1, T2 v2);
  }

  @FunctionalInterface
  public interface P3<T1, T2, T3> extends Observer {
    void handle(T1 v1, T2 v2, T3 v3);
  }

  @FunctionalInterface
  public interface P4<T1, T2, T3, T4> extends Observer {
    void handle(T1 v1, T2 v2, T3 v3, T4 v4);
  }

  @FunctionalInterface
  public interface P5<T1, T2, T3, T4, T5> extends Observer {
    void handle(
        T1 v1,
        T2 v2,
        T3 v3,
        T4 v4,
        T5 v5);
  }

  @FunctionalInterface
  public interface P6<T1, T2, T3, T4, T5, T6> extends Observer {
    void handle(
        T1 v1,
        T2 v2,
        T3 v3,
        T4 v4,
        T5 v5,
        T6 v6);
  }

  @FunctionalInterface
  public interface P7<T1, T2, T3, T4, T5, T6, T7> extends Observer {
    void handle(
        T1 v1,
        T2 v2,
        T3 v3,
        T4 v4,
        T5 v5,
        T6 v6,
        T7 v7);
  }

  @FunctionalInterface
  public interface P8<T1, T2, T3, T4, T5, T6, T7, T8> extends Observer {
    void handle(
        T1 v1,
        T2 v2,
        T3 v3,
        T4 v4,
        T5 v5,
        T6 v6,
        T7 v7,
        T8 v8);
  }

  @FunctionalInterface
  public interface P9<T1, T2, T3, T4, T5, T6, T7, T8, T9> extends Observer {
    void handle(
        T1 v1,
        T2 v2,
        T3 v3,
        T4 v4,
        T5 v5,
        T6 v6,
        T7 v7,
        T8 v8,
        T9 v9);
  }

  @FunctionalInterface
  public interface P10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> extends Observer {
    void handle(
        T1 v1,
        T2 v2,
        T3 v3,
        T4 v4,
        T5 v5,
        T6 v6,
        T7 v7,
        T8 v8,
        T9 v9,
        T10 v10);
  }

  @FunctionalInterface
  public interface P11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> extends Observer {
    void handle(
        T1 v1,
        T2 v2,
        T3 v3,
        T4 v4,
        T5 v5,
        T6 v6,
        T7 v7,
        T8 v8,
        T9 v9,
        T10 v10,
        T11 v11);
  }

  @FunctionalInterface
  public interface P12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> extends Observer {
    void handle(
        T1 v1,
        T2 v2,
        T3 v3,
        T4 v4,
        T5 v5,
        T6 v6,
        T7 v7,
        T8 v8,
        T9 v9,
        T10 v10,
        T11 v11,
        T12 v12);
  }
}
