package edu.brown.cs.student.stars;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.brown.cs.student.csv.CSVParser;
import edu.brown.cs.student.csv.FactoryFailureException;
import edu.brown.cs.student.csv.IntegerFactory;
import edu.brown.cs.student.csv.ListStringFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests the functionality of the CSVParser class, addressing both the create() and count() methods
 * in addition to multiple implementations of the CreatorFromRow interface
 */
public class TestCSVParser {

  // ** Tests count of an empty CSV */
  @Test
  public void testCountEmpty() throws IOException {
    File file = new File("data/stars/empty.csv");
    FileReader fr = new FileReader(file);

    StarFactory sf = new StarFactory();
    CSVParser<Star> parser = new CSVParser<>(fr, sf);
    parser.count();

    assertEquals(0, parser.getWordCount()); // Word Count
    assertEquals(0, parser.getCharacterCount()); // Character Count
    assertEquals(0, parser.getRowCount()); // Row Count
    assertEquals(0, parser.getColumnCount()); // Column Count
  }

  // ** Tests count of a CSV with only a single character */
  @Test
  public void testCountOneChar() throws IOException {
    File file = new File("data/stars/one-char.csv");
    FileReader fr = new FileReader(file);

    StarFactory sf = new StarFactory();
    CSVParser<Star> parser = new CSVParser<>(fr, sf);
    parser.count();

    assertEquals(1, parser.getWordCount()); // Word Count
    assertEquals(1, parser.getCharacterCount()); // Character Count
    assertEquals(1, parser.getRowCount()); // Row Count
    assertEquals(1, parser.getColumnCount()); // Column Count
  }

  /** Tests count of a CSV with only one row */
  @Test
  public void testCountOneRow() throws IOException {
    File file = new File("data/stars/one-star.csv");
    FileReader fr = new FileReader(file);

    StarFactory sf = new StarFactory();
    CSVParser<Star> parser = new CSVParser<>(fr, sf);
    parser.count();

    assertEquals(5, parser.getWordCount()); // Word Count
    assertEquals(11, parser.getCharacterCount()); // Character Count
    assertEquals(1, parser.getRowCount()); // Row Count
    assertEquals(5, parser.getColumnCount()); // Column Count
  }

  // ** Tests count of a CSV with only one column */
  @Test
  public void testCountOneColumn() throws IOException {
    File file = new File("data/stars/one-column.csv");
    FileReader fr = new FileReader(file);

    StarFactory sf = new StarFactory();
    CSVParser<Star> parser = new CSVParser<>(fr, sf);
    parser.count();

    assertEquals(5, parser.getWordCount()); // Word Count
    assertEquals(6, parser.getCharacterCount()); // Character Count
    assertEquals(5, parser.getRowCount()); // Row Count
    assertEquals(1, parser.getColumnCount()); // Column Count
  }

  /** Tests count of a CSV with empty values */
  @Test
  public void testCountEmptyValues() throws IOException {
    File file = new File("data/stars/empty-values.csv");
    FileReader fr = new FileReader(file);

    StarFactory sf = new StarFactory();
    CSVParser<Star> parser = new CSVParser<>(fr, sf);
    parser.count();

    assertEquals(8, parser.getWordCount()); // Word Count
    assertEquals(37, parser.getCharacterCount()); // Character Count
    assertEquals(2, parser.getRowCount()); // Row Count
    assertEquals(5, parser.getColumnCount()); // Column Count
  }

  /** Tests count of a small CSV */
  @Test
  public void testCountSmallCSV() throws IOException {
    File file = new File("data/stars/ten-star.csv");
    FileReader fr = new FileReader(file);

    StarFactory sf = new StarFactory();
    CSVParser<Star> parser = new CSVParser<>(fr, sf);
    parser.count();

    assertEquals(54, parser.getWordCount()); // Word Count
    assertEquals(362, parser.getCharacterCount()); // Character Count
    assertEquals(10, parser.getRowCount()); // Row Count
    assertEquals(5, parser.getColumnCount()); // Column Count
  }

  /** Tests count of a large CSV */
  @Test
  public void testCountLargeCSV() throws IOException {
    File file = new File("data/stars/stardata.csv");
    FileReader fr = new FileReader(file);

    StarFactory sf = new StarFactory();
    CSVParser<Star> parser = new CSVParser<>(fr, sf);
    parser.count();

    assertEquals(598116, parser.getWordCount()); // Word Count
    assertEquals(5286426, parser.getCharacterCount()); // Character Count
    assertEquals(119617, parser.getRowCount()); // Row Count
    assertEquals(5, parser.getColumnCount()); // Column Count
  }

  // ** Tests creation of objects from a CSV with empty values */
  @Test
  public void testCreateEmptyValues() throws IOException {
    File file = new File("data/stars/empty-values.csv");
    FileReader fr = new FileReader(file);

    StarFactory sf = new StarFactory();
    CSVParser<Star> parser = new CSVParser<>(fr, sf);
    assertThrows(FactoryFailureException.class, parser::create);
  }

  // ** Tests creation of objects from an improperly formatted CSV */
  @Test
  public void testCreateImproperFormat() throws FileNotFoundException {
    File file = new File("data/stars/improper.csv");
    FileReader fr = new FileReader(file);

    StarFactory sf = new StarFactory();
    CSVParser<Star> parser = new CSVParser<>(fr, sf);
    assertThrows(FactoryFailureException.class, parser::create);
  }

