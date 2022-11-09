


public class RedLineHandler implements Route {
  private Float min_lat;
  private Float min_lon;
  private Float max_lat;
  private Float max_lon;

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
    try{}
    catch{}
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
    
    

}
