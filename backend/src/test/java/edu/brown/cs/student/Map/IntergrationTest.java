package edu.brown.cs.student.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.server.RedLineHandler;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
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

//import static edu.brown.cs.student.server.RedLineHandler.getFilteredData;

import edu.brown.cs.student.server.RedLineHandler.FeatureCollection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import edu.brown.cs.student.server.RedLineHandler.Feature;



public class IntergrationTest {

  //RedLineHandler redLineHandler;

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
    RedLineHandler redLineHandler = new RedLineHandler();
    Spark.init();
    Spark.awaitInitialization();
  }

  /** stops the spark listening on the endpoint after each integration test, */
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
   * Tests if no parameters will return a bad error request 
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
   * tests if invalid params will return a bad error request 
   *
   * @throws IOException
   */
  @Test
  public void testRedLineInvalidParams() throws IOException {
    String apiCall = "redline?min_lon=987&max_lon=-112&min_lat=33&max_lat=10";
    HttpURLConnection clientConnection = tryRequest(apiCall);
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Map<String, Object> response =
        moshi.adapter(Map.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("error_bad_request", response.get("result"));
  }

  /**
   * tests if missing params will return a bad error request 
   *
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
  }

/**
   * tests if the min lat/lon is greather than the max will return a bad error request 
   *
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

    System.out.println(response.get("data"));
    assertEquals("error_bad_request", response.get("result"));

  }



//UNIT TESTING


 @Test
  public void testFilterDataNoArea() throws IOException {
    Path filePath = Path.of("src/mockData/fullDownload.json");
    String contents = Files.readString(filePath);

  
   RedLineHandler redLineHander = new RedLineHandler();
   FeatureCollection filteredData = redLineHander.getFilteredData ((float)0.0, (float)0.0,( float)0.0, (float)0.0, contents);
  assertEquals(filteredData, new FeatureCollection("FeatureCollection", Collections.emptyList()));
  }

  @Test
  public void testFilterProperRequest() throws IOException {
    Path filePath = Path.of("src/mockData/fullDownload.json");
    String contents = Files.readString(filePath);

   RedLineHandler redLineHander = new RedLineHandler();
    FeatureCollection filteredData = redLineHander.getFilteredData((float)33.46, (float)33.5, (float)-112.08, (float)-112, contents);
  assertEquals(filteredData, new FeatureCollection("FeatureCollection", Collections.emptyList()));
  }

  /**
   * Tests that a boundary box excluding the lon/lat of all features in the mocked json does not
   * return any features.
   * @throws IOException
   */
  @Test
  public void testMockNonInclusiveBoundedBox() throws IOException {
    String filepath = "src/mockData/fullDownload.json";
    System.out.println(Paths.get(filepath));
    String contents = new String(Files.readAllBytes((Paths.get(filepath))));

   RedLineHandler redLineHander = new RedLineHandler();
    FeatureCollection filteredData = redLineHander.getFilteredData((float)11, (float)11, (float)13, (float)13, contents);
    // assertEquals(filteredData.size(), 0);
  assertEquals(filteredData, new FeatureCollection("FeatureCollection", Collections.emptyList()));
  }

  /**
   * Tests that a boundary box that partially includes the mocked json features does not return
   * any features.
   * @throws IOException
   */
  @Test
  public void testMockPartialIntersection() throws IOException {
    Path filePath = Path.of("src/mockData/fullDownload.json");
    String contents = Files.readString(filePath);


   RedLineHandler redLineHander = new RedLineHandler();
    FeatureCollection filteredData = redLineHander.getFilteredData((float)2, (float)2, (float)5, (float)4, contents);
    // assertEquals(filteredData.size(), 0);
  assertEquals(filteredData, new FeatureCollection("FeatureCollection", Collections.emptyList()));
  }

  /**
   * Tests that a boundary box that includes the lat/lon of the mocked json features returns both
   * mocked json features.
   * @throws IOException
   */
  @Test
  public void testMockIncludesBothFeatures() throws IOException {
    Path filePath = Path.of("src/mockData/fullDownload.json");
    String contents = Files.readString(filePath);

   RedLineHandler redLineHander = new RedLineHandler();
    FeatureCollection filteredData = redLineHander.getFilteredData((float)-1, (float)-1, (float)3, (float)3, contents);
    // assertEquals(filteredData.size(), 2);
  }

  @Test
  public void testMockBoundedBoxIncludesOneFeature() throws IOException {
    Path filePath = Path.of("src/mockData/fullDownload.json");
    String contents = Files.readString(filePath);

   RedLineHandler redLineHander = new RedLineHandler();
    FeatureCollection filteredData = redLineHander.getFilteredData((float)-1, (float)-1, (float)4, (float)4, contents);
    //assertEquals(filteredData.size(), 1);
  }

  @Test
  public void testMockBoundedBoxFeatureBorder() throws IOException {
    Path filePath = Path.of("src/mockData/fullDownload.json");
    String contents = Files.readString(filePath);

   RedLineHandler redLineHander = new RedLineHandler();
   FeatureCollection filteredData = redLineHander.getFilteredData((float)0, (float)0, (float)3, (float)3, contents);
    //assertEquals(filteredData.size(), 1);
  }

/**
   * A fuzz test to check that no errors are returned given any lat-lon ranges, even those which are
   * invalid lat or lon values
   */

  @Test
  public void FuzzTestParams(){
    Path filePath = Path.of("src/mockData/fullDownload.json");
    //String contents = Files.readString(filePath);

    for(int i=0; i<1000;i++) {
      String min_lat = String.valueOf((Math.random() * 400) - 200);
      String max_lat = String.valueOf((Math.random() * 400) - 200);
      String min_lon = String.valueOf((Math.random() * 400) - 200);
      String max_lon = String.valueOf((Math.random() * 400) - 200);
      RedLineHandler handler = new RedLineHandler();
      //handler.getFilteredData(min_lat,max_lat,min_lon,max_lon, contents);
  //   }
  // }

  /**
   * A test to check that given a mock GeoJSON, the get filteredJSON will execute properly and
   * checks that the function is correctly checking all coordinates.
   */
  // @Test
  // public void mockGeoJSON(){
  //   RedLineHandler handler = new RedLineHandler();
  //   String filteredJson = handler.getFilteredJData("49","51","49","51","/Users/leoshack/Desktop/CS0320/integration-aremels-lshack/backend/test/mockGeoJSON.json");
  //   int zoneNumber = filteredJson.split("geometry").length -1;
  //   assertEquals(zoneNumber,1);
  //   String cityChunk = filteredJson.split("city")[1];
  //   String city = cityChunk.split("\"")[2];
  //   assertEquals(city,"Birmingham");
  // }



}