package edu.brown.cs.student.main;

import edu.brown.cs.student.csv.CSVParser;
import edu.brown.cs.student.csv.FactoryFailureException;
import edu.brown.cs.student.kdtree.DistanceSorter;
import edu.brown.cs.student.kdtree.KdTree;
import edu.brown.cs.student.stars.GalaxyGenerator;
import edu.brown.cs.student.stars.Star;
import edu.brown.cs.student.stars.StarFactory;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

/** The Main class of our project. This is where execution begins. */
public final class Main {
  /**
   * The initial method called when execution begins.
   *
   * @param args An array of command line arguments
   * @throws IOException error from reading file
   * @throws FactoryFailureException error from creating object
   */
  public static void main(String[] args) throws IOException, FactoryFailureException {
    new Main(args).run();
  }

  private final String[] args;

  private Main(String[] args) {
    this.args = args;
  }

  private void run() throws IOException, FactoryFailureException {
    // prints out command line arguments; can remove this
    System.out.println(Arrays.toString(args));

    // generates galaxy of stars, computes nearest neighbor for all
    if (args.length == 2 && args[0].equals("generate_galaxy")) {
      int numStars = 0;
      try {
        numStars = Integer.parseInt(args[1]);
      } catch (Exception ignored) {
        System.err.println("ERROR: Could not parse number of stars to generate.");
      }
      List<Star> galaxy = GalaxyGenerator.generate(numStars);
      KdTree<Star> starKdTree = new KdTree<>(galaxy, 0);
      for (Star star : galaxy) {
        PriorityQueue<Star> pq =
            starKdTree.kdTreeSearch(
                "neighbors", 1, star, new DistanceSorter(star), new HashSet<>());
        System.out.println(pq.peek());
      }
    } else if (args.length == 2 && args[0].equals("count")) {
      // Computes count for document (words, characters, rows, columns)

      String filePathway = args[1];

      // Attempts to create a FileReader object and performs count on file
      try {
        // Creates a file
        File file = new File(filePathway);
        FileReader fr = new FileReader(file);

        // Creates CSVParser object and runs analysis of file
        StarFactory sf = new StarFactory();
        CSVParser<Star> parser = new CSVParser<>(fr, sf);
        parser.count();

        // Return data to user
        System.out.println("Word Count: " + parser.getWordCount());
        System.out.println("Character Count: " + parser.getCharacterCount());
        System.out.println("Row Count: " + parser.getRowCount());
        System.out.println("Column Count: " + parser.getColumnCount());
      } catch (IOException e) { // IOException
        System.err.println("ERROR: The file cannot be read");
        throw e;
      }
    } else if (args.length == 2 && args[0].equals("create")) {
      // Creates a List of Star objects given a CSV of star data

      String filePathway = args[1];

      // Attempts to create a FileReader object and create objects from CSV
      try {
        // Creates a file
        File file = new File(filePathway);
        FileReader fr = new FileReader(file);

        // Creates CSVParser object and runs analysis of file
        StarFactory sf = new StarFactory();
        CSVParser<Star> parser = new CSVParser<>(fr, sf);
        parser.create();
      } catch (IOException e) { // IOException
        System.err.println("ERROR: The file cannot be read");
        throw e;
      } catch (FactoryFailureException f) { // FactoryFailureException
        System.err.println("ERROR: a row cannot be converted to an object!");
        throw f;
      }
    }
  }
}
