package nl.naturalis.lod.rdf.gen;

import static com.fasterxml.jackson.core.JsonToken.END_ARRAY;
import static com.fasterxml.jackson.core.JsonToken.END_OBJECT;
import static com.fasterxml.jackson.core.JsonToken.START_ARRAY;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class RdfGenerator {

  public static void main(String[] args) throws JsonParseException, IOException {
    JsonFactory jsonF = new JsonFactory();
    JsonParser jp = jsonF.createParser(new File("/home/ayco/tmp/sp.json"));
    if (jp.nextToken() != START_ARRAY) {
      throw new RuntimeException("Cannot parse contents of file. Expected " + START_ARRAY);
    }
    while (jp.nextToken() != END_ARRAY) {
      HashMap<String, Object> specimen = readObject(jp);
      System.out.println(specimen.size());
    }
    jp.close();
  }

  private static Object readValue(JsonParser jp) throws IOException {
    switch (jp.currentToken()) {
      case VALUE_STRING:
        return jp.getValueAsString();
      case VALUE_NUMBER_INT:
        return Long.valueOf(jp.getLongValue());
      case VALUE_NUMBER_FLOAT:
        return Double.valueOf(jp.getDoubleValue());
      case START_OBJECT:
        return readObject(jp);
      case START_ARRAY:
        return readArray(jp);
      case VALUE_NULL:
        return null;
      case VALUE_FALSE:
        return Boolean.FALSE;
      case VALUE_TRUE:
        return Boolean.TRUE;
      default:
        throw unexpectedToken(jp.currentToken(), null);
    }
  }

  // Assumes current token is {
  private static HashMap<String, Object> readObject(JsonParser jp) throws IOException {
    HashMap<String, Object> map = new HashMap<>();
    jp.nextToken();
    while (jp.currentToken() != END_OBJECT) {
      String key = jp.getCurrentName();
      jp.nextToken();
      Object val = readValue(jp);
      map.put(key, val);
      jp.nextToken();
    }
    return map;
  }

  // Assumes current token is [
  private static List<Object> readArray(JsonParser jp) throws IOException {
    List<Object> list = new ArrayList<>();
    while (jp.nextToken() != END_ARRAY) {
      list.add(readValue(jp));
    }
    return list;
  }

  private static RuntimeException unexpectedToken(JsonToken unexpected, JsonToken expected) {
    if (expected == null) {
      return new RuntimeException("Unexpected token: " + unexpected);
    }
    String fmt = "Unexpected token: %s (expected %s)";
    String msg = String.format(fmt, unexpected, expected);
    return new RuntimeException(msg);
  }

}
