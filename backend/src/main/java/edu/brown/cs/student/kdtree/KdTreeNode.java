package edu.brown.cs.student.kdtree;

/** Interface representing nodes of a k-d tree. */
public interface KdTreeNode {
  /**
   * Getter to return the point data.
   *
   * @return Array of doubles representing the point (i.e. x,y,z)
   */
  double[] getPoint();

  /**
   * Getter to return the dimension of the point at the node.
   *
   * @return Integer representing the dimension of the point.
   */
  int getDimension();

  /**
   * Finds the straight-line (euclidean) distance between the current KdTreeNode and another
   * KdTreeNode.
   *
   * @param node KdTreeNode to find the euclidean distance to.
   * @return double representing the euclidean distance.
   */
  double euclideanDistance(KdTreeNode node);
}
