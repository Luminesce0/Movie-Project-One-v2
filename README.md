Movie-Project-One-v2
======
Movie Project One is an app built to help users discover popular and highly rated movies on the web. 

It displays a scrolling grid of movie trailers, launches a details screen whenever a particular movie is selected, 
allows users to save favorites, play trailers, and read user reviews. 

Movie-Project-One-v2's Coding Features & Design
------
This app utilizes core Android user interface components (Recycler View, View Holders, Constraint Layout), 
Material Design, preference integration, multi threading and content provider functionality. 

As a whole, this gives Movie Project One data storage capabilities, offline functionality, an uninhibitied UI thread, 
effecient UI generation and a Material Design inspired UI.

The app fetches movie information using themoviedb.org web API or from the device's storage.

To utilize Movie-Project-One-v2
------
1. An account can be created at https://www.themoviedb.org/
2. Once logged into your profile, your API key can be found at https://www.themoviedb.org/settings/api
* Utilize the API Key (v3 Auth) 
3. Once the API Key is acquired, find the file gradle.properties
4. Insert your API key within the quotations at line 19 MyMovieDBApiKey=""
5. The app is now ready to use once built and deployed! Enjoy
