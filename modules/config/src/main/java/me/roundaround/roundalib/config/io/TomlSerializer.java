package me.roundaround.roundalib.config.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link ConfigSerializer} implementation for TOML format. Supports sections
 * (written as {@code [group]} headers), inline arrays, and {@code #}-prefixed
 * comments.
 * <p>
 * Value types returned on read: {@link Boolean}, {@link Integer},
 * {@link Double}, {@link String}, {@link List}{@code <Object>}.
 */
public final class TomlSerializer implements ConfigSerializer {

  @Override
  public ConfigDoc read(Reader reader) throws IOException {
    ConfigDoc doc = new ConfigDoc();
    BufferedReader br = reader instanceof BufferedReader b ? b : new BufferedReader(reader);

    String currentSection = null;
    StringBuilder pendingComment = new StringBuilder();

    String line;
    while ((line = br.readLine()) != null) {
      String trimmed = line.trim();

      if (trimmed.isEmpty()) {
        pendingComment.setLength(0);
        continue;
      }

      if (trimmed.startsWith("#")) {
        String commentLine = trimmed.substring(1);
        if (!pendingComment.isEmpty()) {
          pendingComment.append("\n");
        }
        pendingComment.append(commentLine);
        continue;
      }

      if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
        currentSection = trimmed.substring(1, trimmed.length() - 1).trim();
        pendingComment.setLength(0);
        continue;
      }

      int eqIndex = trimmed.indexOf('=');
      if (eqIndex > 0) {
        String key = trimmed.substring(0, eqIndex).trim();
        String valueStr = trimmed.substring(eqIndex + 1).trim();
        String path = currentSection != null ? currentSection + "." + key : key;

        doc.set(path, parseValue(valueStr));

        if (!pendingComment.isEmpty()) {
          doc.setComment(path, pendingComment.toString());
          pendingComment.setLength(0);
        }
      }
    }

    return doc;
  }

  @Override
  public void write(ConfigDoc doc, Writer writer) throws IOException {
    List<String> rootScalarKeys = new ArrayList<>();
    List<String> rootMapKeys = new ArrayList<>();
    Map<String, List<String>> sectionKeys = new LinkedHashMap<>();

    for (String path : doc.keySet()) {
      int dot = path.indexOf('.');
      if (dot < 0) {
        if (doc.get(path) instanceof Map<?, ?>) {
          rootMapKeys.add(path);
        } else {
          rootScalarKeys.add(path);
        }
      } else {
        String group = path.substring(0, dot);
        sectionKeys.computeIfAbsent(group, k -> new ArrayList<>()).add(path);
      }
    }

    for (String path : rootScalarKeys) {
      writeComment(writer, doc.getComment(path));
      writer.write(path + " = " + formatValue(doc.get(path)) + "\n");
    }

    for (Map.Entry<String, List<String>> section : sectionKeys.entrySet()) {
      writer.write("\n[" + section.getKey() + "]\n");
      for (String path : section.getValue()) {
        String key = path.substring(section.getKey().length() + 1);
        writeComment(writer, doc.getComment(path));
        writer.write(key + " = " + formatValue(doc.get(path)) + "\n");
      }
    }

    for (String path : rootMapKeys) {
      writeComment(writer, doc.getComment(path));
      writer.write("\n[" + path + "]\n");
      Map<?, ?> map = (Map<?, ?>) doc.get(path);
      for (Map.Entry<?, ?> entry : map.entrySet()) {
        writer.write(entry.getKey() + " = " + formatValue(entry.getValue()) + "\n");
      }
    }
  }

  // -------------------------------------------------------------------------
  // Parsing helpers
  // -------------------------------------------------------------------------

  private Object parseValue(String s) {
    if (s.equals("true")) return Boolean.TRUE;
    if (s.equals("false")) return Boolean.FALSE;
    if (s.startsWith("\"")) return parseString(s);
    if (s.startsWith("[")) return parseArray(s);

    try {
      return Integer.parseInt(s);
    } catch (NumberFormatException ignored) {
    }

    // Long → downcast (TOML integers fit in int for config values)
    try {
      return (int) Long.parseLong(s);
    } catch (NumberFormatException ignored) {
    }

    try {
      return Double.parseDouble(s);
    } catch (NumberFormatException ignored) {
    }

    return s;
  }

  private String parseString(String s) {
    StringBuilder sb = new StringBuilder();
    int i = 1;
    while (i < s.length()) {
      char c = s.charAt(i);
      if (c == '"') break;
      if (c == '\\' && i + 1 < s.length()) {
        i++;
        switch (s.charAt(i)) {
          case 'n' -> sb.append('\n');
          case 'r' -> sb.append('\r');
          case 't' -> sb.append('\t');
          case '"' -> sb.append('"');
          case '\\' -> sb.append('\\');
          default -> {
            sb.append('\\');
            sb.append(s.charAt(i));
          }
        }
      } else {
        sb.append(c);
      }
      i++;
    }
    return sb.toString();
  }

  private List<Object> parseArray(String s) {
    List<Object> result = new ArrayList<>();
    String inner = s.substring(1, s.length() - 1).trim();
    if (inner.isEmpty()) return result;
    for (String elem : splitElements(inner)) {
      result.add(parseValue(elem.trim()));
    }
    return result;
  }

  private List<String> splitElements(String s) {
    List<String> result = new ArrayList<>();
    int depth = 0;
    boolean inStr = false;
    StringBuilder cur = new StringBuilder();

    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c == '\\' && inStr) {
        cur.append(c);
        if (i + 1 < s.length()) cur.append(s.charAt(++i));
        continue;
      }
      if (c == '"') {
        inStr = !inStr;
        cur.append(c);
        continue;
      }
      if (!inStr) {
        if (c == '[' || c == '(') depth++;
        else if (c == ']' || c == ')') depth--;
        else if (c == ',' && depth == 0) {
          result.add(cur.toString());
          cur.setLength(0);
          continue;
        }
      }
      cur.append(c);
    }
    if (!cur.isEmpty()) result.add(cur.toString());
    return result;
  }

  // -------------------------------------------------------------------------
  // Formatting helpers
  // -------------------------------------------------------------------------

  private void writeComment(Writer writer, String comment) throws IOException {
    if (comment == null) return;
    for (String line : comment.split("\n", -1)) {
      writer.write("#" + line + "\n");
    }
  }

  private String formatValue(Object value) {
    if (value instanceof Boolean b) return b.toString();
    if (value instanceof Integer i) return i.toString();
    if (value instanceof Long l) return l.toString();
    if (value instanceof Double d) return formatDouble(d);
    if (value instanceof Float f) return formatDouble(f.doubleValue());
    if (value instanceof String s) return "\"" + escapeString(s) + "\"";
    if (value instanceof List<?> list) {
      StringBuilder sb = new StringBuilder("[");
      boolean first = true;
      for (Object item : list) {
        if (!first) sb.append(", ");
        first = false;
        sb.append(formatValue(item));
      }
      sb.append("]");
      return sb.toString();
    }
    return "\"" + escapeString(String.valueOf(value)) + "\"";
  }

  private String formatDouble(double d) {
    if (d == Math.floor(d) && !Double.isInfinite(d) && Math.abs(d) < 1e15) {
      return String.format("%.1f", d);
    }
    String s = Double.toString(d);
    if (!s.contains(".") && !s.contains("e") && !s.contains("E")) {
      s += ".0";
    }
    return s;
  }

  private String escapeString(String s) {
    return s.replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t");
  }
}
