package edu.brown.cs.student.csv;

import java.util.List;

/** Factory to create Integers from a List of Strings. */
public class IntegerFactory implements CreatorFromRow<Integer> {

  @Override
  public Integer create(List<String> row) throws FactoryFailureException {
    if (row.size() != 1) {
      throw new FactoryFailureException(row);
    }
    try {
      return Integer.parseInt(row.get(0));
    } catch (NumberFormatException e) {
      throw new FactoryFailureException(row);
    }
  }
}
