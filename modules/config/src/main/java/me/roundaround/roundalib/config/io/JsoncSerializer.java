package me.roundaround.roundalib.config.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

/**
 * {@link ConfigSerializer} implementation for JSON-with-comments (JSONC) format.
 * Produces a structured JSON object where top-level scalars appear as root
 * properties and sections appear as nested objects. Comments use {@code //}
 * line syntax.
 * <p>
 * The reader expects the same regular structure that the writer produces.
 * Value types returned on read: {@link Boolean}, {@link Integer},
 * {@link Double}, {@link String}, {@link List}{@code <Object>}.
 */
public final class JsoncSerializer implements ConfigSerializer {

  @Override
  public ConfigDoc read(Reader reader) throws IOException {
    ConfigDoc doc = new ConfigDoc();
    BufferedReader br = reader instanceof BufferedReader b ? b : new BufferedReader(reader);

    String currentSection = null;
    StringBuilder pendingComment = new StringBuilder();

    String line;
    while ((line = br.readLine()) != null) {
      String trimmed = line.trim();

      if (trimmed.isEmpty() || trimmed.equals("{") || trimmed.equals("}")) {
        pendingComment.setLength(0);
        continue;
      }

      // Section close: "}," or "}" at indent 2
      if (trimmed.equals("},") || (trimmed.equals("}") && currentSection != null)) {
        currentSection = null;
        pendingComment.setLength(0);
        continue;
      }

      if (trimmed.startsWith("//")) {
        String commentText = trimmed.substring(2);
        if (!pendingComment.isEmpty()) {
          pendingComment.append("\n");
        }
        pendingComment.append(commentText);
        continue;
      }

      if (!trimmed.startsWith("\"")) {
        continue;
      }

      // Parse: "key": value or "key": {
      int keyEnd = trimmed.indexOf('"', 1);
      if (keyEnd < 0) {
        continue;
      }
      String key = trimmed.substring(1, keyEnd);

      String rest = trimmed.substring(keyEnd + 1).trim();
      if (!rest.startsWith(":")) {
        continue;
      }
      rest = rest.substring(1).trim();

      // Strip trailing comma
      if (rest.endsWith(",")) {
        rest = rest.substring(0, rest.length() - 1).trim();
      }

      if (rest.equals("{")) {
        currentSection = key;
        pendingComment.setLength(0);
        continue;
      }

      String path = currentSection != null ? currentSection + "." + key : key;
      doc.set(path, this.parseValue(rest));

      if (!pendingComment.isEmpty()) {
        doc.setComment(path, pendingComment.toString());
        pendingComment.setLength(0);
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

    writer.write("{\n");

    // All top-level object keys (for comma placement)
    List<String> topLevelKeys = new ArrayList<>(rootScalarKeys);
    topLevelKeys.addAll(sectionKeys.keySet());
    topLevelKeys.addAll(rootMapKeys);
    int totalTop = topLevelKeys.size();
    int topIdx = 0;

    for (String path : rootScalarKeys) {
      topIdx++;
      boolean last = (topIdx == totalTop);
      this.writeComment(writer, doc.getComment(path), "  ");
      writer.write("  \"" + path + "\": " + this.formatValue(doc.get(path)));
      writer.write(last ? "\n" : ",\n");
    }

    int sectionIdx = rootScalarKeys.size();
    for (Map.Entry<String, List<String>> section : sectionKeys.entrySet()) {
      sectionIdx++;
      boolean lastSection = (sectionIdx == totalTop - rootMapKeys.size());
      String group = section.getKey();
      List<String> keys = section.getValue();

      writer.write("  \"" + group + "\": {\n");
      for (int i = 0; i < keys.size(); i++) {
        String path = keys.get(i);
        boolean lastEntry = (i == keys.size() - 1);
        String key = path.substring(group.length() + 1);
        this.writeComment(writer, doc.getComment(path), "    ");
        writer.write("    \"" + key + "\": " + this.formatValue(doc.get(path)));
        writer.write(lastEntry ? "\n" : ",\n");
      }
      writer.write("  }");
      writer.write((sectionIdx == totalTop) ? "\n" : ",\n");
    }

    int mapIdx = rootScalarKeys.size() + sectionKeys.size();
    for (String path : rootMapKeys) {
      mapIdx++;
      boolean lastItem = (mapIdx == totalTop);
      Map<?, ?> map = (Map<?, ?>) doc.get(path);
      List<?> mapKeys = new ArrayList<>(map.keySet());

      this.writeComment(writer, doc.getComment(path), "  ");
      writer.write("  \"" + path + "\": {\n");
      for (int i = 0; i < mapKeys.size(); i++) {
        Object k = mapKeys.get(i);
        boolean lastEntry = (i == mapKeys.size() - 1);
        writer.write("    \"" + k + "\": " + this.formatValue(map.get(k)));
        writer.write(lastEntry ? "\n" : ",\n");
      }
      writer.write("  }");
      writer.write(lastItem ? "\n" : ",\n");
    }

    writer.write("}\n");
  }

  // -------------------------------------------------------------------------
  // Parsing helpers
  // -------------------------------------------------------------------------

  private Object parseValue(String s) {
    if (s.equals("true")) {
      return Boolean.TRUE;
    }
    if (s.equals("false")) {
      return Boolean.FALSE;
    }
    if (s.equals("null")) {
      return null;
    }
    if (s.startsWith("\"")) {
      return this.parseJsonString(s);
    }
    if (s.startsWith("[")) {
      return this.parseJsonArray(s);
    }

    try {
      return Integer.parseInt(s);
    } catch (NumberFormatException ignored) {
    }

    try {
      return (int) Long.parseLong(s);
    } catch (NumberFormatException ignored) {
    }

    try {
      return Double.parseDouble(s);
    } catch (NumberFormatException ignored) {
    }

    try {
      // Try ROOT locale first
      return NumberFormat.getInstance(Locale.ROOT).parse(s).doubleValue();
    } catch (ParseException ignored) {
    }

    try {
      // Fall back to user's locale
      return NumberFormat.getInstance().parse(s).doubleValue();
    } catch (ParseException ignored) {
    }

    return s;
  }

  private String parseJsonString(String s) {
    if (s.length() < 2) {
      return s;
    }
    StringBuilder sb = new StringBuilder();
    int i = 1;
    while (i < s.length()) {
      char c = s.charAt(i);
      if (c == '"') {
        break;
      }
      if (c == '\\' && i + 1 < s.length()) {
        i++;
        switch (s.charAt(i)) {
          case 'n' -> sb.append('\n');
          case 'r' -> sb.append('\r');
          case 't' -> sb.append('\t');
          case '"' -> sb.append('"');
          case '\\' -> sb.append('\\');
          case '/' -> sb.append('/');
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

  private List<Object> parseJsonArray(String s) {
    List<Object> result = new ArrayList<>();
    String inner = s.substring(1, s.length() - 1).trim();
    if (inner.isEmpty()) {
      return result;
    }
    for (String elem : this.splitArrayElements(inner)) {
      result.add(this.parseValue(elem.trim()));
    }
    return result;
  }

  private List<String> splitArrayElements(String s) {
    List<String> result = new ArrayList<>();
    int depth = 0;
    boolean inStr = false;
    StringBuilder cur = new StringBuilder();

    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c == '\\' && inStr) {
        cur.append(c);
        if (i + 1 < s.length()) {
          cur.append(s.charAt(++i));
        }
        continue;
      }
      if (c == '"') {
        inStr = !inStr;
        cur.append(c);
        continue;
      }
      if (!inStr) {
        if (c == '[' || c == '{') {
          depth++;
        } else if (c == ']' || c == '}') {
          depth--;
        } else if (c == ',' && depth == 0) {
          result.add(cur.toString());
          cur.setLength(0);
          continue;
        }
      }
      cur.append(c);
    }
    if (!cur.isEmpty()) {
      result.add(cur.toString());
    }
    return result;
  }

  // -------------------------------------------------------------------------
  // Formatting helpers
  // -------------------------------------------------------------------------

  private void writeComment(Writer writer, String comment, String indent) throws IOException {
    if (comment == null) {
      return;
    }
    for (String line : comment.split("\n", -1)) {
      writer.write(indent + "//" + line + "\n");
    }
  }

  private String formatValue(Object value) {
    if (value == null) {
      return "null";
    }
    if (value instanceof Boolean b) {
      return b.toString();
    }
    if (value instanceof Integer i) {
      return i.toString();
    }
    if (value instanceof Long l) {
      return l.toString();
    }
    if (value instanceof Double d) {
      return this.formatDouble(d);
    }
    if (value instanceof Float f) {
      return this.formatDouble(f.doubleValue());
    }
    if (value instanceof String s) {
      return "\"" + this.escapeString(s) + "\"";
    }
    if (value instanceof List<?> list) {
      StringBuilder sb = new StringBuilder("[");
      boolean first = true;
      for (Object item : list) {
        if (!first) {
          sb.append(", ");
        }
        first = false;
        sb.append(this.formatValue(item));
      }
      sb.append("]");
      return sb.toString();
    }
    return "\"" + this.escapeString(String.valueOf(value)) + "\"";
  }

  private String formatDouble(double d) {
    if (d == Math.floor(d) && !Double.isInfinite(d) && Math.abs(d) < 1e15) {
      return String.format(Locale.ROOT, "%.1f", d);
    }
    return Double.toString(d);
  }

  private String escapeString(String s) {
    return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
  }
}
