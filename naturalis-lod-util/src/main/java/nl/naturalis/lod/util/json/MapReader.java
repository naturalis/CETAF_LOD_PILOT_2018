package nl.naturalis.lod.util.json;

import java.util.List;
import java.util.Map;

public class MapReader {

	public static final Object MISSING_VALUE = new Object();

	@SuppressWarnings("unchecked")
	public static Object read(Map<String, Object> map, Path path) {
		for (int i = 0; i < path.countElements(); ++i) {
			Object val = map.get(path.getElement(i));
			if (val == null)
				return map.containsKey(path.getElement(i)) ? null : MISSING_VALUE;
			if (i == path.countElements() - 1)
				return val;
			if (val instanceof List) {
				try {
					int idx = Integer.parseInt(path.getElement(i + 1));
					List<?> list = (List<?>) val;
					if (idx >= list.size())
						return MISSING_VALUE;
					val = list.get(idx);
					if (++i == path.countElements() - 1)
						return val;
				} catch (NumberFormatException e) {
					String fmt = "Missing array index after %s in path %s";
					String msg = String.format(fmt, path.getElement(i), path);
					throw new MapReadException(msg);
				}
			}
			map = (Map<String, Object>) val;
		}
		/* Won't get here */ return null;
	}

	private final Map<String, Object> map;

	public MapReader(Map<String, Object> map) {
		this.map = map;
	}

	public Object read(Path path) {
		return read(map, path);
	}

}
