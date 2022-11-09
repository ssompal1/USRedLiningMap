package edu.brown.cs.student.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Can count the number of words, characters, rows, and columns of a CSV file and can parse a CSV
 * file, creating the provided object from each row's values.
 *
 * @param <T> any type the user provides through their implementation of the CreatorFromRow
 *     interface
 */
public class CSVParser<T> {
  // Class parameters
  private final Reader reader;
  private final CreatorFromRow<T> rowCreator;

  // Counts of data
  private int wordCount = 0;
  private int characterCount = 0;
  private int rowCount = 0;
  private int columnCount = 0;

  /**
   * Represents a CSV file with a corresponding CreatorFromRow implementation.
   *
   * @param reader reader object containing file
   * @param rowCreator implementation of interface with type T
   */
  public CSVParser(Reader reader, CreatorFromRow<T> rowCreator) {
    this.reader = reader;
    this.rowCreator = rowCreator;
  }

  /**
   * @return word count
   */
  public int getWordCount() {
    return wordCount;
  }

  /**
   * @return character count
   */
  public int getCharacterCount() {
    return characterCount;
  }

  /**
   * @return row count
   */
  public int getRowCount() {
    return rowCount;
  }

  /**
   * @return column count
   */
  public int getColumnCount() {
    return columnCount;
  }

  /**
   * Counts the number of words, characters, rows, and columns in a given CSV, skipping the first
   * line of the file.
   *
   * @throws IOException error from reading file
   */
  public void count() throws IOException {
    try {
      // Creates a BufferedReader object and reads first line for column count
      BufferedReader in = new BufferedReader(this.reader);

      // Guarantees there is anything to read in the CSV
      String line;
      if ((line = in.readLine()) != null) {
        // Divides line into columns and counts the number of columns
        String[] columns = line.split(",");
        this.columnCount = columns.length;
      }

      // Counts the number of words, characters, and rows
      while ((line = in.readLine()) != null) {
        // Increments character, words, rows, and columns
        this.wordCount += new StringTokenizer(line, ",| ").countTokens();
        this.characterCount += line.length();
        this.rowCount++;
      }

      // Closes the BufferedReader
      in.close();

    } catch (IOException e) { // IOException
      System.err.println("ERROR: the Reader object cannot be wrapped in a BufferReader object!");
      throw e;
    }
  }

  /**
   * Creates a list of objects of type T, where each row of the CSV is converted to one object.
   *
   * @return a list of objects generated from each row of the provided CSV
   * @throws FactoryFailureException error from creating object
   * @throws IOException error from reading file
   */
  public List<T> create() throws FactoryFailureException, IOException {
    // List to store each object representation of a row
    List<T> objects = new ArrayList<>();

    try {
      // Creates a BufferedReader object and checks if empty
      BufferedReader in = new BufferedReader(this.reader);
      if (in.readLine() == null) {
        System.err.println("ERROR: The file cannot be converted because it is empty.");
      }

      String line;
      while ((line = in.readLine()) != null) {
        // Creates object from row and adds it to list
        String[] columns = line.split(",");
        List<String> objectFields = Arrays.asList(columns);
        T newObject = this.rowCreator.create(objectFields);
        objects.add(newObject);
      }

      // Closes the BufferedReader
      in.close();

    } catch (IOException e) { // IOException
      System.err.println("ERROR: the Reader object cannot be wrapped in a BufferReader object!");
      throw e;
    } catch (FactoryFailureException f) { // Factory Failure Exception
      System.err.println("ERROR: a row cannot be converted to an object!");
      throw f;
    }

    // Return the data for the user in the form of a list
    return objects;
  }
}
