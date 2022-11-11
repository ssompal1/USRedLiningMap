package edu.brown.cs.student.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.squareup.moshi.Moshi;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.Test;
import spark.Spark;

public class TestWeatherUnits {
  /**
   * Tests truncating within the WeatherHandler class, focusing on an already truncated number, a
   * number that cannot be truncated, a negative number, and a number that should be truncated.
   */
  @Test
  public void testTruncating() {
    String alreadyTruncated = "45.9283";
    String toBeTruncated = "-28.3423421";
    String cannotTruncate = "3.23";
    String zero = "0";

    WeatherHandler weatherHandler = new WeatherHandler();

    assertEquals(weatherHandler.truncate(alreadyTruncated), alreadyTruncated);
    assertEquals(weatherHandler.truncate(toBeTruncated), "-28.3423");
    assertEquals(weatherHandler.truncate(cannotTruncate), "3.23");
    assertEquals(weatherHandler.truncate(zero), "0");
  }

  /** Tests the getTemperature() function within the WeatherHandler Class */
  @Test
  public void testGetTemperature() throws Exception {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING);
    Spark.get("weather", new WeatherHandler());
    Spark.init();
    Spark.awaitInitialization();

    String lat = "41.8240";
    String lon = "-71.4128";
    String website =
        "http://localhost:" + Spark.port() + "/" + "weather?lat=" + lat + "&lon=" + lon;

    URL requestURL = new URL(website);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    clientConnection.connect();
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Map<String, Object> responses =
        moshi.adapter(Map.class).fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Double temperature = (Double) responses.get("temperature");

    assertTrue(temperature > -150 && temperature < 150);

    clientConnection.disconnect();
    Spark.unmap("/weather");
    Spark.stop();
    Spark.awaitStop();
  }
}
