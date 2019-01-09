package nl.naturalis.rdf.etl;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import nl.naturalis.rdf.util.JsonArrayProcessor;
import nl.naturalis.rdf.util.JsonObjectHandler;
import nl.naturalis.rdf.util.JsonStreamException;
import nl.naturalis.rdf.util.MapReader;
import nl.naturalis.rdf.util.Path;

import static nl.naturalis.rdf.etl.SpecimenRdfLoader.PATH_COLLECTOR;
import static nl.naturalis.rdf.etl.SpecimenRdfLoader.PATH_COLLECTOR_FIELDNO;
import static nl.naturalis.rdf.etl.SpecimenRdfLoader.PATH_FAMILY;
import static nl.naturalis.rdf.etl.SpecimenRdfLoader.PATH_LATITUDE;
import static nl.naturalis.rdf.etl.SpecimenRdfLoader.PATH_LONGITUDE;
import static nl.naturalis.rdf.etl.SpecimenRdfLoader.PATH_MULTIMEDIA;
import static nl.naturalis.rdf.etl.SpecimenRdfLoader.PATH_RECORD_BASIS;
import static nl.naturalis.rdf.etl.SpecimenRdfLoader.PATH_SCIENTIFIC_NAME;
import static nl.naturalis.rdf.etl.SpecimenRdfLoader.PATH_UNIT_ID;

/**
 * A JsonObjectHandler implementation that handles instances of the (NBA) Specimen class. The RDF triples extracted from a Specimen are
 * written to standard out. For demo/test purposes.
 */
public class SpecimenRdfConsoleWriter implements JsonObjectHandler {

  public static void main(String[] args) throws JsonParseException, IOException, JsonStreamException {
    SpecimenRdfConsoleWriter writer = new SpecimenRdfConsoleWriter();
    InputStream is = SpecimenImporter.class.getResourceAsStream("/nba-aves-10-specimens.json");
    JsonArrayProcessor jap = JsonArrayProcessor.create(is, writer);
    jap.process();
  }

  private ModelBuilder builder;
  private ValueFactory vf;

  public SpecimenRdfConsoleWriter() {
    builder = new ModelBuilder();
    vf = SimpleValueFactory.getInstance();
    builder.setNamespace("dc", "http://purl.org/dc/terms/");
    builder.setNamespace("dwc", "http://rs.tdwg.org/dwc/terms/");
  }

  @Override
  @SuppressWarnings("unchecked")
  public void handle(Map<String, Object> map) {
    MapReader mr = new MapReader(map);
    String unitID = (String) mr.read(PATH_UNIT_ID);
    IRI iri = vf.createIRI("http://data.biodiversitydata.nl/naturalis/specimen/" + unitID);
    String val = readMap(mr, PATH_SCIENTIFIC_NAME);
    builder.subject(iri).add("dc:title", vf.createLiteral(val));
    if ((val = readMap(mr, PATH_FAMILY)) != null) {
      builder.add("dwc:family", val);
    }
    if ((val = readMap(mr, PATH_RECORD_BASIS)) != null) {
      builder.add("dc:type", val);
    }
    if ((val = readMap(mr, PATH_COLLECTOR)) != null) {
      builder.add("dwc:recordedBy", val);
    }
    if ((val = readMap(mr, PATH_COLLECTOR_FIELDNO)) != null) {
      builder.add("dwc:fieldNumber", val);
    }
    Object obj = mr.read(PATH_MULTIMEDIA);
    if (obj != null && obj != MapReader.MISSING_VALUE) {
      builder.add("dwc:associatedMedia", ((List<Object>) obj).get(0).toString());
    }
    if ((val = readMap(mr, PATH_LATITUDE)) != null) {
      builder.add("dwc:decimalLatitude", val);
    }
    if ((val = readMap(mr, PATH_LONGITUDE)) != null) {
      builder.add("dwc:decimalLongitude", val);
    }

    Model model = builder.build();

    Rio.write(model, System.out, RDFFormat.RDFXML);
  }

  private static String readMap(MapReader mr, Path p) {
    Object obj = mr.read(p);
    return obj == MapReader.MISSING_VALUE ? null : obj.toString();
  }

}
