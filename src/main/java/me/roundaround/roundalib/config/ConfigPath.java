package me.roundaround.roundalib.config;

import java.util.Objects;

public class ConfigPath implements Comparable<ConfigPath> {
  public static final String DELIMITER = ".";

  protected final String group;
  protected final String id;

  protected ConfigPath(String group, String id) {
    this.group = sanitize(group);
    this.id = sanitize(id);

    assert this.id != null;
  }

  public static ConfigPath of(String id) {
    return of(null, id);
  }

  public static ConfigPath of(String group, String id) {
    return new ConfigPath(group, id);
  }

  public static ConfigPath parse(String path) {
    return parse(path, DELIMITER);
  }

  public static ConfigPath parse(String path, String delimiter) {
    int i = path.indexOf(delimiter);
    if (i < 0) {
      return of(path);
    }
    return of(path.substring(0, i), path.substring(i + 1));
  }

  public boolean hasGroup() {
    return this.group != null;
  }

  public String getGroup() {
    return this.group;
  }

  public String getId() {
    return this.id;
  }

  public String toString(String delimiter) {
    if (this.group == null) {
      return this.id;
    }
    return this.group + delimiter + this.id;
  }

  @Override
  public String toString() {
    return this.toString(DELIMITER);
  }

  @Override
  public int compareTo(ConfigPath other) {
    int i = this.id.compareTo(other.id);
    if (i == 0) {
      i = Objects.compare(this.group, other.group, (o1, o2) -> {
        if (o1 == null && o2 == null) {
          return 0;
        } else if (o1 == null) {
          return -1;
        } else if (o2 == null) {
          return 1;
        }

        return o1.compareTo(o2);
      });
    }
    return i;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof ConfigPath that))
      return false;
    return Objects.equals(this.group, that.group) && Objects.equals(this.id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.group, this.id);
  }

  protected static String sanitize(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    return value.trim();
  }
}
