Sprint 2: API Proxy 
====================
### Authors: Shravya Sompalli (ssompal1), Calvin Eng (ceng4)
###### Estimated time contribution: 18 hrs

## Project Details
Created a simple API Proxy server that has two functions: loading and getting CSV files and determining temperature at a certain latitude and longitude. The project accesses the national weather API in order to determine the temperature from the coordinates. 

References and Collaborators-

sprint-2-nharbiso-plestz
- copied idea of using a CSVData class for the shared state

sprint-2-agupt137-aparmesw
- copied process to retrieve the temperature from latitude and longitude

https://stackoverflow.com/questions/153724/how-to-round-a-number-to-n-decimal-places-in-java
- used in order to truncate the latitude and longitude

Link to Github Repository-
(https://github.com/cs0320-f2022/sprint-2-ceng4-ssompal1)

## Design Choices
We implemented a server with three different handlers: LoadHandler, GetHandler, and WeatherHandler. Each handler follows a similar format with a handle() method and (a) response() method(s). We created a CSVData class, which stores the data from a CSV and has a boolean saying if a CSV file is loaded. This is the shared state that the LoadHandler and GetHandler have access to. In addition, it allows for error checking within GetHandler. One such error check is whether or not a CSV file is loaded. If a file is not loaded, the server will return a "bad_datasource" error, rather than a specialized message for consistency.

For defensive programming, we decided to create a copy of the CSV data within the getData() method of the CSVData class. In addition, a user cannot request to load a file that is not within the data folder of the software to protect from them opening and receiving the contents of any file.

For responses from each of the handlers, there is a success and failure response, or in the case of the LoadHandler, just a single response method. Each of the responses returns a Map<String, Object> with the results, the parameters, and the output if needed. 

## Errors
From our end, some of the Java imports do not work properly; however, mvn package and mvn site work, along with all tests passing. 

https://edstem.org/us/courses/28100/discussion/1955498

## Tests
We create several different sets of tests in order to test the various types of handlers. In each of the test suites, we set up the server by setting the spark port number. In a @BeforeEach designated method, we restart the server and initialize a new Handler class. In the TestGetHandler and TestLoadHandler suites, this set up process also includes initializing a new CSVData instance and setting it to null and the isLoaded boolean to false. In a @AfterEach designated method, we remove listening on the endpoints of the server and stop the server. These intermediate methods enable us to properly set up and take down the server for each test conducted. Each of the testing suites then accounts for the various test cases for queries passed in and potential responses. 

For the TestLoadHandler suite, the potential error responses tested include:
- no filepath being passed in
- an invalid filepath being passed in
- a filepath to an existing but improper file being passed in
- an improper request being made
- a file not in the data folder

For the TestGetHandler suite, the potential error responses being tested include:
- no file loaded
- file loaded but issue with parsing

For the WeatherHandler suite, the potential error responses being tested include:
- no latitude/longitude being passed in
- improper request being made
- a latitude and longitude passed not corresponding to a real location
- improper JSON

Then for each of the suites, several instances of valid queries corresponding to successful requests and responses are tested. 

## Running the Program
The API server can be started by running the Server class. Once the console has printed a message saying the server has started, the user can move to their browser and type "https:localhost/3232/" + a valid endpoint (getcsv,loaddsv,weather) + "?" + 
- "filepath=" + valid filepath
- "lat=" + valid latitude + "&lon=" + valid longitude

Testing can be run if the user has already installed maven by running "mvn package"
For loading and getting a CSV, the file must be in the data folder of the software. 
