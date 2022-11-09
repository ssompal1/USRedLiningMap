package edu.brown.cs.student.server;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.csv.CSVData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** Handler class for retrieving contents of CSV "getcsv" API endpoint */
public class GetHandler implements Route {
  private final CSVData data;

  /**
   * @param data
   */
  public GetHandler(CSVData data) {
    this.data = data;
  }

  /**
   * This main method of the class dictates the processing and response of a request made to the API
   * server. The method accesses the instance of CSVData (shared state) and returns the results of
   * corresponding failureResponses method calls depending on if the CSVData has been loaded or if
   * the fileData is null. Otherwise, a successfulResponse method is called. If
   *
   * @param request
   * @param response
   * @return JSON string of HashMap based on response method call
   */
  @Override
  public Object handle(Request request, Response response) {
    // Checks if there is a CSV already loaded
    if (this.data.getIsLoaded() != true) {
      return getFailureResponse("error_bad_request");
    }

    // Checks if the CSV was properly parsed
    List<List<String>> fileData = this.data.getData();
    if (fileData == null) {
      return getFailureResponse("error_datasource");
    }

    // Returns the contents of the CSV
    return getSuccessResponse();
  }

  /**
   * This method executes when the user request is successfully completed. A map is created with a
   * String stored denoting the successful result and the contents of the CSV held in the CSVData
   * instance is stored in the map. The map is serialized and returned in JSON string format.
   *
   * @return serialized JSON string of Map
   */
  public Object getSuccessResponse() {
    // Creates map with failure response
    Map<String, Object> responses = new HashMap<>();
    responses.put("result", "success");
    responses.put("data", this.data.getData());

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
  public Object getFailureResponse(String responseType) {
    // Creates map with failure response
    Map<String, Object> responses = new HashMap<>();
    responses.put("result", responseType);

    // Serializes responses into JSON format
    Moshi moshi = new Moshi.Builder().build();
    return moshi.adapter(Map.class).toJson(responses);
  }
}
