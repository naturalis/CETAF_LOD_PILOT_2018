package nl.naturalis.rdf.etl;

import java.util.List;
import java.util.Map;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import nl.naturalis.rdf.util.MapReader;
import nl.naturalis.rdf.util.Path;

/**
 * Converts specimens (provided in the form of Map instances) to RDF, and writes the RDF to standard out. For demo, test and debug purposes.
 */
public class SpecimenRDFConsoleWriter implements SpecimenRDFWriter {

  private final ModelBuilder builder;
  private final ValueFactory vf;

  private int counter = 0;

  public SpecimenRDFConsoleWriter() {
    builder = new ModelBuilder();
    vf = SimpleValueFactory.getInstance();
    builder.setNamespace("dc", "http://purl.org/dc/terms/");
    builder.setNamespace("dwc", "http://rs.tdwg.org/dwc/terms/");
  }

  @Override
  @SuppressWarnings("rawtypes")
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
      builder.add("dwc:associatedMedia", ((List) obj).get(0).toString());
    }
    if ((val = readMap(mr, PATH_LATITUDE)) != null) {
      builder.add("dwc:decimalLatitude", val);
    }
    if ((val = readMap(mr, PATH_LONGITUDE)) != null) {
      builder.add("dwc:decimalLongitude", val);
    }
    Model model = builder.build();
    Rio.write(model, System.out, RDFFormat.RDFXML);
    ++counter;
  }

  public int counter() {
    return counter;
  }

  private static String readMap(MapReader mr, Path p) {
    Object obj = mr.read(p);
    return obj == MapReader.MISSING_VALUE ? null : obj.toString();
  }

}
