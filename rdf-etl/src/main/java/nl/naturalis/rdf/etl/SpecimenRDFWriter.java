package nl.naturalis.rdf.etl;

import nl.naturalis.rdf.util.JsonObjectHandler;
import nl.naturalis.rdf.util.Path;

public interface SpecimenRDFWriter extends JsonObjectHandler {
  
  public static final Path PATH_UNIT_ID = new Path("unitID");
  public static final Path PATH_UNIT_GUID = new Path("unitGUID");
  public static final Path PATH_SCIENTIFIC_NAME = new Path("identifications.0.scientificName.fullScientificName");
  public static final Path PATH_FAMILY = new Path("identifications.0.defaultClassification.family");
  public static final Path PATH_RECORD_BASIS = new Path("kindOfUnit");
  public static final Path PATH_COLLECTOR = new Path("gatheringEvent.gatheringPersons.0.fullName");
  public static final Path PATH_COLLECTOR_FIELDNO = new Path("collectorsFieldNumber");
  public static final Path PATH_MULTIMEDIA = new Path("associatedMultiMediaUris");
  public static final Path PATH_LATITUDE = new Path("gatheringEvent.siteCoordinates.0.latitudeDecimal");
  public static final Path PATH_LONGITUDE = new Path("gatheringEvent.siteCoordinates.0.longitudeDecimal");

  /**
   * Returns the number of RDF triplets written thus far.
   * @return
   */
  int counter();

}
