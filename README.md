# LocationTracker
This project contains code for an Android application which I called it LocationTracker.
The application uses Google Location API and Google map for tracking user and send it to its backend through a RESTful interface.
The application has a local database and a very simple scheduler. 
## Functionality of the application
1. User can create , delete and update a profile.
2. User can records it position.
2. Application can save the obtained location in the local database.
3. Appllication sends the obtained location to a custom backend.
4. Data transfer is based on simple scheduleing
5. User can fetch all or a part of its recorded location from backend and show it the google map in the application.
6. User can delete all or part of recoreded location based on time interval.
<h2> APIs</h2>
<ul>
<li>Google Maps</li>
<li>Google Location API</li>
<li>Realm ( removed in favour of SQLite)</li>
<li>Retrofite (Rest client)</li>
</ul>
