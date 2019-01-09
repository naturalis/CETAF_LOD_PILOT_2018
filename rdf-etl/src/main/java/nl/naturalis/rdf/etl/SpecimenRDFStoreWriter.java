package nl.naturalis.rdf.etl;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.nativerdf.NativeStore;

import nl.naturalis.rdf.util.JsonObjectHandler;
import nl.naturalis.rdf.util.MapReader;
import nl.naturalis.rdf.util.Path;

/**
 * A JsonObjectHandler implementation that handles instances of the (NBA) Specimen class. It extracts information from a Specimen object,
 * converts it into RDF triplets, and pumps them into the RDF store. A SpecimenRdfLoader is a stateful object. It should be instatiated just
 * once, passed on to a JsonArrayProcessor, and then closed once the JsonArrayProcessor is finished.
 */
public class SpecimenRdfLoader implements JsonObjectHandler, AutoCloseable {

  private static final ValueFactory vf = SimpleValueFactory.getInstance();

  private static final String PURL_BASE_URL = "http://data.biodiversitydata.nl/naturalis/specimen/";

  static final Path PATH_UNIT_ID = new Path("unitID");
  static final Path PATH_SCIENTIFIC_NAME = new Path("identifications.0.scientificName.fullScientificName");
  static final Path PATH_FAMILY = new Path("identifications.0.defaultClassification.family");
  static final Path PATH_RECORD_BASIS = new Path("kindOfUnit");
  static final Path PATH_COLLECTOR = new Path("gatheringEvent.gatheringPersons.0.fullName");
  static final Path PATH_COLLECTOR_FIELDNO = new Path("collectorsFieldNumber");
  static final Path PATH_MULTIMEDIA = new Path("associatedMultiMediaUris");
  static final Path PATH_LATITUDE = new Path("gatheringEvent.siteCoordinates.0.latitudeDecimal");
  static final Path PATH_LONGITUDE = new Path("gatheringEvent.siteCoordinates.0.longitudeDecimal");

  private final RepositoryConnection con;

  public SpecimenRdfLoader() {
    String path = System.getProperty("user.home") + "/rdf4j-specimens";
    File dataDir = new File(path);
    Repository repo = new SailRepository(new NativeStore(dataDir));
    repo.initialize();
    con = repo.getConnection();
    con.setNamespace("dc", "http://purl.org/dc/terms/");
    con.setNamespace("dwc", "http://rs.tdwg.org/dwc/terms/");
  }

  @Override
  @SuppressWarnings("unchecked")
  public void handle(Map<String, Object> map) {
    MapReader mapReader = new MapReader(map);
    String unitID = (String) mapReader.read(PATH_UNIT_ID);
    IRI subject = iri(PURL_BASE_URL + unitID);
    String val = readMap(mapReader, PATH_SCIENTIFIC_NAME);
    con.add(subject, iri("dc:title"), stringLiteral(val));
    if ((val = readMap(mapReader, PATH_FAMILY)) != null) {
      con.add(subject, iri("dwc:family"), stringLiteral(val));
    }
    if ((val = readMap(mapReader, PATH_RECORD_BASIS)) != null) {
      con.add(subject, iri("dc:type"), stringLiteral(val));
    }
    if ((val = readMap(mapReader, PATH_COLLECTOR)) != null) {
      con.add(subject, iri("dwc:recordedBy"), stringLiteral(val));
    }
    if ((val = readMap(mapReader, PATH_COLLECTOR_FIELDNO)) != null) {
      con.add(subject, iri("dwc:fieldNumber"), stringLiteral(val));
    }
    Object obj = mapReader.read(PATH_MULTIMEDIA);
    if (obj != null && obj != MapReader.MISSING_VALUE) {
      String url = ((List<Object>) obj).get(0).toString();
      con.add(subject, iri("dwc:associatedMedia"), stringLiteral(url));
    }
    if ((val = readMap(mapReader, PATH_LATITUDE)) != null) {
      con.add(subject, iri("dwc:decimalLatitude"), doubleLiteral(val));
    }
    if ((val = readMap(mapReader, PATH_LONGITUDE)) != null) {
      con.add(subject, iri("dwc:decimalLongitude"), doubleLiteral(val));
    }
  }

  @Override
  public void close() {
    con.close();
  }

  private static String readMap(MapReader mr, Path p) {
    Object obj = mr.read(p);
    return obj == MapReader.MISSING_VALUE ? null : obj.toString();
  }

  private static IRI iri(String s) {
    return vf.createIRI(s);
  }

  private static Literal stringLiteral(String s) {
    return vf.createLiteral(s);
  }

  private static Literal doubleLiteral(String s) {
    return vf.createLiteral(Double.valueOf(s));
  }

}
