package nl.naturalis.lod.util.json;

import java.util.Map;

@FunctionalInterface
public interface JsonObjectHandler {

  public void handle(Map<String, Object> obj);
  
}
