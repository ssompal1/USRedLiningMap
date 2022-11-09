package edu.brown.cs.student.stars;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.brown.cs.student.kdtree.DistanceSorter;
import edu.brown.cs.student.kdtree.KdTree;
import edu.brown.cs.student.kdtree.KdTreeNode;
import edu.brown.cs.student.kdtree.Point;
import java.util.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class TestKdTree {
  private KdTree<KdTreeNode> kdTree;

  /** Sets up the k-d Tree using an odd number of 2d points. */
  public void setUpOddPoints() {
    List<KdTreeNode> points = new ArrayList<>();
    points.add(new Point(new double[] {4, 1}));
    points.add(new Point(new double[] {0, 5}));
    points.add(new Point(new double[] {3, 6}));
    points.add(new Point(new double[] {6, 5}));
    points.add(new Point(new double[] {7, 0}));
    points.add(new Point(new double[] {6, 2}));
    points.add(new Point(new double[] {6, 4}));
    this.kdTree = new KdTree<>(points, 0);
  }

  /** Sets up the k-d Tree using an even number of 2d points. */
  public void setUpEvenPoints() {
    List<KdTreeNode> points = new ArrayList<>();
    points.add(new Point(new double[] {4, 1}));
    points.add(new Point(new double[] {0, 5}));
    points.add(new Point(new double[] {3, 6}));
    points.add(new Point(new double[] {6, 5}));
    points.add(new Point(new double[] {7, 0}));
    points.add(new Point(new double[] {6, 2}));
    points.add(new Point(new double[] {6, 4}));
    points.add(new Point(new double[] {1, 0}));
    this.kdTree = new KdTree<>(points, 0);
  }

  /** Sets up the k-d Tree using an empty ArrayList of nodes. */
  public void setUpEmptyTree() {
    List<KdTreeNode> points = new ArrayList<>();
    this.kdTree = new KdTree<>(points, 0);
  }

  /** Resets the k-d Tree. */
  @AfterEach
  public void tearDown() {
    this.kdTree = null;
  }

  /** * Tests whether the k-d Tree has the correct root node. */
  @Test
  public void testCorrectRoot() {
    setUpOddPoints();

    assertEquals(2, kdTree.getHead().getDimension());
    assertEquals("[6.0, 5.0]", Arrays.toString(kdTree.getHead().getPoint()));
  }

  /** * Tests whether the k-d Tree's root has the correct children nodes. */
  @Test
  public void testCorrectChildren() {
    setUpOddPoints();

    KdTree<KdTreeNode> left = kdTree.getLeft();
    KdTree<KdTreeNode> right = kdTree.getRight();

    assertEquals(2, left.getHead().getDimension());
    assertEquals(2, right.getHead().getDimension());
    assertEquals("[0.0, 5.0]", Arrays.toString(left.getHead().getPoint()));
    assertEquals("[6.0, 2.0]", Arrays.toString(right.getHead().getPoint()));
  }

  /** * Tests whether the k-d Tree's root's children have the correct children nodes. */
  @Test
  public void testCorrectChildrensChildren() {
    setUpOddPoints();

    KdTree<KdTreeNode> leftLeft = kdTree.getLeft().getLeft();
    KdTree<KdTreeNode> leftRight = kdTree.getLeft().getRight();

    KdTree<KdTreeNode> rightLeft = kdTree.getRight().getLeft();
    KdTree<KdTreeNode> rightRight = kdTree.getRight().getRight();

    assertEquals(2, leftLeft.getHead().getDimension());
    assertEquals(2, leftRight.getHead().getDimension());
    assertEquals(2, rightLeft.getHead().getDimension());
    assertEquals(2, rightRight.getHead().getDimension());

    assertEquals("[4.0, 1.0]", Arrays.toString(leftLeft.getHead().getPoint()));
    assertEquals("[3.0, 6.0]", Arrays.toString(leftRight.getHead().getPoint()));
    assertEquals("[7.0, 0.0]", Arrays.toString(rightLeft.getHead().getPoint()));
    assertEquals("[6.0, 4.0]", Arrays.toString(rightRight.getHead().getPoint()));
  }
  /** * Tests whether the k-d Tree's initializes correctly with an empty input list of nodes. */
  @Test
  public void testEmptyTree() {
    setUpEmptyTree();

    assertNull(kdTree.getHead());
    assertNull(kdTree.getLeft());
    assertNull(kdTree.getRight());
    assertEquals(kdTree.getKdTreeNodes().size(), 0);
    assertEquals(kdTree.getAxis(), 0);
  }

  /** * Tests whether the k-d Tree's initializes correctly an even number of nodes. */
  @Test
  public void testEvenNumNodes() {
    setUpEvenPoints();

    assertEquals(2, kdTree.getHead().getDimension());
    assertEquals("[6.0, 5.0]", Arrays.toString(kdTree.getHead().getPoint()));

    KdTree<KdTreeNode> left = kdTree.getLeft();
    KdTree<KdTreeNode> right = kdTree.getRight();

    assertEquals(2, left.getHead().getDimension());
    assertEquals(2, right.getHead().getDimension());
    assertEquals("[0.0, 5.0]", Arrays.toString(left.getHead().getPoint()));
    assertEquals("[6.0, 2.0]", Arrays.toString(right.getHead().getPoint()));

    KdTree<KdTreeNode> leftLeft = kdTree.getLeft().getLeft();
    KdTree<KdTreeNode> leftRight = kdTree.getLeft().getRight();
    KdTree<KdTreeNode> rightLeft = kdTree.getRight().getLeft();
    KdTree<KdTreeNode> rightRight = kdTree.getRight().getRight();

    assertEquals(2, leftLeft.getHead().getDimension());
    assertEquals(2, leftRight.getHead().getDimension());
    assertEquals(2, rightLeft.getHead().getDimension());
    assertEquals(2, rightRight.getHead().getDimension());

    assertEquals("[4.0, 1.0]", Arrays.toString(leftLeft.getHead().getPoint()));
    assertEquals("[3.0, 6.0]", Arrays.toString(leftRight.getHead().getPoint()));
    assertEquals("[7.0, 0.0]", Arrays.toString(rightLeft.getHead().getPoint()));
    assertEquals("[6.0, 4.0]", Arrays.toString(rightRight.getHead().getPoint()));

    assertEquals("[1.0, 0.0]", Arrays.toString(leftLeft.getLeft().getHead().getPoint()));
  }

  /** * Tests nearest neighbors and radius search on an empty k-d tree. */
  @Test
  public void testEmptyTreeSearch() {
    setUpEmptyTree();

    Point targetPoint = new Point(new double[] {4, 1});
    PriorityQueue<KdTreeNode> neighbors =
        kdTree.kdTreeSearch(
            "neighbors", 5, targetPoint, new DistanceSorter(targetPoint), new HashSet<>());
    PriorityQueue<KdTreeNode> radius =
        kdTree.kdTreeSearch(
            "radius", 5, targetPoint, new DistanceSorter(targetPoint), new HashSet<>());

    assertEquals(neighbors.size(), 0);
    assertEquals(radius.size(), 0);
  }

  /** * Tests nearest neighbors search with k = 0. */
  @Test
  public void testKIs0() {
    setUpEvenPoints();

    Point targetPoint = new Point(new double[] {4, 1});
    PriorityQueue<KdTreeNode> neighbors =
        kdTree.kdTreeSearch(
            "neighbors", 0, targetPoint, new DistanceSorter(targetPoint), new HashSet<>());
    PriorityQueue<KdTreeNode> neighborsNaive =
        kdTree.naiveSearch("neighbors", 0, targetPoint, new HashSet<>());

    assertEquals(neighbors.size(), 0);
    assertArrayEquals(neighborsNaive.toArray(), neighbors.toArray());
  }

  /** * Tests radius search with r = 0 and a point present at targetPoint. */
  @Test
  public void testRIs0AtPoint() {
    setUpEvenPoints();

    Point targetPoint = new Point(new double[] {4, 1});
    PriorityQueue<KdTreeNode> radius =
        kdTree.kdTreeSearch(
            "radius", 0, targetPoint, new DistanceSorter(targetPoint), new HashSet<>());
    PriorityQueue<KdTreeNode> radiusNaive =
        kdTree.naiveSearch("radius", 0, targetPoint, new HashSet<>());

    assertEquals(radius.size(), 1);
    assert radius.peek() != null;
    assertEquals("[4.0, 1.0]", Arrays.toString(radius.peek().getPoint()));
    assertArrayEquals(radiusNaive.toArray(), radius.toArray());
  }

  /** * Tests radius search with r = 0 and no point present at targetPoint. */
  @Test
  public void testRIs0NoPoint() {
    setUpOddPoints();

    Point targetPoint = new Point(new double[] {5, 5});
    PriorityQueue<KdTreeNode> radius =
        kdTree.kdTreeSearch(
            "radius", 0, targetPoint, new DistanceSorter(targetPoint), new HashSet<>());
    PriorityQueue<KdTreeNode> radiusNaive =
        kdTree.naiveSearch("radius", 0, targetPoint, new HashSet<>());

    assertEquals(radius.size(), 0);
    assertArrayEquals(radiusNaive.toArray(), radius.toArray());
  }

  /** * Tests nearest neighbors search with and without a point to ignore. */
  @Test
  public void testNeighborsIgnore() {
    setUpOddPoints();

    Point targetPoint = new Point(new double[] {4, 1});
    HashSet<KdTreeNode> ignore = new HashSet<>();
    ignore.add(targetPoint);

    PriorityQueue<KdTreeNode> neighborsIgnore =
        kdTree.kdTreeSearch("neighbors", 3, targetPoint, new DistanceSorter(targetPoint), ignore);
    PriorityQueue<KdTreeNode> neighborsIgnoreNaive =
        kdTree.naiveSearch("neighbors", 3, targetPoint, ignore);

    assertEquals(3, neighborsIgnore.size());
    assertFalse(neighborsIgnore.contains(targetPoint));
    assertArrayEquals(neighborsIgnoreNaive.toArray(), neighborsIgnore.toArray());
  }

  /** * Tests nearest neighbors search with and without a point to ignore. */
  @Test
  public void testNeighborsNoIgnore() {
    setUpOddPoints();

    Point targetPoint = new Point(new double[] {4, 1});

    PriorityQueue<KdTreeNode> neighborsNoIgnore =
        kdTree.kdTreeSearch(
            "neighbors", 3, targetPoint, new DistanceSorter(targetPoint), new HashSet<>());
    PriorityQueue<KdTreeNode> neighborsNoIgnoreNaive =
        kdTree.naiveSearch("neighbors", 3, targetPoint, new HashSet<>());

    assertEquals(3, neighborsNoIgnore.size());
    assertTrue(neighborsNoIgnore.contains(targetPoint));
    assertArrayEquals(neighborsNoIgnoreNaive.toArray(), neighborsNoIgnore.toArray());
  }

  /** * Tests radius search with a point to ignore. */
  @Test
  public void testRadiusIgnore() {
    setUpOddPoints();

    Point targetPoint = new Point(new double[] {4, 1});
    HashSet<KdTreeNode> ignore = new HashSet<>();
    ignore.add(targetPoint);

    PriorityQueue<KdTreeNode> radiusIgnore =
        kdTree.kdTreeSearch("radius", 4, targetPoint, new DistanceSorter(targetPoint), ignore);
    PriorityQueue<KdTreeNode> radiusIgnoreNaive =
        kdTree.naiveSearch("radius", 4, targetPoint, ignore);

    assertEquals(3, radiusIgnore.size());
    assertFalse(radiusIgnore.contains(targetPoint));
    assertArrayEquals(radiusIgnoreNaive.toArray(), radiusIgnore.toArray());
  }

  /** * Tests radius search without a point to ignore. */
  @Test
  public void testRadiusNoIgnore() {
    setUpOddPoints();

    Point targetPoint = new Point(new double[] {4, 1});

    PriorityQueue<KdTreeNode> radiusNoIgnore =
        kdTree.kdTreeSearch(
            "radius", 4, targetPoint, new DistanceSorter(targetPoint), new HashSet<>());
    PriorityQueue<KdTreeNode> radiusNoIgnoreNaive =
        kdTree.naiveSearch("radius", 4, targetPoint, new HashSet<>());

    assertEquals(4, radiusNoIgnore.size());
    assertTrue(radiusNoIgnore.contains(targetPoint));
    assertArrayEquals(radiusNoIgnoreNaive.toArray(), radiusNoIgnore.toArray());
  }
}
