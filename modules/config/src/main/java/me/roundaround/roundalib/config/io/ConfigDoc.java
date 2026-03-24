package me.roundaround.roundalib.config.io;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * In-memory representation of a config document. Stores entries by dotted path
 * ({@code "key"} for root-level, {@code "group.key"} for sectioned entries),
 * preserving insertion order. Each entry may carry an optional leading comment.
 */
public final class ConfigDoc {
  private final LinkedHashMap<String, Object> values = new LinkedHashMap<>();
  private final LinkedHashMap<String, String> comments = new LinkedHashMap<>();

  // -------------------------------------------------------------------------
  // Write operations
  // -------------------------------------------------------------------------

  public void set(String path, Object value) {
    values.put(path, value);
  }

  /** Sets {@code value} only if {@code path} is not already present. */
  public void setIfAbsent(String path, Object value) {
    values.putIfAbsent(path, value);
  }

  /**
   * If {@code path} is not already present, computes a value using {@code computer}
   * and stores it. Returns the existing or newly computed value.
   */
  public Object computeIfAbsent(String path, Function<String, Object> computer) {
    return values.computeIfAbsent(path, computer);
  }

  /** Removes the entry at {@code path} and its associated comment, if present. */
  public void remove(String path) {
    values.remove(path);
    comments.remove(path);
  }

  public void setComment(String path, String comment) {
    comments.put(path, comment);
  }

  // -------------------------------------------------------------------------
  // Read operations
  // -------------------------------------------------------------------------

  public Object get(String path) {
    Object direct = values.get(path);
    if (direct != null) return direct;

    // Fall back to collecting section sub-entries (e.g. when TOML stored a Map-valued
    // option as a [section] and the reader produced "path.key" entries).
    String prefix = path + ".";
    LinkedHashMap<String, Object> section = new LinkedHashMap<>();
    for (Map.Entry<String, Object> entry : values.entrySet()) {
      if (entry.getKey().startsWith(prefix)) {
        section.put(entry.getKey().substring(prefix.length()), entry.getValue());
      }
    }
    return section.isEmpty() ? null : section;
  }

  /** Returns the value at {@code path}, or {@code defaultValue} if absent. */
  public Object getOrDefault(String path, Object defaultValue) {
    Object v = get(path);
    return v != null ? v : defaultValue;
  }

  public int getIntOrElse(String path, int fallback) {
    Object v = values.get(path);
    if (v instanceof Integer i) return i;
    if (v instanceof Long l) return (int) l.longValue();
    if (v instanceof Number n) return n.intValue();
    if (v instanceof String s) {
      try {
        return Integer.parseInt(s.trim());
      } catch (NumberFormatException ignored) {
      }
    }
    return fallback;
  }

  public long getLongOrElse(String path, long fallback) {
    Object v = values.get(path);
    if (v instanceof Long l) return l;
    if (v instanceof Number n) return n.longValue();
    if (v instanceof String s) {
      try {
        return Long.parseLong(s.trim());
      } catch (NumberFormatException ignored) {
      }
    }
    return fallback;
  }

  public double getDoubleOrElse(String path, double fallback) {
    Object v = values.get(path);
    if (v instanceof Double d) return d;
    if (v instanceof Number n) return n.doubleValue();
    if (v instanceof String s) {
      try {
        return Double.parseDouble(s.trim());
      } catch (NumberFormatException ignored) {
      }
    }
    return fallback;
  }

  public boolean getBooleanOrElse(String path, boolean fallback) {
    Object v = values.get(path);
    if (v instanceof Boolean b) return b;
    if (v instanceof String s) {
      if (s.equalsIgnoreCase("true")) return true;
      if (s.equalsIgnoreCase("false")) return false;
    }
    return fallback;
  }

  public String getStringOrElse(String path, String fallback) {
    Object v = values.get(path);
    if (v instanceof String s) return s;
    if (v != null) return String.valueOf(v);
    return fallback;
  }

  public String getComment(String path) {
    return comments.get(path);
  }

  // -------------------------------------------------------------------------
  // Membership / iteration
  // -------------------------------------------------------------------------

  /** Returns {@code true} if an entry exists at {@code path}. */
  public boolean contains(String path) {
    return values.containsKey(path);
  }

  /** @deprecated Use {@link #contains(String)} */
  @Deprecated
  public boolean containsKey(String path) {
    return contains(path);
  }

  public Set<String> keySet() {
    return Collections.unmodifiableSet(values.keySet());
  }
}
