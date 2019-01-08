package nl.naturalis.rdf.etl;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.core.JsonParseException;

import nl.naturalis.rdf.util.JsonArrayProcessor;
import nl.naturalis.rdf.util.JsonStreamException;

/**
 * Main class for importing specimen data into the RDF store.
 */
public class SpecimenImporter {

  public static void main(String[] args) throws JsonParseException, IOException, JsonStreamException {
    // SpecimenRdfGenerator srg = new SpecimenRdfGenerator();
    try (SpecimenRdfLoader loader = new SpecimenRdfLoader()) {
      InputStream is = SpecimenImporter.class.getResourceAsStream("/nba-aves-10-specimens.json");
      JsonArrayProcessor jsonArrayProcessor = JsonArrayProcessor.create(is, loader);
      jsonArrayProcessor.process();
    }
  }

}
