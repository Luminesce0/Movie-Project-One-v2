Movie Project One
======
Movie Project One is an app built to help users discover popular and highly rated movies on the web. 

It displays a scrolling grid of movie trailers, launches a details screen whenever a particular movie is selected, 
allows users to save favorites, play trailers, and read user reviews. 

Movie-Project-One-v2's Coding Features & Design
------
This app utilizes core Android user interface components (Recycler View, View Holders, Constraint Layout), 
Material Design, preference integration, multi threading and content provider functionality. 

As a whole, this gives Movie Project One data storage capabilities, offline functionality, an uninhibited UI thread, 
efficient UI generation and a Material Design inspired UI.


Movie Project One fetches movie information using themoviedb.org web API.

![alt text](https://www.themoviedb.org/assets/static_cache/41bdcf10bbf6f84c0fc73f27b2180b95/images/v4/logos/91x81.png "TMDb")

To utilize Movie Project One
------

1. An account can be created at https://www.themoviedb.org/
2. Once logged into your profile, your API key can be found at https://www.themoviedb.org/settings/api

   *Utilize the API Key (**v3** Auth)*  
3. Once the API Key is acquired, find the file gradle.properties
4. Insert your API key within the quotations at line 19 MyMovieDBApiKey=""
5. The app is now ready to use once built and deployed! Enjoy
