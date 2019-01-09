package nl.naturalis.rdf.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.LineNumberReader;

public class JsonNdProcessor {

  public static JsonNdProcessor create(File f, JsonObjectHandler h) throws FileNotFoundException {
    return new JsonNdProcessor(new FileInputStream(f), h);
  }

  private final InputStream in;
  private final JsonObjectHandler handler;

  private JsonNdProcessor(InputStream in, JsonObjectHandler handler) {
    this.in = in;
    this.handler = handler;
  }
  
  public void process() throws JsonStreamException {
    
  }

}
