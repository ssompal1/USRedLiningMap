# MAP INTEGRATION 

## TEAM MEMBERS
Shravya Sompalli(ssompal1)
Victoria Vo (vvo4)

## TOTAL HOURS 

## REPO LINK
https://github.com/cs0320-f2022/integration-ssompal1-vvo4.git

## CONTRIBUTIONS/SOURCES

## DESIGN CHOICES 
This project is split into a frontend and backend directory. The pertinent files in the backend are the RedLineHandler and Server
classes. The server sets up a new endpoint "redline" so that the user may retrieve a collection of features displayed in JSON format
from the large geoJSON provided by <INSERT SOURCE>. In the RedLineHandler, 

The frontend contains mostly contains MapDemo and overlays. The MapDemo.tsx file is in charge of running the main Map and the overlays.ts file connects our bakcend directory with the map. 

## BUGS 

## TESTS
To run the frontend tests, navigate into the frontend directory and run npm test in the Terminal.

## RUNNING THE APP
1. To run the backend, go into the backend directory and run ./run in the Terminal.
2. To run the frontend, go into the frontend directory and run npm start in the Terminal. 
3. This opens a tab in a web browser with the app.