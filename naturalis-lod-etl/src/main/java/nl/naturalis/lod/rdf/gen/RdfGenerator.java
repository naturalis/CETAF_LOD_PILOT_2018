package nl.naturalis.lod.rdf.gen;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.core.JsonParseException;

import nl.naturalis.lod.util.json.JsonObjectHandler;
import nl.naturalis.lod.util.json.JsonStreamException;
import nl.naturalis.lod.util.json.JsonStreamProcessor;

public class RdfGenerator {

  public static void main(String[] args)
      throws JsonParseException, IOException, JsonStreamException {
    JsonObjectHandler h = (obj) -> {
      System.out.println(obj.keySet().size());
    };
    InputStream is = RdfGenerator.class.getResourceAsStream("/nba-aves-5-specimens.json");
    JsonStreamProcessor josp = JsonStreamProcessor.create(is, h);
    josp.process();
  }

}
