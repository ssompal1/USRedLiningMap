# MAP INTEGRATION 


## OVERVIEW
This web application presents a map of the United States with a layer displaying neighborhoods designated as grades A (green) through D (red) by the Home Owner's Loan Corporation on their "residential security" in the 1930s. While HOLC claimed to evaluate residential security based on the likelihood of residents in an area to pay back loans or mortgages on time, it acted with clear racist guidelines. Per HOLC guidelines, neighborhoods with at least one Black resident were given a grade "D". This led to the systemic devaluation of properties of Black residents and people near them. Further, when the Federal Housing Administration insured mortgages issued by banking institutions and required them to offer loans at lower interest rates, they discouraged banks from offering loans in neighborhoods with a present or growing number of Black residents as well as non-single family home suburbs. This practice of systemic refusal to ensure mortgages in and neighborhoods with Black residents, leading to targeted disinvestment, has come to be known as redlining. 



## RUNNING THE APP 
To run the frontend, go into the frontend directory and run npm start in the Terminal. This should take the user directly to their browser where a map of Providence overlayed with redlining data should be rendered. 

To run the backend API server, go into the backend directory and run ./run in the Terminal to start the server. Then the user must type "http://localhost:3232/redline?min_lat=w&max_lat=x&min_lon=y4&max_lon=z" into their browser where w,x,y, and z are replaced by the desired query parameters of the user to filter the data they request by latitude and longitude.

## TESTS
To run backend tests, the user can go to the backend directory and run npm test file_name where filename is the name of test file file the user wants to run. The new testing files pertinent to this API server are the RedLineIntegrationTest.java file and the TestRedLineUnitTest.java file. 
OVERVIEW:
- Backend
    - Integration testing
        - Random/fuzz testing
    - Unit testing
        / Random/fuzz testing
        - Mocking


