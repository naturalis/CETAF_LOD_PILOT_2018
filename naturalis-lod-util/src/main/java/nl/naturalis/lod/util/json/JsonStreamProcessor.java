package nl.naturalis.lod.util.json;

import static com.fasterxml.jackson.core.JsonToken.END_ARRAY;
import static com.fasterxml.jackson.core.JsonToken.END_OBJECT;
import static com.fasterxml.jackson.core.JsonToken.START_ARRAY;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;

/**
 * Processes a JSON stream constituting an array of objects. That is, the stream is supposed to look
 * like this:
 * 
 * <pre>
 * [ {...}, {...}, {...}, ... ]
 * </pre>
 * 
 * Each object is read into a <code>Map</code> which is passed on to a {@link JsonObjectHandler}.
 * 
 * @author Ayco Holleman
 *
 */
public class JsonStreamProcessor {

  public static JsonStreamProcessor create(InputStream is, JsonObjectHandler h)
      throws JsonParseException, IOException {
    JsonFactory jsonF = new JsonFactory();
    JsonParser jp = jsonF.createParser(is);
    return new JsonStreamProcessor(jp, h);
  }

  public static JsonStreamProcessor create(Reader r, JsonObjectHandler h)
      throws JsonParseException, IOException {
    JsonFactory jsonF = new JsonFactory();
    JsonParser jp = jsonF.createParser(r);
    return new JsonStreamProcessor(jp, h);
  }

  public static JsonStreamProcessor create(File f, JsonObjectHandler h)
      throws JsonParseException, IOException {
    JsonFactory jsonF = new JsonFactory();
    JsonParser jp = jsonF.createParser(f);
    return new JsonStreamProcessor(jp, h);
  }

  public static JsonStreamProcessor create(String s, JsonObjectHandler h)
      throws JsonParseException, IOException {
    return create(new StringReader(s), h);
  }

  private final JsonParser p;
  private final JsonObjectHandler h;

  private JsonStreamProcessor(JsonParser parser, JsonObjectHandler handler) {
    this.p = parser;
    this.h = handler;
  }

  public void process() throws JsonStreamException {
    try {
      if (p.nextToken() != START_ARRAY) {
        throw new JsonStreamException("First token in stream must be " + START_ARRAY);
      }
      while (p.nextToken() != END_ARRAY) {
        h.handle(readObject());
      }
    } catch (IOException e) {
      throw new JsonStreamException(e);
    }
  }

  private Object readValue() throws IOException, JsonStreamException {
    switch (p.currentToken()) {
      case VALUE_STRING:
        return p.getValueAsString();
      case VALUE_NUMBER_INT:
        return Long.valueOf(p.getLongValue());
      case VALUE_NUMBER_FLOAT:
        return Double.valueOf(p.getDoubleValue());
      case START_OBJECT:
        return readObject();
      case START_ARRAY:
        return readArray();
      case VALUE_NULL:
        return null;
      case VALUE_FALSE:
        return Boolean.FALSE;
      case VALUE_TRUE:
        return Boolean.TRUE;
      default:
        throw new JsonStreamException("Unexpected token: " + p.currentToken());
    }
  }

  // Assumes current token is {
  private HashMap<String, Object> readObject() throws IOException, JsonStreamException {
    HashMap<String, Object> obj = new HashMap<>();
    p.nextToken();
    while (p.currentToken() != END_OBJECT) {
      String key = p.getCurrentName();
      p.nextToken();
      obj.put(key, readValue());
      p.nextToken();
    }
    return obj;
  }

  // Assumes current token is [
  private List<Object> readArray() throws IOException, JsonStreamException {
    List<Object> list = new ArrayList<>();
    while (p.nextToken() != END_ARRAY) {
      list.add(readValue());
    }
    return list;
  }

}
