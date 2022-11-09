package edu.brown.cs.student.server;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.csv.CSVData;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

public class TestGetHandler {

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
   * new instance of GetHandler class is created with instance of CSVData passed in as argument
   * Server is started
   */
  @BeforeEach
  public void setup() {
    // Re-initializes state, etc. for _every_ test method run
    data.setData(null);
    data.setIsLoaded(false);

    // Restarts the entire Spark server for every test
    Spark.get("getcsv", new GetHandler(data));
    Spark.init();
    Spark.awaitInitialization(); // don't continue until the server is listening
  }

  /** After each test is run, the endpoint is removed and server is stopped. */
  @AfterEach
  public void teardown() {
    // Gracefully stop Spark listening on both endpoints
    Spark.unmap("/getcsv");
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
   * Test case in which user does not pass in a filepath
   *
   * @throws IOException
   */
  @Test
  public void testGetNoFileLoaded() throws IOException {
    HttpURLConnection clientConnection = tryRequest("getcsv");
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Map<String, Object> responses =
        moshi.adapter(Map.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals(Map.of("result", "error_bad_request"), responses);

    clientConnection.disconnect();
  }

  /**
   * Test case in which a CSV is loaded, but there was an issue with its assignment.
   *
   * @throws IOException
   */
  @Test
  public void testGetFileNotSet() throws IOException {
    this.data.setIsLoaded(true);

    HttpURLConnection clientConnection = tryRequest("getcsv");
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Map<String, Object> responses =
        moshi.adapter(Map.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals(Map.of("result", "error_datasource"), responses);

    clientConnection.disconnect();
  }

  /**
   * Test case in which proper CSV is loaded
   *
   * @throws IOException
   */
  @Test
  public void testGetFileLoaded() throws IOException {
    this.data.setIsLoaded(true);
    this.data.setData(List.of(List.of("0", "Sol", "0", "0", "0")));

    HttpURLConnection clientConnection = tryRequest("getcsv");
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Map<String, Object> responses =
        moshi.adapter(Map.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals(responses.get("result"), "success");
    assertEquals(responses.get("data").toString(), "[[0, Sol, 0, 0, 0]]");

    clientConnection.disconnect();
  }

  /**
   * Test case in which proper CSV is loaded and has a single character.
   *
   * @throws IOException
   */
  @Test
  public void testGetFileOneCharacter() throws IOException {
    this.data.setIsLoaded(true);
    this.data.setData(List.of(List.of("a")));

    HttpURLConnection clientConnection = tryRequest("getcsv");
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Map<String, Object> responses =
        moshi.adapter(Map.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals(responses.get("result"), "success");
    assertEquals(responses.get("data").toString(), "[[a]]");

    clientConnection.disconnect();
  }

  @Test
  public void testLoadImproperRequest() throws IOException {
    HttpURLConnection clientConnection = tryRequest("getcsv?sdfasd=sdf");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Map<String, Object> responses =
        moshi.adapter(Map.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals(Map.of("result", "error_bad_request"), responses);

    clientConnection.disconnect();
  }
}
