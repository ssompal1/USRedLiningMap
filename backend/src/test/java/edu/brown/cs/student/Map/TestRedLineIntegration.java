package edu.brown.cs.student.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.server.RedLineHandler;
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


/**
 * This class contains the integration test for the RedLineHandler. It tests that requests to
 * API server are successful and return expected geoJSON data filtered by latitude/longitude bounds
 */
public class TestRedLineIntegration {

  /** sets up the port before testing */
  @BeforeAll
  public static void setup_spark() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING);
  }

  /** prepares the endpoint before each test */
  @BeforeEach
  public void setup() {
    // Sets up the endpoint and handler
    Spark.get("redline", new RedLineHandler());
    Spark.init();
    Spark.awaitInitialization();
  }

  /** stops the spark listening on the endpoint after each integration test, */
  @AfterEach
  public void teardown() {
    // Stop Spark listening on the endpoint
    Spark.unmap("redline");
    Spark.awaitStop();
  }

  private static HttpURLConnection tryRequest(String apiCall) throws IOException {
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    clientConnection.connect();
    return clientConnection;
  }

  /**
   * This test checks that a bad error request will be returned if no query parameters are passed in
   *
   * @throws IOException
   */
  @Test
  public void testRedLineNoParameters() throws IOException {
    HttpURLConnection clientConnection = tryRequest("redline");
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Map<String, Object> responses =
        moshi.adapter(Map.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals(Map.of("result", "error_bad_request"), responses);

    clientConnection.disconnect();
  }


  /**
   * This test checks that a bad error request will be returned if query parameters corresponding
   * to non-existent latitudes/longitudes are passed in
   *
   * @throws IOException
   */
  @Test
  public void testRedLineNonExistentParams() throws IOException {
    String apiCall = "redline?min_lon=987&max_lon=-112&min_lat=33&max_lat=10";
    HttpURLConnection clientConnection = tryRequest(apiCall);
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Map<String, Object> response =
        moshi.adapter(Map.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("error_bad_request", response.get("result"));

    clientConnection.disconnect();
  }

  /**
   * This test checks that a bad error request will be returned if query parameters that can't be
   * converted to floats are passed in
   * @throws IOException
   */
  @Test
  public void testRedLineNonFloatParams() throws IOException {
    String apiCall = "redline?min_lon=hello&max_lon=hi&min_lat=invalid&max_lat=params";
    HttpURLConnection clientConnection = tryRequest(apiCall);
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Map<String, Object> response =
        moshi.adapter(Map.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("error_bad_request", response.get("result"));

    clientConnection.disconnect();
  }

  /**
   * This test checks that a bad error request will be returned if not all
   * query parameters are passed in
   * @throws IOException
   */
  @Test
  public void testRedLineMissingParameters() throws IOException {
    String apiCall = "redline?min_lon=-112.09&max_lon=33.5&min_lat=33.46";
    HttpURLConnection clientConnection = tryRequest(apiCall);
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Map<String, Object> response =
        moshi.adapter(Map.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("error_bad_request", response.get("result"));

    clientConnection.disconnect();
  }

  /**
   * This test checks that a bad error request will be returned if the max lat/lon are greater than
   * the min for query parameters
   * @throws IOException
   */
  @Test
  public void testMinGreaterThanMax() throws IOException {
    String apiCall = "redline?min_lon=-112.048&max_lon=-112&min_lat=33.4&max_lat=1";
    HttpURLConnection clientConnection = tryRequest(apiCall);
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Map<String, Object> response =
        moshi.adapter(Map.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("error_bad_request", response.get("result"));

    clientConnection.disconnect();
  }

  /**
   * This test checks that a successful result is returned with valid query parameters
   * @throws IOException
   */
  @Test
  public void testValidParameters() throws IOException{
    String apiCall = "redline?min_lon=-74.00&max_lon=-73.90&min_lat=40.71&max_lat=40.77";
    HttpURLConnection clientConnection = tryRequest(apiCall);
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    Map<String, Object> response =
        moshi.adapter(Map.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("success", response.get("result"));

    clientConnection.disconnect();
  }


/**
   * This fuzz test checks that a connection to the server is always being made and that a
 * result is always being returned regardless of the validity of the parameters
   */
  @Test
  public void testFuzzTestIntegration() throws IOException {
    for(int i=0; i<1000;i++) {
      String min_lat = String.valueOf((Math.random() * 400) - 200);
      String max_lat = String.valueOf((Math.random() * 400) - 200);
      String min_lon = String.valueOf((Math.random() * 400) - 200);
      String max_lon = String.valueOf((Math.random() * 400) - 200);
      String apiCall = "redline?min_lon="+min_lon+"&max_lon="+max_lon+"&min_lat="+min_lat+"&max_lat="+max_lat;
      HttpURLConnection clientConnection = tryRequest(apiCall);
      assertEquals(200, clientConnection.getResponseCode());
      Moshi moshi = new Moshi.Builder().build();
      Map<String, Object> response =
          moshi.adapter(Map.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
      assertEquals(response.containsKey("result"), true);

      clientConnection.disconnect();
    }
  }
}