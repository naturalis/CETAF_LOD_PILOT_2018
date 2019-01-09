package nl.naturalis.rdf.etl;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.nativerdf.NativeStore;

import nl.naturalis.rdf.util.MapReader;
import nl.naturalis.rdf.util.Path;

/**
 * Converts specimens (provided in the form of Map instances) to RDF triples, which are written to an RDF4J triple store.
 */
public class SpecimenRDFStoreWriter implements SpecimenRDFWriter, AutoCloseable {

  private static final Logger logger = LogManager.getLogger(SpecimenRDFStoreWriter.class);
  private static final ValueFactory vf = SimpleValueFactory.getInstance();

  private final RepositoryConnection con;

  private int counter = 0;

  public SpecimenRDFStoreWriter() {
    String path = System.getProperty("user.home") + "/rdf4j-specimens";
    File dataDir = new File(path);
    Repository repo = new SailRepository(new NativeStore(dataDir));
    repo.initialize();
    con = repo.getConnection();
    con.setNamespace("dc", "http://purl.org/dc/terms/");
    con.setNamespace("dwc", "http://rs.tdwg.org/dwc/terms/");
  }

  @Override
  @SuppressWarnings({"rawtypes"})
  public void handle(Map<String, Object> map) {
    MapReader mr = new MapReader(map);
    String val;
    if ((val = read(mr, PATH_UNIT_GUID)) == null) {
      logger.warn("Ignoring specimen without unitGUID (unitID={})", mr.read(PATH_UNIT_ID));
      return;
    }
    IRI subject = vf.createIRI(val);
    if ((val = read(mr, PATH_SCIENTIFIC_NAME)) != null) {
      con.add(subject, IRIs.DC_TITLE, stringLiteral(val));
    }
    if ((val = read(mr, PATH_FAMILY)) != null) {
      con.add(subject, IRIs.DWC_FAMILY, stringLiteral(val));
    }
    if ((val = read(mr, PATH_RECORD_BASIS)) != null) {
      con.add(subject, IRIs.DC_TYPE, stringLiteral(val));
    }
    if ((val = read(mr, PATH_COLLECTOR)) != null) {
      con.add(subject, IRIs.DWC_RECORDED_BY, stringLiteral(val));
    }
    if ((val = read(mr, PATH_COLLECTOR_FIELDNO)) != null) {
      con.add(subject, IRIs.DWC_FIELD_NUMBER, stringLiteral(val));
    }
    if ((val = read(mr, PATH_LATITUDE)) != null) {
      con.add(subject, IRIs.DWC_DECIMAL_LATITUDE, doubleLiteral(val));
    }
    if ((val = read(mr, PATH_LONGITUDE)) != null) {
      con.add(subject, IRIs.DWC_DECIMAL_LONGITUDE, doubleLiteral(val));
    }
    Object obj = mr.read(PATH_MULTIMEDIA);
    if (obj != null && obj != MapReader.MISSING_VALUE) {
      String url = ((List) obj).get(0).toString();
      con.add(subject, IRIs.DWC_ASSOCIATED_MEDIA, stringLiteral(url));
    }
    if (logger.isDebugEnabled() && ++counter % 1000 == 0) {
      logger.debug("Specimens processed: {}", counter);
    }
  }

  @Override
  public void close() {
    con.close();
  }

  public int counter() {
    return counter;
  }

  private static String read(MapReader mr, Path p) {
    Object obj = mr.read(p);
    return (obj == null || obj == MapReader.MISSING_VALUE) ? null : obj.toString();
  }

  private static Literal stringLiteral(String s) {
    return vf.createLiteral(s);
  }

  private static Literal doubleLiteral(String s) {
    return vf.createLiteral(Double.valueOf(s));
  }

}
