package edu.brown.cs.student.server;

import static spark.Spark.after;

import edu.brown.cs.student.csv.CSVData;
import spark.Spark;

/**
 * Top-level class for this demo. Contains the main() method which starts Spark and runs the various
 * handlers. We have three endpoints corresponding to each of the Handlers
 * (GetHandler,LoadHandler,WeatherHandler) The instance of CSVData acts as a shared state so the
 * GetHandler and LoadHandler classes can access and mutate the same set of data
 */
public class Server {
  public static void main(String[] args) {
    Spark.port(3230);

    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });

    // Creates a shared state for load and get processes
    CSVData data = new CSVData();

    // Setting up the handlers for the GET loadCSV, getCSV, and weather endpoints
    Spark.get("loadcsv", new LoadHandler(data));
    Spark.get("getcsv", new GetHandler(data));
    Spark.get("weather", new WeatherHandler());
    Spark.get("redline", new RedLineHandler());
    Spark.init();
    Spark.awaitInitialization();
    System.out.println("Server started.");
  }
}
