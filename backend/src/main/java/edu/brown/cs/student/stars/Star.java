package edu.brown.cs.student.stars;

import edu.brown.cs.student.kdtree.KdTreeNode;

/**
 * Class that represents a three-dimensional star with a name and id.
 *
 * <p>NOTE: This is a **RECORD** class. Java automatically creates equals, toString, etc. Immutable
 * data, so we get getters, but not setters.
 *
 * <p>This requires Java 16 or higher (and might need setting in language level as well as project
 * structure)
 */
public record Star(long id, String name, double x, double y, double z) implements KdTreeNode {
  @Override
  public double[] getPoint() {
    return new double[] {this.x, this.y, this.z};
  }

  @Override
  public int getDimension() {
    return 3;
  }

  @Override
  public double euclideanDistance(KdTreeNode node) {
    assert node.getDimension() == this.getDimension();

    double[] nodePoint = node.getPoint();
    double deltaX = this.x - nodePoint[0];
    double deltaY = this.y - nodePoint[1];
    double deltaZ = this.z - nodePoint[2];
    return Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
  }
}
