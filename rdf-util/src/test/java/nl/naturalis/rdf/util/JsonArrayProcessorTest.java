package nl.naturalis.rdf.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class JsonArrayProcessorTest {

  @Test
  public void process01() throws JsonParseException, IOException, JsonStreamException {
    String s = "[]";
    JsonArrayProcessor p = JsonArrayProcessor.create(s, (obj) -> {
      fail("Should not be here");
    });
    p.process();
  }

  @Test
  public void process02() throws JsonParseException, IOException, JsonStreamException {
    String s = "[{ \"firstName\" : \"John\", \"lastName\" : \"Smith\" }]";
    JsonObjectHandler h = new JsonObjectHandler() {
      @Override
      public void handle(Map<String, Object> obj) {
        assertEquals("01", 2, obj.keySet().size());
        assertEquals("02", "John", obj.get("firstName"));
        assertEquals("03", "Smith", obj.get("lastName"));
      }
    };
    JsonArrayProcessor p = JsonArrayProcessor.create(s, h);
    p.process();
  }

  @Test
  public void process03() throws JsonParseException, IOException, JsonStreamException {
    CollectingJsonObjectHandler h = new CollectingJsonObjectHandler();
    InputStream is = getClass().getResourceAsStream("JsonStreamProcessorTest.process03.json");
    JsonArrayProcessor p = JsonArrayProcessor.create(is, h);
    p.process();
    assertEquals("01", 1, h.collected.size());
  }

  @Test
  public void process04() throws JsonParseException, IOException, JsonStreamException {
    CollectingJsonObjectHandler h = new CollectingJsonObjectHandler();
    InputStream is = getClass().getResourceAsStream("JsonStreamProcessorTest.process04.json");
    JsonArrayProcessor p = JsonArrayProcessor.create(is, h);
    p.process();
    assertEquals("01", 2, h.collected.size());
  }

}


class CollectingJsonObjectHandler implements JsonObjectHandler {

  public List<Map<String, Object>> collected = new ArrayList<>();

  @Override
  public void handle(Map<String, Object> obj) {
    collected.add(obj);
  }

}
