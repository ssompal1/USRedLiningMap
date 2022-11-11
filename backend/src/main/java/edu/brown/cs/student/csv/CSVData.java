package edu.brown.cs.student.csv;

import java.util.ArrayList;
import java.util.List;

/**
 * The CSVData class provides a shared state for Handler classes to be able to access and mutate the
 * same data, in this case, the contents of a CSV file.
 */
public class CSVData {
  private List<List<String>> data;
  private Boolean isLoaded;
  private Integer row;
  private Integer col;

  /**
   * Instance variables for the data and boolean indicated loaded status are initialized
   *
   * @param data
   */
  public CSVData(List<List<String>> data) {
    this.data = data;
    this.isLoaded = null;
  }

  public CSVData() {
    this(null);
  }

  /**
   * getter method for data
   *
   * @return contents of CSV file or null
   */
  public List<List<String>> getData() {
    // Outputs null if null
    if (this.data == null) return null;

    // Creates a defensive copy
    List<List<String>> dataCopy = new ArrayList<>();
    for (List<String> row : this.data) {
      dataCopy.add(new ArrayList<>(row));
    }
    return dataCopy;
  }

  /**
   * setter method for data
   *
   * @param csvData- CSV contents
   */
  public void setData(List<List<String>> csvData) {
    this.data = csvData;
  }

  public void clearData() {
    this.data.clear();
  }

  /**
   * getter method for loaded status
   *
   * @return boolean depending on if CSV has been loaded by LoadHandler previously
   */
  public Boolean getIsLoaded() {
    return this.isLoaded;
  }

  /**
   * setter method for loaded status
   *
   * @param value
   */
  public void setIsLoaded(Boolean value) {
    this.isLoaded = value;
  }

  public void setRow(int row) {
    this.row = row;
  }

  public void setCol(int col) {
    this.col = col;
  }

  public Integer getRow() {
    return this.row;
  }

  public Integer getCol() {
    return this.col;
  }
}
