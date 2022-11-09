package edu.brown.cs.student.stars;

import edu.brown.cs.student.csv.CreatorFromRow;
import edu.brown.cs.student.csv.FactoryFailureException;
import java.util.List;

/** Factory to create Star objects from a List of Strings. */
public class StarFactory implements CreatorFromRow<Star> {

  @Override
  public Star create(List<String> row) throws FactoryFailureException {
    if (row.size() != 5) {
      throw new FactoryFailureException(row);
    }
    try {
      long id = Long.parseLong(row.get(0));
      String name = row.get(1);
      double x = Double.parseDouble(row.get(2));
      double y = Double.parseDouble(row.get(3));
      double z = Double.parseDouble(row.get(4));
      return new Star(id, name, x, y, z);
    } catch (NumberFormatException e) {
      throw new FactoryFailureException(row);
    }
  }
}
