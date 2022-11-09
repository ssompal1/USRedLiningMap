package edu.brown.cs.student.kdtree;

import java.util.Arrays;
import java.util.Objects;

/** Class to represent a simple point in k-dimensional space. */
public class Point implements KdTreeNode {
  private double[] point;
  private int dimension;
  private final int hashNum = 31;

  /**
   * Constructs a point from an array of doubles.
   *
   * @param vals array of doubles representing a point (i.e. (x,y,z))
   */
  public Point(double[] vals) {
    this.point = vals;
    this.dimension = vals.length;
  }

  /**
   * Getter to return the dimension of a given Point.
   *
   * @return integer representing the dimension of a Point.
   */
  public int getDimension() {
    return this.dimension;
  }

  /**
   * Finds euclidean distance between the current node and a given node.
   *
   * @param node The point to find the distance to.
   * @return double The straight line distance.
   */
  public double euclideanDistance(KdTreeNode node) {
    double[] currentVals = this.point;
    double[] targetVals = node.getPoint();
    double sum = 0;
    for (int i = 0; i < currentVals.length; i++) {
      sum += Math.pow(currentVals[i] - targetVals[i], 2);
    }
    return Math.sqrt(sum);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Point)) {
      return false;
    }
    Point point1 = (Point) o;
    return dimension == point1.dimension && Arrays.equals(point, point1.point);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(dimension);
    result = hashNum * result + Arrays.hashCode(point);
    return result;
  }

  /**
   * Getter to return the point data of a Point.
   *
   * @return array of doubles representing a point (i.e. (x,y,z))
   */
  public double[] getPoint() {
    return this.point;
  }
}
