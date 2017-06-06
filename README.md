# Weather-app
Shows the weather in any city and forecast of 5 days

Structure this app

This app uses Google Location API that allows to define current location(city).
API Openweathermap provides data of current weather and forecast. App starts service that every 10 minutes calls to 
openweathermap API and gets weather data. This data we put in SQLite data base, and every 10 minutes we update this 
info. After getting new data in SQLite we update our UI.
Also app uses PlaceAutocomplete API that helps us to input city that we need.
