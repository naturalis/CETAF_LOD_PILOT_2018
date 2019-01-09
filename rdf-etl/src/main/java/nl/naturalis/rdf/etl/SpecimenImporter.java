package nl.naturalis.rdf.etl;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.naturalis.rdf.util.JsonNDProcessor;

/**
 * Main class for importing specimen data into the RDF store.
 */
public class SpecimenImporter {

  private static final Logger logger = LogManager.getLogger(SpecimenImporter.class);

  public static void main(String... args) throws JsonParseException, IOException {
    if (args.length == 0) {
      logger.debug("Please provide JsonND file with specimens or directory containing such files (*.ndjson)");
      return;
    }
    File file = new File(args[0]);
    if (!file.exists()) {
      logger.debug("No such file: {}", args[0]);
      return;
    }
    File[] files;
    if (file.isFile()) {
      files = new File[] {file};
    } else {
      files = file.listFiles(x -> x.getName().endsWith(".ndjson"));
    }
    if (System.getProperty("debug") == null || System.getProperty("debug").equals("false")) {
      logger.info("Importing specimens into RDF store");
      try (SpecimenRDFStoreWriter writer = new SpecimenRDFStoreWriter()) {
        for (File f : files) {
          logger.info("processing file {}", f.getAbsolutePath());
          JsonNDProcessor.create(f, writer).process();
        }
        logger.info("Number of specimens processed: {}", writer.counter());
      }
    } else {
      SpecimenRDFConsoleWriter writer = new SpecimenRDFConsoleWriter();
      for (File f : files) {
        logger.info("processing file {}", f.getAbsolutePath());
        JsonNDProcessor.create(f, writer).process();
      }
      logger.info("Number of specimens processed: {}", writer.counter());
    }
  }

}
