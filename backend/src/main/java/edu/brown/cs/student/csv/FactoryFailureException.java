package edu.brown.cs.student.csv;

import java.util.List;

/** Exception thrown when a Factory class fails. */
public class FactoryFailureException extends Exception {
  /** row that cannot be converted to an object */
  final List<String> row;

  /**
   * @param row the row the create method fails on
   */
  public FactoryFailureException(List<String> row) {
    this.row = row;
  }
}
