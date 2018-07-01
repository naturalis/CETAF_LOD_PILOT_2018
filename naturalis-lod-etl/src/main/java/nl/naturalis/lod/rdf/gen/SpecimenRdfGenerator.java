package nl.naturalis.lod.rdf.gen;

import java.util.Map;

import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import nl.naturalis.lod.util.json.JsonObjectHandler;
import nl.naturalis.lod.util.json.MapReader;
import nl.naturalis.lod.util.json.Path;

public class SpecimenRdfGenerator implements JsonObjectHandler {

	ModelBuilder builder;
	ValueFactory vf;

	private static final Path PATH_UNIT_ID = new Path("unitID");
	private static final Path PATH_SCIENTIFIC_NAME = new Path("identifications.0.scientificName.fullScientificName");

	public SpecimenRdfGenerator() {
		builder = new ModelBuilder();
		vf = SimpleValueFactory.getInstance();
		builder.setNamespace("dc", "http://purl.org/dc/terms/");
		builder.setNamespace("dwc", "http://rs.tdwg.org/dwc/terms/");
		builder.setNamespace("ex", "http://example.org/");

	}

	@Override
	public void handle(Map<String, Object> map) {
		MapReader mr = new MapReader(map);
		String unitID = (String) mr.read(PATH_UNIT_ID);
		String sn = (String) mr.read(PATH_SCIENTIFIC_NAME);
		IRI iri = vf.createIRI("http://data.biodiversitydata.nl/naturalis/specimen/" + unitID);
		builder.subject(iri).add("dc:title", vf.createLiteral(sn));

		Model model = builder.build();

		// Instead of simply printing the statements to the screen, we use a Rio writer
		// to
		// write the model in RDF/XML syntax:
		Rio.write(model, System.out, RDFFormat.RDFXML);
	}

}
