package edu.brown.cs.student.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.squareup.moshi.Moshi;
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

public class TestWeatherHandler {
  /** Set spark port to 0 before test suite is run */
  @BeforeAll
  public static void setup_before_everything() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root logger
  }

  /**
   * Before each test is run, endpoint created with new instance of WeatherHandler class Server is
   * started
   */
  @BeforeEach
  public void setup() {
    // Restarts the entire Spark server for every test
    Spark.get("weather", new WeatherHandler());
    Spark.init();
    Spark.awaitInitialization(); // don't continue until the server is listening
  }

  /** After each test is run, the endpoint is removed and server is stopped. */
  @AfterEach
  public void teardown() {
    // Gracefully stop Spark listening on both endpoints
    Spark.unmap("/weather");
    Spark.stop();
    Spark.awaitStop();
  }

  /**
   * Helper to start a connection to a specific API endpoint/params
   *
   * @param apiCall the call string, including endpoint
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
   * Tests case in which user does not pass in a latitude or longitude
   *
   * @throws IOException
   */
  @Test
  public void testWeatherNoParameters() throws IOException {
    HttpURLConnection clientConnection = tryRequest("weather");
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Map<String, Object> responses =
        moshi.adapter(Map.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals(Map.of("result", "error_bad_request"), responses);

    clientConnection.disconnect();
  }

  /**
   * Tests weather request without a longitude
   *
   * @throws IOException
   */
  @Test
  public void testWeatherNoLongitude() throws IOException {
    HttpURLConnection clientConnection = tryRequest("weather?lat=41.8240");
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Map<String, Object> responses =
        moshi.adapter(Map.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals(Map.of("result", "error_bad_request", "lat", "41.8240"), responses);
    clientConnection.disconnect();
  }

  /**
   * Tests weather request with unusual parameters
   *
   * @throws IOException
   */
  @Test
  public void testWeatherOddRequest() throws IOException {
    HttpURLConnection clientConnection = tryRequest("weather?asdf;");
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Map<String, Object> responses =
        moshi.adapter(Map.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals(Map.of("result", "error_bad_request"), responses);
    clientConnection.disconnect();
  }

  /**
   * Test case in which the user passes in an invalid longitude
   *
   * @throws IOException
   */
  @Test
  public void testWeatherImproperLongitude() throws IOException {
    HttpURLConnection clientConnection = tryRequest("weather?lat=41.8240&lon=71.4128");
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Map<String, Object> responses =
        moshi.adapter(Map.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals(Map.of("result", "error_bad_json", "lat", "41.8240", "lon", "71.4128"), responses);

    clientConnection.disconnect();
  }

  /**
   * Test case in which the user passes in a proper latitude and longitude
   *
   * @throws IOException
   */
  @Test
  public void testWeatherProperRequest() throws IOException {
    HttpURLConnection clientConnection = tryRequest("weather?lat=41.8240&lon=-71.4128");
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Map<String, Object> responses =
        moshi.adapter(Map.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Double temperature = (Double) responses.get("temperature");

    assertEquals(responses.get("result"), "success");
    assertEquals(responses.get("lat"), "41.8240");
    assertEquals(responses.get("lon"), "-71.4128");
    assertTrue(temperature < 150 && temperature > -150);

    clientConnection.disconnect();
  }

  /**
   * Test case in which the user passes in a proper latitude and longitude that must be truncated
   *
   * @throws IOException
   */
  @Test
  public void testWeatherProperTruncateRequest() throws IOException {
    HttpURLConnection clientConnection = tryRequest("weather?lat=33.494171&lon=-111.926048");
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Map<String, Object> responses =
        moshi.adapter(Map.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Double temperature = (Double) responses.get("temperature");

    assertEquals(responses.get("result"), "success");
    assertEquals(responses.get("lat"), "33.494171");
    assertEquals(responses.get("lon"), "-111.926048");
    assertTrue(temperature < 150 && temperature > -150);

    clientConnection.disconnect();
  }
}
