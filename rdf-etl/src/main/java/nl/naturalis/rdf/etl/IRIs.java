package nl.naturalis.rdf.etl;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

public class IRIs {

  private static final ValueFactory vf = SimpleValueFactory.getInstance();

  public static final IRI DC_TITLE = vf.createIRI("dc:title");
  public static final IRI DC_TYPE = vf.createIRI("dc:type");
  
  public static final IRI DWC_FAMILY = vf.createIRI("dwc:family");
  public static final IRI DWC_RECORDED_BY = vf.createIRI("dwc:recordedBy");
  public static final IRI DWC_FIELD_NUMBER = vf.createIRI("dwc:fieldNumber");
  public static final IRI DWC_ASSOCIATED_MEDIA = vf.createIRI("dwc:associatedMedia");
  public static final IRI DWC_DECIMAL_LONGITUDE = vf.createIRI("dwc:decimalLongitude");
  public static final IRI DWC_DECIMAL_LATITUDE = vf.createIRI("dwc:decimalLatitude");

}
