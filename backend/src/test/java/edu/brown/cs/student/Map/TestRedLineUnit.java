package edu.brown.cs.student.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.brown.cs.student.server.RedLineHandler;
import edu.brown.cs.student.server.RedLineHandler.FeatureCollection;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import org.junit.jupiter.api.Test;

/**
 * This class contains the unit tests for the RedLineHandler. It tests the function
 * getFilteredData.
 */
public class TestRedLineUnit {

  /**
   * This test checks that the getFilteredData function will return FeatureCollection with no
   * features will be returned with parameters that cover zero area
   *
   * @throws IOException
   */
  @Test
  public void testNoAreaInBoundedBox() throws IOException {
    Path filePath = Path.of("src/jsonData/fullDownload.json");
    String contents = Files.readString(filePath);

    RedLineHandler redLineHander = new RedLineHandler();
    FeatureCollection filteredData = redLineHander.getFilteredData((float) 0.0, (float) 0.0,
        (float) 0.0, (float) 0.0, contents);
    assertEquals(filteredData, new FeatureCollection("FeatureCollection", Collections.emptyList()));
  }

  /**
   * This test checks that the getFilteredData function will return FeatureCollection with a list of
   * Features that only have Phoenix as the city for a set of latitudes/longitudes contained within
   * the Phoenix area
   *
   * @throws IOException
   */
  @Test
  public void testPhoenixQueryParams() throws IOException {
    Path filePath = Path.of("src/jsonData/fullDownload.json");
    String contents = Files.readString(filePath);

    RedLineHandler redLineHander = new RedLineHandler();
    FeatureCollection filteredData = redLineHander.getFilteredData((float) 33.46, (float) 33.5,
        (float) -112.08, (float) -112, contents);
    String cityName1 = filteredData.features().get(1).properties().get("city").toString();
    for (int i = 0; i < filteredData.features().size(); i++) {
      String cityName = filteredData.features().get(i).properties().get("city").toString();
      assertEquals(cityName, "Phoenix");
    }
  }

  /**
   * This test checks that the getFilteredData function will return FeatureCollection with no
   * features will be returned with parameters that correspond to an area in the ocean
   *
   * @throws IOException
   */
  @Test
  public void testNoFeaturesInParameters() throws IOException {
    Path filePath = Path.of("src/jsonData/fullDownload.json");
    String contents = Files.readString(filePath);

    RedLineHandler redLineHander = new RedLineHandler();
    FeatureCollection filteredData = redLineHander.getFilteredData((float) 30, (float) 32,
        (float) -40, (float) -38, contents);
    assertEquals(filteredData, new FeatureCollection("FeatureCollection", Collections.emptyList()));
  }


  /**
   * This fuzz test checks that the getFilteredData function will always return a FeatureCollection
   * containing a string dictating the type and a List with any input for min/max latitude and longitude
   *
   * @throws IOException
   */
  @Test
  public void FuzzTestParams() throws IOException {
    Path filePath = Path.of("src/jsonData/fullDownload.json");
    String contents = Files.readString(filePath);
    RedLineHandler redLineHander = new RedLineHandler();

    for (int i = 0; i < 1000; i++) {
      Float min_lat = (float) (Math.random() * 400) - 200;
      Float max_lat = (float) (Math.random() * 400) - 200;
      Float min_lon = (float) (Math.random() * 400) - 200;
      Float max_lon = (float) (Math.random() * 400) - 200;
      FeatureCollection filteredData = redLineHander.getFilteredData(min_lat, max_lat, min_lon,
          max_lon, contents);
      assertEquals(filteredData.type(),"FeatureCollection");
      assertEquals(filteredData.features().getClass(), ArrayList.class);
    }
  }

  /**
   * This test checks that the getFilteredData function will return FeatureCollection a valid feature
   * with properties state and city constructed in a mock dataset
   *
   * @throws IOException
   */
  @Test
  public void testMockData() throws IOException {
    Path filePath = Path.of("src/jsonData/mockDownload.json");
    String contents = Files.readString(filePath);

    RedLineHandler redLineHander = new RedLineHandler();
    FeatureCollection filteredData = redLineHander.getFilteredData((float) 29, (float) 31,
        (float) 29, (float) 31, contents);

    String cityName = filteredData.features().get(0).properties().get("city").toString();
    String stateName = filteredData.features().get(0).properties().get("state").toString();
    assertEquals(cityName, "San Francisco");
    assertEquals(stateName, "CA");
  }
}
