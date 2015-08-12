# UdacityProjects
To run the application, please add an external API key to com.learning.jonathan.popularmovies.MainActivityFragment.java 
in the public static final string API_KEY.

My previous attempt at the project did not meet specifications as I failed to store the State of the application after an onStop, for example. To get around this, I have changed this so that all movie data for both 'popular' and 'highest rated', are now loaded into two 'MovieStore' objects, one for each list.

These objects are persisted and the data will only be reloaded after the app and data are destroyed.

Hope that works. This has been quite a challenge. Thanks
