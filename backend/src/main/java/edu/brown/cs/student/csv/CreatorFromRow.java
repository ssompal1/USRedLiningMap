package edu.brown.cs.student.csv;

import java.util.List;

/**
 * Creates an object of type T from a List of Strings. The create function is used within the
 * CSVParser class in order to convert rows of the CSV into the provided type.
 *
 * @param <T> object to be created
 */
public interface CreatorFromRow<T> {

  /**
   * @param row current row in the CSV file
   * @return type version of the List String
   * @throws FactoryFailureException error from creating object
   */
  T create(List<String> row) throws FactoryFailureException;
}
