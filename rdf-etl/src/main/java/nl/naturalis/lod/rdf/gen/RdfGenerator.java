package nl.naturalis.lod.rdf.gen;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.core.JsonParseException;

import nl.naturalis.rdf.util.JsonArrayProcessor;
import nl.naturalis.rdf.util.JsonStreamException;


public class RdfGenerator {

  public static void main(String[] args) throws JsonParseException, IOException, JsonStreamException {
    // SpecimenRdfGenerator srg = new SpecimenRdfGenerator();
    try (SpecimenRdfLoader loader = new SpecimenRdfLoader()) {
      InputStream is = RdfGenerator.class.getResourceAsStream("/nba-aves-10-specimens.json");
      JsonArrayProcessor josp = JsonArrayProcessor.create(is, loader);
      josp.process();
    }
  }

}
