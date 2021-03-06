package nl.naturalis.rdf.util;

import java.util.List;
import java.util.Map;

public class MapReader {

  public static final Object MISSING_VALUE = new Object();

  @SuppressWarnings({"unchecked", "rawtypes"})
  public static Object read(Map<String, Object> map, Path path) {
    Object val;
    for (int i = 0; i < path.countElements(); ++i) {
      if ((val = map.get(path.getElement(i))) == null) {
        return map.containsKey(path.getElement(i)) ? null : MISSING_VALUE;
      } else if (i == path.countElements() - 1) {
        return val;
      } else if (val instanceof List) {
        try {
          int idx = Integer.parseInt(path.getElement(i + 1));
          List list = (List) val;
          if (idx >= list.size()) {
            return MISSING_VALUE;
          }
          val = list.get(idx);
          if (++i == path.countElements() - 1) {
            return val;
          }
        } catch (NumberFormatException e) {
          String fmt = "Missing array index after %s in path %s";
          String msg = String.format(fmt, path.getElement(i), path);
          throw new MapReadException(msg);
        }
      }
      map = (Map<String, Object>) val;
    }
    throw new IllegalArgumentException("Zero-length path not allowed");
  }

  private Map<String, Object> map;

  public MapReader(Map<String, Object> map) {
    this.map = map;
  }

  public Object read(Path path) {
    return read(map, path);
  }

}
