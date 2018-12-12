package nl.naturalis.lod.rdf.gen;

import java.util.List;
import java.util.Map;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import nl.naturalis.rdf.util.JsonObjectHandler;
import nl.naturalis.rdf.util.MapReader;
import nl.naturalis.rdf.util.Path;


public class SpecimenRdfGenerator implements JsonObjectHandler {

	ModelBuilder builder;
	ValueFactory vf;

	private static final Path PATH_UNIT_ID = new Path("unitID");
	private static final Path PATH_SCIENTIFIC_NAME = new Path("identifications.0.scientificName.fullScientificName");
	private static final Path PATH_FAMILY = new Path("identifications.0.defaultClassification.family");
	private static final Path PATH_RECORD_BASIS = new Path("kindOfUnit");
	private static final Path PATH_COLLECTOR = new Path("gatheringEvent.gatheringPersons.0.fullName");
	private static final Path PATH_COLLECTOR_FIELDNO = new Path("collectorsFieldNumber");
	private static final Path PATH_MULTIMEDIA = new Path("associatedMultiMediaUris");
	private static final Path PATH_LATITUDE = new Path("gatheringEvent.siteCoordinates.0.latitudeDecimal");
	private static final Path PATH_LONGITUDE = new Path("gatheringEvent.siteCoordinates.0.longitudeDecimal");

	public SpecimenRdfGenerator() {
		builder = new ModelBuilder();
		vf = SimpleValueFactory.getInstance();
		builder.setNamespace("dc", "http://purl.org/dc/terms/");
		builder.setNamespace("dwc", "http://rs.tdwg.org/dwc/terms/");
		builder.setNamespace("ex", "http://example.org/");

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

		// Instead of simply printing the statements to the screen, we use a Rio writer
		// to
		// write the model in RDF/XML syntax:
		Rio.write(model, System.out, RDFFormat.RDFXML);
	}

  private static String readMap(MapReader mr, Path p) {
		Object obj = mr.read(p);
		return obj == MapReader.MISSING_VALUE ? null : obj.toString();
	}

}
