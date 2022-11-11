package edu.brown.cs.student.Map;

import edu.brown.cs.student.server.RedLineHandler;
import java.util.ArrayList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import spark.Spark;
import com.squareup.moshi.Moshi;

public class IntergrationTest {

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

  /** stops the spark listening on the endpoint after each integration test,  */
  @AfterEach
  public void teardown() {
    // Stop Spark listening on the endpoint
    Spark.unmap("geoJSON");
    Spark.awaitStop(); 
  }

    private static HttpURLConnection tryRequest(String apiCall) throws IOException {
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    clientConnection.connect();
    return clientConnection;
  }

  /**
   * Tests if the GeoJSON is being filtered correctly for the given valid lat-lon ranges
   * @throws IOException
   */

@Test
  public void testValidParams() throws IOException {
  String apiCall = "redline?min_lon=-75.4&max_lon=-75&min_lat=39.95&max_lat=40";
    HttpURLConnection clientConnection = tryRequest(apiCall);
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Map<String, Object> response =
        moshi.adapter(Map.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("FeatureCollection", response.get("type"));
    List features = (List) response.get("features");
    Map firstFeature = (Map) features.get(0);
    Map firstFeatureProps = (Map) firstFeature.get("properties");
    String city = (String) firstFeatureProps.get("city");
    String holc_grade = (String) firstFeatureProps.get("holc_grade");
    assertEquals("Phoenix", city);
    assertEquals("A", holc_grade);
  }

  @Test
  public void testRedLineNoParameters() throws IOException {
    // HttpURLConnection clientConnection = tryRequest("redline");
    // assertEquals(200, clientConnection.getResponseCode());

    // Moshi moshi = new Moshi.Builder().build();
    // Map<String, Object> responses =
    //     moshi.adapter(Map.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    // assertEquals(Map.of("result", "error_bad_request"), responses);

    // clientConnection.disconnect();
  }

  @Test
  public void testRedLineMissingParameters() throws IOException {
    String apiCall = "red_line?min_lon=-112.09&max_lon=33.5&min_lat=33.46";
    HttpURLConnection clientConnection = tryRequest(apiCall);
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Map<String, Object> response =
        moshi.adapter(Map.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("error_bad_request", response.get("result"));
  }

/**
   * tests if invalid params will return an error 
   * @throws IOException
   */
  @Test
  public void testRedLineInvalidParams() throws IOException {
    String apiCall = "redline?min_lon=987&max_lon=-75&min_lat=89.54&max_lat=10";
    HttpURLConnection clientConnection = tryRequest(apiCall);
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Map<String, Object> response =
        moshi.adapter(Map.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("error_bad_request", response.get("result"));
  }

@Test
  public void testMinGreaterThanMax() throws IOException {
    //String apiCall = "red_line?min_lon=-112.06&max_lon=33.46&min_lat=33.5&max_lat=-112.09";
  //String apiCall = "redline?min_lon=40&max_lon=30&min_lat=39.95&max_lat=50";
    String apiCall = "redline?min_lon=-75.4&max_lon=-75&min_lat=39.95&max_lat=1";
    HttpURLConnection clientConnection = tryRequest(apiCall);
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Map<String, Object> response =
          moshi.adapter(Map.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals(new ArrayList<>(), response.get("features"));
    assertEquals("FeatureCollection", response.get("type"));
  }
}
  

