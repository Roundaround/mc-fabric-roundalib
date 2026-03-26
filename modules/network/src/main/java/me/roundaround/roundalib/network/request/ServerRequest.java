package me.roundaround.roundalib.network.request;

import java.util.concurrent.CompletableFuture;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class ServerRequest<T extends CustomPacketPayload> {
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

  public boolean matches(int reqId, CustomPacketPayload payload) {
    return this.reqId != null && this.type != null && this.reqId == reqId &&
           payload.getClass().isAssignableFrom(this.type);
  }

  public boolean cancel() {
    return this.future.cancel(true);
  }

  @SuppressWarnings("unchecked")
  public boolean resolve(int reqId, CustomPacketPayload payload) {
    if (!this.matches(reqId, payload)) {
      return false;
    }
    this.future.complete((T) payload);
    return true;
  }

  public static <T extends CustomPacketPayload> ServerRequest<T> failedRequest(Throwable throwable) {
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
