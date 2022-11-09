package edu.brown.cs.student.csv;

import java.util.List;

/** Factory to create Integers from a List of Strings. */
public class ListStringFactory implements CreatorFromRow<List<String>> {

  @Override
  public List<String> create(List<String> row) {
    return row;
  }
}
