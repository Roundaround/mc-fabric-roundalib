package me.roundaround.roundalib.network.request;

import net.minecraft.network.packet.CustomPayload;

import java.util.HashMap;
import java.util.List;

public class RequestTracker {
  private final HashMap<Integer, ServerRequest<?>> pendingRequests = new HashMap<>();

  public <T extends CustomPayload> ServerRequest<T> create(Class<T> type) throws IllegalStateException {
    return this.create(type, ServerRequest.NOOP);
  }

  public <T extends CustomPayload> ServerRequest<T> create(Class<T> type, Runnable onCancel) {
    int reqId = this.getUniqueReqId();
    ServerRequest<T> request = new ServerRequest<>(
        reqId,
        type,
        join(onCancel, () -> this.pendingRequests.remove(reqId))
    );
    this.pendingRequests.put(reqId, request);
    return request;
  }

  public boolean resolve(ServerRequest<?> request, CustomPayload payload) {
    return this.resolve(request.getReqId(), payload);
  }

  public boolean resolve(int reqId, CustomPayload payload) {
    ServerRequest<?> request = this.pendingRequests.remove(reqId);
    if (request == null || request.getFuture().isDone()) {
      return false;
    }
    return request.resolve(reqId, payload);
  }

  public boolean cancel(ServerRequest<?> request) {
    return this.cancel(request.getReqId());
  }

  public boolean cancel(int reqId) {
    ServerRequest<?> request = this.pendingRequests.remove(reqId);
    if (request == null) {
      return false;
    }
    return request.cancel();
  }

  public void clear() {
    List<ServerRequest<?>> requests = List.copyOf(this.pendingRequests.values());
    for (ServerRequest<?> request : requests) {
      // Each request should auto-remove from the map.
      request.cancel();
    }

    // Clear afterward just in case someone added a request outside the given API.
    this.pendingRequests.clear();
  }

  private int getUniqueReqId() {
    for (int i = Integer.MIN_VALUE; i < Integer.MAX_VALUE; i++) {
      if (!this.pendingRequests.containsKey(i)) {
        return i;
      }
    }
    throw new IllegalStateException("Failed to find an available request ID.");
  }

  private static Runnable join(Runnable a, Runnable b) {
    return () -> {
      a.run();
      b.run();
    };
  }
}
