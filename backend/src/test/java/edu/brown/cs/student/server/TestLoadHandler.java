package edu.brown.cs.student.server;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.csv.CSVData;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

public class TestLoadHandler {

  /** Set spark port to 0 before test suite is run */
  @BeforeAll
  public static void setup_before_everything() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root logger
  }

  /**
   * Shared state for all tests. We need to be able to mutate it but never need to replace the
   * reference itself. We clear this state out after every test runs.
   */
  CSVData data = new CSVData();

  /**
   * Before each test is run, fields in instance of CSVData are initialized, endpoint is set, and
   * new instance of LoadHandler class is created with instance of CSVData passed in as argument
   * Server is started
   */
  @BeforeEach
  public void setup() {
    // Re-initializes state, etc. for _every_ test method run
    data.setData(null);
    data.setIsLoaded(false);

    // Restarts the entire Spark server for every test
    Spark.get("loadcsv", new LoadHandler(data));
    Spark.init();
    Spark.awaitInitialization(); // don't continue until the server is listening
  }

  /** After each test is run, the endpoint is removed and server is stopped. */
  @AfterEach
  public void teardown() {
    // Gracefully stop Spark listening on both endpoints
    Spark.unmap("/loadcsv");
    Spark.stop();
    Spark.awaitStop();
  }

  /**
   * Helper to start a connection to a specific API endpoint/params
   *
   * @param apiCall the call string, including endpoint (NOTE: this would be better if it had more
   *     structure!)
   * @return the connection for the given URL, just after connecting
   * @throws IOException if the connection fails for some reason
   */
  private static HttpURLConnection tryRequest(String apiCall) throws IOException {
    // Configures the connection
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    clientConnection.connect();
    return clientConnection;
  }

  /**
   * Test case in which user does not provide filepath
   *
   * @throws IOException
   */
  @Test
  public void testLoadNoFilepath() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Map<String, Object> responses =
        moshi.adapter(Map.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals(Map.of("result", "error_bad_request"), responses);

    clientConnection.disconnect();
  }

  /**
   * Test case in which user passes in filepath with no corresponding file
   *
   * @throws IOException
   */
  @Test
  public void testLoadNotExistingFile() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=data/stars/random.csv");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Map<String, Object> responses =
        moshi.adapter(Map.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals(
        Map.of("result", "error_datasource", "filepath", "data/stars/random.csv"), responses);

    clientConnection.disconnect();
  }

  /**
   * Test case in which user passes in valid filepath and existing file, but in invalid form
   *
   * @throws IOException
   */
  @Test
  public void testLoadImproperFile() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=data/stars/improper.csv");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Map<String, Object> responses =
        moshi.adapter(Map.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals(Map.of("result", "success", "filepath", "data/stars/improper.csv"), responses);

    clientConnection.disconnect();
  }

  /**
   * Test case in which user passes in a filepath not in the /data folder
   *
   * @throws IOException
   */
  @Test
  public void testLoadWrongFolder() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=info/stars/improper.csv");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Map<String, Object> responses =
        moshi.adapter(Map.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals(
        Map.of("result", "error_datasource", "filepath", "info/stars/improper.csv"), responses);

    clientConnection.disconnect();
  }

  /**
   * Tests case in which user passed in filepath to valid file
   *
   * @throws IOException
   */
  @Test
  public void testLoadProperFile() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=data/stars/stardata.csv");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Map<String, Object> responses =
        moshi.adapter(Map.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals(Map.of("result", "success", "filepath", "data/stars/stardata.csv"), responses);

    clientConnection.disconnect();
  }

  /**
   * Test case in which user passes in improper request for query parameter
   *
   * @throws IOException
   */
  @Test
  public void testLoadImproperRequest() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?sdfasd=sdf");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Map<String, Object> responses =
        moshi.adapter(Map.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals(Map.of("result", "error_bad_request"), responses);

    clientConnection.disconnect();
  }
}
