# MAP INTEGRATION 

## TEAM MEMBERS
Shravya Sompalli(ssompal1)
Victoria Vo (vvo4)

## TOTAL HOURS
Total time spent: 12 hours 

## REPO LINK
https://github.com/cs0320-f2022/integration-ssompal1-vvo4.git

## CONTRIBUTIONS/SOURCES
https://github.com/cs0320-f2022/integration-aremels-lshack.git
- testing and mocking
https://github.com/cs0320-f2022/integration-ahudda1-ceng4.git
- organization of RedLineHandler
- styles
- aria labels
https://github.com/cs0320-f2022/integration-eperelm2-tlynn1.git
- MapBox onClick and onMove
## CODE OVERVIEW & DESIGN CHOICES 
This project is split into a frontend and backend directory. The pertinent files for this project in the backend are the RedLineHandler and Server classes. The Server class sets up a new endpoint "redline" so that the user may retrieve a collection of features displayed in JSON format from the large geoJSON provided by the University of Richmond. In the RedLineHandler, query parameters for minimum and maximum longitudes are taken in as boundary points for filtering the geoJSON data to be returned to the user. This class verifies the existence and validity of the four parameters, and returns the filtered geoJSON data or a bad request error with the parameters provided. The handle method in the class handles success and failure responses while the getFilteredData method acts as a helper, intaking the query parameters as well as the string contents of a JSON and returning a FeatureCollection.

The frontend primarily contains MapDemo.tsx and overlays.ts. The MapDemo.tsx file contains the code for creating the interactive map and the overlays.ts file assigns colors for the different HOLC designated areas "A","B","C","D" in order to visualize the phenomena of redlining. 

RECORDS: We decided to create 3 records—FeatureCollection,Feature,Geometry—because the geoJSON already used these categories in order to make the data intelligible. By creating these records, we were able to serialize the geoJSON into objects representing these fields.

BAD REQUESTS: The following conditions result in an error bad request message:
- missing parameters
- non-existent latitudes and longitudes
- larger minimums than maximums
- parameters that cannot be converted to floats

DEFENSIVE PROGRAMMING: Our defensive programming is primarily visible in the RedLineHandler. In here, we delegate the filtering of the JSON data to a helper function and leave higher level functions in the main handler. By handling all the errors in the handle function, we localize errors to a singular place and prevent corrupted file contents from ever entering the filter method. We also handle a number of improper user input cases such as inputting strings as query parameters by utilizing try catch statements. 

ARIA-LABELS: We utilize aria labels in our Map.tsx file largely in order to mark a main description of the map. 

## BUGS 

## TESTS
To run backend tests, the user can go to the backend directory and run npm test file_name where filename is the name of test file file the user wants to run. The new testing files pertinent to this API server are the RedLineIntegrationTest.java file and the TestRedLineUnitTest.java file. 
OVERVIEW:
- Backend
    - Integration testing
        - Random/fuzz testing
    - Unit testing
        / Random/fuzz testing
        - Mocking

Integration Testing
- Testing server request with no parameters
- Testing server request with missing parameters
- Testing server request with minimum latitude/longitude greater than maximum
- Testing server request with parameters that cannot be converted to float
- Testing valid server request
- Fuzz test with random query parameters

Unit Testing
- Testing getFilteredData function with parameters creating no bounded area
- Testing getFilteredData function with valid parameters corresponding to Phoeniz, AZ
- Testing getFilteredData function with parameters corresponding to area with no features e.g.in the ocean 
- Fuzz test of getFilteredData function
- Testing getFilteredData function with mock data, mocking valid query parameters corresponding to San Francisco


## RUNNING THE APP 
USER STORY 1 & 2
To run the frontend, go into the frontend directory and run npm start in the Terminal. This should take the user directly to their browser where a map of Providence overlayed with redlining data should be rendered. 

USER STORY 3
To run the backend API server, go into the backend directory and run ./run in the Terminal to start the server. Then the user must type "http://localhost:3232/redline?min_lat=w&max_lat=x&min_lon=y4&max_lon=z" into their browser where w,x,y, and z are replaced by the desired query parameters of the user to filter the data they request by latitude and longitude.


