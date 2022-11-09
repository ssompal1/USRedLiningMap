package edu.brown.cs.student.stars;

import java.util.ArrayList;
import java.util.List;

/** Generates a galaxy of Stars. */
public class GalaxyGenerator {
  /**
   * Generates a galaxy (list) of Stars.
   *
   * @param numStars number of stars in the galaxy
   * @return list of stars
   */
  public static List<Star> generate(int numStars) {
    List<Star> galaxy = new ArrayList<>();
    for (int i = 0; i < numStars; i++) {
      galaxy.add(new Star(i, "star " + i, Math.random(), Math.random(), Math.random()));
    }
    return galaxy;
  }
}
