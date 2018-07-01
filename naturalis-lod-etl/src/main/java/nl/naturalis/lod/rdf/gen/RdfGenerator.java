package nl.naturalis.lod.rdf.gen;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.core.JsonParseException;

import nl.naturalis.lod.util.json.JsonObjectHandler;
import nl.naturalis.lod.util.json.JsonStreamException;
import nl.naturalis.lod.util.json.JsonStreamProcessor;

public class RdfGenerator {

  public static void main(String[] args)
      throws JsonParseException, IOException, JsonStreamException {
	  SpecimenRdfGenerator srg = new SpecimenRdfGenerator();
    InputStream is = RdfGenerator.class.getResourceAsStream("/nba-aves-1-specimen.json");
    JsonStreamProcessor josp = JsonStreamProcessor.create(is, srg);
    josp.process();
  }

}
