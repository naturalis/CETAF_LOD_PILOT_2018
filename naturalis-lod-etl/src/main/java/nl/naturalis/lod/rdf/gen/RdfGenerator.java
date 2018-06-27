package nl.naturalis.lod.rdf.gen;

import java.io.File;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonParseException;
import nl.naturalis.lod.util.json.JsonObjectHandler;
import nl.naturalis.lod.util.json.JsonStreamException;
import nl.naturalis.lod.util.json.JsonStreamProcessor;

public class RdfGenerator {

  public static void main(String[] args)
      throws JsonParseException, IOException, JsonStreamException {
    File f = new File("/home/ayco/tmp/sp2.json");
    JsonObjectHandler h = (obj) -> {
      System.out.println(obj.keySet().size());
    };
    JsonStreamProcessor josp = JsonStreamProcessor.forFile(f, h);
    josp.process();
  }

}