  // ** Tests creation of a single object from a CSV */
  @Test
  public void testCreateOneStar() throws IOException, FactoryFailureException {
    File file = new File("data/stars/one-star.csv");
    FileReader fr = new FileReader(file);

    StarFactory sf = new StarFactory();
    CSVParser<Star> parser = new CSVParser<>(fr, sf);

    assertEquals(List.of(new Star(0, "Sol", 0, 0, 0)), parser.create());
  }

  // ** Tests creation of two objects from a CSV */
  @Test
  public void testCreateTwoStars() throws IOException, FactoryFailureException {
    File file = new File("data/stars/two-stars.csv");
    FileReader fr = new FileReader(file);

    StarFactory sf = new StarFactory();
    CSVParser<Star> parser = new CSVParser<>(fr, sf);
    List<Star> stars = new ArrayList<>();
    stars.add(new Star(0, "Sol", 0, 0, 0));
    stars.add(new Star(1, "Andreas", 282.43485, 0.00449, 5.36884));

    assertEquals(stars, parser.create());
  }

  // ** Tests creation of multiple objects from a CSV */
  @Test
  public void testCreateFourStars() throws IOException, FactoryFailureException {
    File file = new File("data/stars/four-stars.csv");
    FileReader fr = new FileReader(file);

    StarFactory sf = new StarFactory();
    CSVParser<Star> parser = new CSVParser<>(fr, sf);
    List<Star> stars = new ArrayList<>();
    stars.add(new Star(0, "Sol", 0, 0, 0));
    stars.add(new Star(1, "Andreas", 282.43485, 0.00449, 5.36884));
    stars.add(new Star(2, "Rory", 43.04329, 0.00285, -15.24144));
    stars.add(new Star(3, "Mortimer", 277.11358, 0.02422, 223.27753));

    assertEquals(stars, parser.create());
  }

  // ** Tests creation of objects using a different CreatorFromRow interface (List<String>) */
  @Test
  public void testCreateListOfStringInterface() throws IOException, FactoryFailureException {
    File file = new File("data/stars/two-stars.csv");
    FileReader fr = new FileReader(file);

    ListStringFactory lsf = new ListStringFactory();
    CSVParser<List<String>> parser = new CSVParser<>(fr, lsf);
    List<List<String>> stars = new ArrayList<>();
    stars.add(List.of("0", "Sol", "0", "0", "0"));
    stars.add(List.of("1", "Andreas", "282.43485", "0.00449", "5.36884"));

    assertEquals(stars, parser.create());
  }

  // ** Tests creation of objects using a different CreatorFromRow interface (List<String>) */
  @Test
  public void testCreateIntegerInterface() throws IOException, FactoryFailureException {
    File file = new File("data/stars/one-column.csv");
    FileReader fr = new FileReader(file);

    IntegerFactory integerFactory = new IntegerFactory();
    CSVParser<Integer> parser = new CSVParser<>(fr, integerFactory);

    assertEquals(List.of(1, 6, 8, 11, 2), parser.create());
  }

  // ** Tests create on a file but with a Reader object */
  @Test
  public void testCreateReader() throws IOException, FactoryFailureException {
    File file = new File("data/stars/one-star.csv");
    Reader r = new FileReader(file);

    StarFactory sf = new StarFactory();
    CSVParser<Star> parser = new CSVParser<>(r, sf);

    assertEquals(List.of(new Star(0, "Sol", 0, 0, 0)), parser.create());
  }

  // ** Tests create on a file but with a StringReader object */
  @Test
  public void testCreateStringReader() throws IOException, FactoryFailureException {
    String data = "StarID,ProperName,X,Y,Z\n" + "0,Sol,0,0,0";
    StringReader sr = new StringReader(data);

    StarFactory sf = new StarFactory();
    CSVParser<Star> parser = new CSVParser<>(sr, sf);

    assertEquals(List.of(new Star(0, "Sol", 0, 0, 0)), parser.create());
  }

  // ** Tests count on a file but with a Reader object */
  @Test
  public void testCountReader() throws IOException {
    File file = new File("data/stars/one-star.csv");
    Reader r = new FileReader(file);

    StarFactory sf = new StarFactory();
    CSVParser<Star> parser = new CSVParser<>(r, sf);
    parser.count();

    assertEquals(5, parser.getWordCount()); // Word Count
    assertEquals(11, parser.getCharacterCount()); // Character Count
    assertEquals(1, parser.getRowCount()); // Row Count
    assertEquals(5, parser.getColumnCount()); // Column Count
  }

  // ** Tests count on a file but with a StringReader object */
  @Test
  public void testCountStringReader() throws IOException {
    String data = "StarID,ProperName,X,Y,Z\n" + "0,Sol,0,0,0";
    StringReader sr = new StringReader(data);

    StarFactory sf = new StarFactory();
    CSVParser<Star> parser = new CSVParser<>(sr, sf);
    parser.count();

    assertEquals(5, parser.getWordCount()); // Word Count
    assertEquals(11, parser.getCharacterCount()); // Character Count
    assertEquals(1, parser.getRowCount()); // Row Count
    assertEquals(5, parser.getColumnCount()); // Column Count
  }
}
