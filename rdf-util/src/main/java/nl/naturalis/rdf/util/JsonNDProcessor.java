package nl.naturalis.rdf.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.common.json.ObjectMapperLocator;

public class JsonNDProcessor {

  @SuppressWarnings("unused")
  private static final Logger logger = LogManager.getLogger(JsonNDProcessor.class);
  private static final ObjectReader jsonReader;

  static {
    ObjectMapper om = ObjectMapperLocator.getInstance().getObjectMapper(Specimen.class);
    jsonReader = om.readerFor(new TypeReference<Map<String, Object>>() {});
  }

  public static JsonNDProcessor create(File f, JsonObjectHandler h) throws FileNotFoundException {
    return new JsonNDProcessor(new LineNumberReader(new FileReader(f)), h);
  }

  private final LineNumberReader lnr;
  private final JsonObjectHandler handler;

  private JsonNDProcessor(LineNumberReader lnr, JsonObjectHandler handler) {
    this.lnr = lnr;
    this.handler = handler;
  }

  public void process() throws IOException {
    LineNumberReader lnr = this.lnr;
    for (String line = lnr.readLine(); line != null; line = lnr.readLine()) {
      handler.handle(jsonReader.readValue(line));
    }
  }

}
