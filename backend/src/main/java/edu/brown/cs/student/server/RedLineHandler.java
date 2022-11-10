package edu.brown.cs.student.server;

import com.squareup.moshi.Moshi;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class RedLineHandler implements Route {
  private String min_lat;
  private String min_lon;
  private String max_lat;
  private String max_lon;

  public RedLineHandler() {
    this.min_lat = null;
    this.min_lon = null;
    this.max_lat = null;
    this.max_lon = null;
  }

    /**
   * This main method of the class dictates the processing and response of a request made to the API
   * server. If the user does not pass a latitude and longitude in the request,
   * weatherFailureResponse is called with a bad_request description. The method attempts to make a
   * request to the NWS API and retrieve a temperature given the user latitude and longitude. If
   * invalid coordinates are passed, weatherFailureResponse is called
   *
   * @param request
   * @param response
   * @return
   */
  @Override
  public Object handle(Request request, Response response) {
    // Retrieves latitude and longitude from requests
    this.min_lat = request.queryParams("min_lat");
    this.min_lon = request.queryParams("min_lon");
    this.max_lat = request.queryParams("min_lat");
    this.max_lon = request.queryParams("min_lon");

    if (this.min_lat == null || this.min_lon == null || this.max_lat == null || this.max_lon == null) {
      return redLineFailureResponse("error_bad_request");
    }

    Float minLatFloat = Float.parseFloat(this.min_lat);
    Float minLonFloat = Float.parseFloat(this.min_lon);
    Float maxLatFloat = Float.parseFloat(this.max_lat);
    Float maxLonFloat = Float.parseFloat(this.max_lon);
    
    Path filePath = Path.of("data/geoJSON/fullDownload.json");
    String fileContents = Files.readString(filePath);

    Moshi moshi = new Moshi.Builder().build();
    FeatureCollection data = moshi.adapter(FeatureCollection.class).fromJson(fileContents);

    List<Feature> filteredFeatures = new ArrayList<>();
    features: for(Feature feature: data.features){
        if(feature.geometry() == null){
            continue;
        }
        for (List<Float> boundaryPoint: feature.geometry.coordinates.get(0).get(0)){
            Float lat = boundaryPoint.get(1);
            Float lon = boundaryPoint.get(0);
            if(lat > maxLatFloat || lat < minLatFloat || lon > maxLatFloat || lon < minLonFloat){
                continue features;
            }
        }
        filteredFeatures.add(feature)
    }
    FeatureCollection collection = new FeatureCollection("FeatureCollection",filteredFeatures);
    return redLineSuccessResponse(collection);


    }


    public Object redLineSuccessResponse(FeatureCollection filteredData){
        Map<String,Object> responses = new HashMap<>();
        responses.put("result","success");
        responses.put("data", filteredData);
        responses.put("min_lat", this.min_lat);
        responses.put("min_lon", this.min_lon);
        responses.put("max_lat", this.max_lat);
        responses.put("max_lon", this.max_lon);

        Moshi moshi = new Moshi.Builder().build();
        return moshi.adapter(Map.class).toJson(responses);
    }
    
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
    
    
    public record FeatureCollection(String type, List<Feature> features) {}
    public record Feature(String type, Geometry geometry, Map<String, Object> properties) {}
    public record Geometry(String type, List<List<List<List<Float>>>> coordinates) {}
}
