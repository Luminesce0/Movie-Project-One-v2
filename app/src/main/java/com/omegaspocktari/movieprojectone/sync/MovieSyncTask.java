package com.omegaspocktari.movieprojectone.sync;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.DateUtils;
import android.util.Log;

import com.omegaspocktari.movieprojectone.R;
import com.omegaspocktari.movieprojectone.data.MoviePreferences;
import com.omegaspocktari.movieprojectone.utilities.NotificationUtils;
import com.omegaspocktari.movieprojectone.utilities.TMDbUtils;

/**
 * Created by ${Michael} on 5/8/2017.
 *
 * Class that handles the acquisition and processing of TMDb movie data into
 * the content provider/database
 */
public class MovieSyncTask {
    private static final String LOG_TAG = MovieSyncTask.class.getSimpleName();

    // Acquire data from TMDb to store within our ContentProvider/Database
    synchronized public static void syncMovies(Context context) {

        Log.d(LOG_TAG, "updateMovies()");

        // Acquire a connectivity manager to see if the network is connected.
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get the current active network's info.
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        // Acquire the preference for the listed movies
        String sortingPreference = MoviePreferences.getPreferredMovieSorting(context);

        // Should a network connection be present, attempt to fetch data
        // however, should the sorting preference be favorites, bypass network
        // for offline capabilities.
        if ((sortingPreference.equals(context.getString(R.string.pref_sorting_favorites)))
                || (networkInfo != null && networkInfo.isConnected())) {

            // Run the methods from TMDbUtils to acquire an array list of movie objects
            // derived from user preference inputs/defaults and JSON queries.
            if (sortingPreference.equals(context.getString(R.string.pref_sorting_popularity)) ||
                    sortingPreference.equals((context.getString(R.string.pref_sorting_rating)))) {
                Log.d(LOG_TAG, "" + sortingPreference);
                Log.d(LOG_TAG, "Returning POPULARITY or RATING results");
                TMDbUtils.extractMovieJsonDataToDatabase(context, sortingPreference);

            } else {
                // Derive data set by favoriting movies
                Log.d(LOG_TAG, "" + sortingPreference);
                Log.d(LOG_TAG, "Returning FAVORITES");
                TMDbUtils.getFavoriteMovieData(context);
            }
        }

        // Check to see if the user should be updated that movies have been updated

        boolean notificationsEnabled = MoviePreferences.areNotificationsEnabled(context);

        // If the notification was last sent 12 hours ago we should notify the user that movies were updated
        long timeSinceLastNotification = MoviePreferences.getElapsedTimeSinceLastNotification(context);

        boolean halfDayPassedSinceLastNotification = false;

        if (timeSinceLastNotification >= DateUtils.DAY_IN_MILLIS/2) {
            halfDayPassedSinceLastNotification = true;
        }

        if (notificationsEnabled && halfDayPassedSinceLastNotification) {
            NotificationUtils.notifyUserOfMovieUpdate(context);
        }
    }
}
