package edu.brown.cs.student.server;

import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** Handler class for returning filtered redlining data "redline" API endpoint */
public class RedLineHandler implements Route {
  private String min_lat;
  private String min_lon;
  private String max_lat;
  private String max_lon;

  // initalize instance variables corresponding to query parameters to null
  public RedLineHandler() {
    this.min_lat = null;
    this.min_lon = null;
    this.max_lat = null;
    this.max_lon = null;
  }

  /**
   * This main method of the class dictates the processing and response of a request made to the API
   * server. If the user does not pass a valid minimum and maximum latitude and a minimum and maximum
   * longitude in the request, redLineFailureResponse is called with a bad_request description. The
   * method attempts to read from the geoJSON file and retrieve a FeaturesCollection given the user
   * min/max latitude and longitude.
   *
   * @param request
   * @param response
   * @return
   */
  @Override
  // http://localhost:3232/redline?min_lat=33.464099&max_lat=33.475366&min_lon=-112.093494&max_lon=-112.061602
  public Object handle(Request request, Response response) {
    // Retrieves latitude and longitude from requests
    this.min_lat = request.queryParams("min_lat");
    this.min_lon = request.queryParams("min_lon");
    this.max_lat = request.queryParams("max_lat");
    this.max_lon = request.queryParams("max_lon");

    // checks if user passed a min/max latitude & longitude
    if (this.min_lat == null
        || this.min_lon == null
        || this.max_lat == null
        || this.max_lon == null) {
      return redLineFailureResponse("error_bad_request");
    }

    Float minLatFloat = Float.parseFloat(this.min_lat);
    Float minLonFloat = Float.parseFloat(this.min_lon);
    Float maxLatFloat = Float.parseFloat(this.max_lat);
    Float maxLonFloat = Float.parseFloat(this.max_lon);


    //checks condition of invalid latitudes and longitudes
    if(minLatFloat < -90 || minLatFloat > 90 || minLonFloat < -180 || minLonFloat > 180 ||
        maxLatFloat < -90 || maxLatFloat > 90 || maxLonFloat < -180 || maxLonFloat > 180){
      return redLineFailureResponse("error_bad_request");
    }

    //checks condition of minimums being greater than maximums
    if(minLatFloat > maxLatFloat || minLonFloat > maxLonFloat){
      return redLineFailureResponse("error_bad_request");
    }
    Path filePath = Path.of("src/mockData/fullDownload.json");
    String fileContents = null;
    try {
      fileContents = Files.readString(filePath);
      System.out.println("successfully read");
    } catch (IOException e) {
      return redLineFailureResponse("error_datasource");
    }
    return this.redLineSuccessResponse(this.getFilteredData(minLatFloat,maxLatFloat,minLonFloat,maxLonFloat, fileContents));
  }

    /**
    * This is the helper function of handle. It takes in the query parameters as Floats and returns the results of either
    * redLineFailureResponse calls or redLineSuccessResponse calls. This function reads the geoJSON, and serializes the data 
    * into a FeatureCollection. Based on the query parameters, Features that fit the query parameters are added to the Collection
    * and Features that do not are passed over in a for loop. Features with null geometry fields are also passed over. 
    * A call to redLineSuccessResponse is made, passing in the Collection as an argument and returning its result. 
     */
    public FeatureCollection getFilteredData(Float minLatFloat, Float maxLatFloat, Float minLonFloat,Float maxLonFloat, String fileContents){

    System.out.println("made it here");

    Moshi moshi = new Moshi.Builder().build();
    FeatureCollection data = null;
    try {
      data = moshi.adapter(FeatureCollection.class).fromJson(fileContents);
      System.out.println("Successfully converted");
    } catch (IOException e) {
    }

    // filters through different regions and add features that meet query parameters of bounded box
    List<Feature> filteredFeatures = new ArrayList<>();
    features:
    for (Feature feature : data.features) {
      if (feature.geometry() == null) {
        continue;
      }
      for (List<Float> boundaryPoint : feature.geometry.coordinates.get(0).get(0)) {
        Float lat = boundaryPoint.get(1);
        Float lon = boundaryPoint.get(0);
        if (lat > maxLatFloat || lat < minLatFloat || lon > maxLonFloat || lon < minLonFloat) {
          continue features;
        }
      }
      System.out.println("adding feature");
      filteredFeatures.add(feature);
    }
    FeatureCollection collection = new FeatureCollection("FeatureCollection", filteredFeatures);
    // return collection of Features as a success response
    return collection;
  }

  /**
   * A collection of features passed in as a parameter. A map is created returning a successful
   * result, the collection, and the values of the passed in query parameters
   */
  public Object redLineSuccessResponse(FeatureCollection filteredData) {
    System.out.println(filteredData);
    Map<String, Object> responses = new HashMap<>();
    responses.put("result", "success");
    responses.put("data", filteredData);
    responses.put("min_lat", this.min_lat);
    responses.put("min_lon", this.min_lon);
    responses.put("max_lat", this.max_lat);
    responses.put("max_lon", this.max_lon);

    // serialize responses into JSON format
    Moshi moshi = new Moshi.Builder().build();
    return moshi.adapter(Map.class).toJson(responses);
  }

  /**
  * A string containing a description of the error type passed in as a parameter. A map is created returning the 
  * error result, and the values of the passed in query parameters
   */
  public Object redLineFailureResponse(String responseType) {
    // Creates map with failure response
    Map<String, Object> responses = new HashMap<>();
    responses.put("result", responseType);
    responses.put("min_lat", this.min_lat);
    responses.put("min_lon", this.min_lon);
    responses.put("max_lat", this.min_lat);
    responses.put("max_lon", this.min_lon);

    // Serializes responses into JSON format
    Moshi moshi = new Moshi.Builder().build();
    return moshi.adapter(Map.class).toJson(responses);
  }

  //designate types for data forms forming the basis of organization for the geoJSON
  public record FeatureCollection(String type, List<Feature> features) {}

  public record Feature(String type, Geometry geometry, Map<String, Object> properties) {}

  public record Geometry(String type, List<List<List<List<Float>>>> coordinates) {}
}
