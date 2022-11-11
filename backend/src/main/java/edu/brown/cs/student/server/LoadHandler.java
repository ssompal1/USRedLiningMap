package edu.brown.cs.student.server;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.csv.CSVData;
import edu.brown.cs.student.csv.CSVParser;
import edu.brown.cs.student.csv.FactoryFailureException;
import edu.brown.cs.student.csv.ListStringFactory;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** Handler class for loading CSV "loadcsv" API endpoint */
public class LoadHandler implements Route {
  private String filepath;
  private final CSVData data;

  /**
   * @param data
   */
  public LoadHandler(CSVData data) {
    this.filepath = null;
    this.data = data;
  }

  /**
   * This main method of the class dictates the processing and response of a request made to the API
   * server. If the user does not pass a filepath in the request, a corresponding loadReponse is
   * called The method creates a file and attempts to parse the file. Exceptions with improper file
   * types and invalid filepaths are caught and corresponding loadResponses are called.
   *
   * @param request
   * @param response
   * @return
   */
  @Override
  public Object handle(Request request, Response response) {
    // Retrieves filepath from request
    this.filepath = request.queryParams("filepath");

    // Determines if the user provided a filepath
    if (this.filepath == null) {
      return loadResponse("error_bad_request");
    }

    // Determines if the desired file is in the data folder
    if (!filepath.startsWith("data/")) {
      return loadResponse("error_datasource");
    }

    // Attempt to read file and parse it into List of List of String
    try {
      File file = new File(this.filepath);
      FileReader fr = new FileReader(file);
      CSVParser<List<String>> parser = new CSVParser<>(fr, new ListStringFactory());
      List<List<String>> fileData = parser.create();
      this.data.setData(fileData);
      this.data.setIsLoaded(true);
    } catch (FactoryFailureException e) {
      return loadResponse("error_datasource");
    } catch (IOException e) {
      return loadResponse("error_datasource");
    }

    return loadResponse("success");
  }

  /**
   * This method returns the response of the API server after a request has been made. It creates a
   * HashMap and stores the type of result passed into the method as well as the filepath. The
   * HashMap is then serialized into JSON string format and returned
   *
   * @param responseType - String of either success or descriptive error message
   * @return JSON string of HashMap
   */
  public Object loadResponse(String responseType) {
    // Creates map with failure response
    Map<String, Object> responses = new HashMap<>();
    responses.put("result", responseType);
    responses.put("filepath", this.filepath);

    // Serializes responses into JSON format
    Moshi moshi = new Moshi.Builder().build();
    return moshi.adapter(Map.class).toJson(responses);
  }
}
