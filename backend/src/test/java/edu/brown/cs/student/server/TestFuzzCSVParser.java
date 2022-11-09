package edu.brown.cs.student.server;

import edu.brown.cs.student.csv.CSVParser;
import edu.brown.cs.student.csv.FactoryFailureException;
import edu.brown.cs.student.csv.ListStringFactory;
import edu.brown.cs.student.stars.Star;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.Test;

public class TestFuzzCSVParser {
  static final int NUM_TRIALS = 1000;
  static final int MAX_STARS = 100;

  public static String getRandomCSV(int maxStrings) {
    StringBuilder sb = new StringBuilder();
    final ThreadLocalRandom r = ThreadLocalRandom.current();
    int numStrings = r.nextInt(maxStrings + 1);
    for (int count = 0; count < numStrings; count++) {
      Star star = getRandomStar();
      sb.append(starToRow(star));
      sb.append(System.lineSeparator());
    }
    return sb.toString();
  }

  public static Star getRandomStar() {
    final ThreadLocalRandom r = ThreadLocalRandom.current();
    long id = r.nextLong();
    int nameLength = r.nextInt(10);
    String name = getRandomStringBounded(nameLength, 45, 126);
    double x = r.nextDouble(Double.MIN_VALUE, Double.MAX_VALUE);
    double y = r.nextDouble(Double.MIN_VALUE, Double.MAX_VALUE);
    double z = r.nextDouble(Double.MIN_VALUE, Double.MAX_VALUE);
    return new Star(id, name, x, y, z);
  }

  public static String starToRow(Star s) {
    return s.id() + "," + s.name() + "," + s.x() + "," + s.y() + "," + s.z();
  }

  public static String getRandomStringBounded(int length, int first, int last) {
    final ThreadLocalRandom r = ThreadLocalRandom.current();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < length; i++) {
      int code = r.nextInt(first, last + 1);
      sb.append((char) code);
    }
    return sb.toString();
  }

  @Test
  public void fuzzTestParser() throws IOException, FactoryFailureException {
    for (int counter = 0; counter < NUM_TRIALS; counter++) {
      String csvString = getRandomCSV(MAX_STARS);
      Reader csv = new StringReader(csvString);
      CSVParser parser = new CSVParser(csv, new ListStringFactory());
      List<List<String>> data = parser.create();
    }
  }
}
