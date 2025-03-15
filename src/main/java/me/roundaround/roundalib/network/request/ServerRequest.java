package me.roundaround.roundalib.network.request;

import net.minecraft.network.packet.CustomPayload;

import java.util.concurrent.CompletableFuture;

public class ServerRequest<T extends CustomPayload> {
  public static final Runnable NOOP = () -> {
  };

  private final Integer reqId;
  private final Class<T> type;
  private final CompletableFuture<T> future;

  public ServerRequest(int reqId, Class<T> type) {
    this(reqId, type, new CompletableFuture<>());
  }

  public ServerRequest(int reqId, Class<T> type, Runnable onCancel) {
    this(reqId, type, cancellableFuture(onCancel));
  }

  private ServerRequest(Integer reqId, Class<T> type, CompletableFuture<T> future) {
    this.reqId = reqId;
    this.type = type;
    this.future = future;
  }

  public int getReqId() {
    return this.reqId;
  }

  public CompletableFuture<T> getFuture() {
    return this.future;
  }

  public boolean matches(int reqId, CustomPayload payload) {
    return this.reqId != null && this.type != null && this.reqId == reqId &&
           payload.getClass().isAssignableFrom(this.type);
  }

  public boolean cancel() {
    return this.future.cancel(true);
  }

  @SuppressWarnings("unchecked")
  public boolean resolve(int reqId, CustomPayload payload) {
    if (!this.matches(reqId, payload)) {
      return false;
    }
    this.future.complete((T) payload);
    return true;
  }

  public static <T extends CustomPayload> ServerRequest<T> failedRequest(Throwable throwable) {
    return new ServerRequest<>(null, null, CompletableFuture.failedFuture(throwable));
  }

  private static <T> CompletableFuture<T> cancellableFuture(Runnable onCancel) {
    return new CompletableFuture<>() {
      @Override
      public boolean cancel(boolean mayInterruptIfRunning) {
        boolean cancelled = super.cancel(mayInterruptIfRunning);
        if (cancelled) {
          onCancel.run();
        }
        return cancelled;
      }
    };
  }
}
