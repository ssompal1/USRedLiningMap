package edu.brown.cs.student.server;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.weather.Forecast;
import edu.brown.cs.student.weather.Weather;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** Handler class for retrieving temperature given latitude and longitude "weather" API endpoint */
public class WeatherHandler implements Route {
  private String lat;
  private String lon;

  public WeatherHandler() {
    this.lat = null;
    this.lon = null;
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
    this.lat = request.queryParams("lat");
    this.lon = request.queryParams("lon");

    // Determines if the user provided a latitude and longitude
    if (this.lat == null || this.lon == null) {
      return weatherFailureResponse("error_bad_request");
    }
    String latRounded = truncate(this.lat);
    String lonRounded = truncate(this.lon);

    // Retrieves temperature and returns a success response
    try {
      // Determines truncated values of latitude and longitude
      Integer temperature = getTemperature(latRounded, lonRounded);
      return weatherSuccessResponse(temperature);
    } catch (IOException ex) {
      return weatherFailureResponse("error_datasource");
    } catch (NullPointerException ex) {
      return weatherFailureResponse("error_bad_json");
    } catch (InterruptedException e) {
      return weatherFailureResponse("error_datasource");
    } catch (URISyntaxException e) {
      return weatherFailureResponse("error_datasource");
    }
  }

  /**
   * Format Strings for latitude and longitude
   *
   * @param numString
   * @return
   */
  public String truncate(String numString) {
    double numDouble = Double.parseDouble(numString);
    DecimalFormat df = new DecimalFormat("#.####");
    return df.format(numDouble);
  }

  /**
   * This method makes a request to the NWS API and deserializes the response to return an integer
   * for temperature
   *
   * @param latitude
   * @param longitude
   * @return
   * @throws Exception
   */
  public Integer getTemperature(String latitude, String longitude)
      throws IOException, InterruptedException, URISyntaxException, NullPointerException {
    String websiteLink = "https://api.weather.gov/points/" + latitude + "," + longitude;
    HttpRequest weatherRequest = HttpRequest.newBuilder().uri(new URI(websiteLink)).GET().build();
    HttpResponse<String> weatherResponse =
        HttpClient.newBuilder().build().send(weatherRequest, BodyHandlers.ofString());

    Moshi moshi = new Moshi.Builder().build();
    String forecastURL =
        moshi.adapter(Weather.class).fromJson(weatherResponse.body()).properties.forecast;

    HttpRequest forecastRequest = HttpRequest.newBuilder().uri(new URI(forecastURL)).GET().build();

    HttpResponse<String> forecastResponse =
        HttpClient.newBuilder().build().send(forecastRequest, BodyHandlers.ofString());

    Moshi moshi_two = new Moshi.Builder().build();
    Integer currentTemperature =
        moshi_two
            .adapter(Forecast.class)
            .fromJson(forecastResponse.body())
            .properties
            .periods
            .get(0)
            .temperature;
    return currentTemperature;
  }

  /**
   * This method executes when the user request is successfully completed. A HashMap is created with
   * a String stored denoting the successful result and the temperature along with the lat/lon The
   * map is serialized and returned in JSON string format.
   *
   * @return serialized JSON string of Map
   */
  public Object weatherSuccessResponse(Integer temperature) {
    // Creates map with failure response
    Map<String, Object> responses = new HashMap<>();
    responses.put("result", "success");
    responses.put("lat", this.lat);
    responses.put("lon", this.lon);
    responses.put("temperature", temperature);

    // Serializes responses into JSON format
    Moshi moshi = new Moshi.Builder().build();
    return moshi.adapter(Map.class).toJson(responses);
  }

  /**
   * This method executes when the user request cannot be successfully completed and a descriptive
   * error response must be returned.
   *
   * @param responseType- Descriptive error message based on issue with file/filepath
   * @return serialized JSON string of Map
   */
  public Object weatherFailureResponse(String responseType) {
    // Creates map with failure response
    Map<String, Object> responses = new HashMap<>();
    responses.put("result", responseType);
    responses.put("lat", this.lat);
    responses.put("lon", this.lon);

    // Serializes responses into JSON format
    Moshi moshi = new Moshi.Builder().build();
    return moshi.adapter(Map.class).toJson(responses);
  }
}
