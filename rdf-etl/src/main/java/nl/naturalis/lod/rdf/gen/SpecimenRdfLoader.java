package nl.naturalis.lod.rdf.gen;

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


public class SpecimenRdfLoader implements JsonObjectHandler, AutoCloseable {

	private static final ValueFactory vf = SimpleValueFactory.getInstance();

	private static final String PURL_BASE_URL = "http://data.biodiversitydata.nl/naturalis/specimen/";

	private static final Path PATH_UNIT_ID = new Path("unitID");
	private static final Path PATH_SCIENTIFIC_NAME = new Path("identifications.0.scientificName.fullScientificName");
	private static final Path PATH_FAMILY = new Path("identifications.0.defaultClassification.family");
	private static final Path PATH_RECORD_BASIS = new Path("kindOfUnit");
	private static final Path PATH_COLLECTOR = new Path("gatheringEvent.gatheringPersons.0.fullName");
	private static final Path PATH_COLLECTOR_FIELDNO = new Path("collectorsFieldNumber");
	private static final Path PATH_MULTIMEDIA = new Path("associatedMultiMediaUris");
	private static final Path PATH_LATITUDE = new Path("gatheringEvent.siteCoordinates.0.latitudeDecimal");
	private static final Path PATH_LONGITUDE = new Path("gatheringEvent.siteCoordinates.0.longitudeDecimal");

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
		MapReader mr = new MapReader(map);
		String unitID = (String) mr.read(PATH_UNIT_ID);
		IRI subject = iri(PURL_BASE_URL + unitID);
		String val = readMap(mr, PATH_SCIENTIFIC_NAME);
		con.add(subject, iri("dc:title"), str(val));
		if ((val = readMap(mr, PATH_FAMILY)) != null) {
			con.add(subject, iri("dwc:family"), str(val));
		}
		if ((val = readMap(mr, PATH_RECORD_BASIS)) != null) {
			con.add(subject, iri("dc:type"), str(val));
		}
		if ((val = readMap(mr, PATH_COLLECTOR)) != null) {
			con.add(subject, iri("dwc:recordedBy"), str(val));
		}
		if ((val = readMap(mr, PATH_COLLECTOR_FIELDNO)) != null) {
			con.add(subject, iri("dwc:fieldNumber"), str(val));
		}
		Object obj = mr.read(PATH_MULTIMEDIA);
		if (obj != null && obj != MapReader.MISSING_VALUE) {
			String url = ((List<Object>) obj).get(0).toString();
			con.add(subject, iri("dwc:associatedMedia"), str(url));
		}
		if ((val = readMap(mr, PATH_LATITUDE)) != null) {
			con.add(subject, iri("dwc:decimalLatitude"), dbl(val));
		}
		if ((val = readMap(mr, PATH_LONGITUDE)) != null) {
			con.add(subject, iri("dwc:decimalLongitude"), dbl(val));
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

	private static Literal str(String s) {
		return vf.createLiteral(s);
	}

	private static Literal dbl(String s) {
		return vf.createLiteral(Double.valueOf(s));
	}

}
