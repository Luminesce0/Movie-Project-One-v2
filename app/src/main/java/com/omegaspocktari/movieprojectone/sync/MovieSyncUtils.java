package com.omegaspocktari.movieprojectone.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.omegaspocktari.movieprojectone.data.MovieContract;

import java.util.concurrent.TimeUnit;

/**
 * Created by ${Michael} on 5/8/2017.
 *
 * Utility class to start up the IntentService
 */
public class MovieSyncUtils {

    // Constant values to sync Movie every 8-12 hours
    private static final int SYNC_INTERVAL_HOURS = 8;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 2;

    // Sync tag to identify sync job
    private static final String MOVIE_SYNC_TAG = "movie-sync";

    // Sync Checker
    private static Boolean sInitialized;

    // Method that schedules periodic movie syncing
    static void scheduleFirebaseJobDispatcherSync(@NonNull final Context context) {

        // Instantiate driver to handle scheduling, validation and execution of jobs
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        // Creation of the job that periodically syncs movies (Popular/Top Rated)
        Job syncMoviesJob = dispatcher.newJobBuilder()
                // Service utilized to sync data
                .setService(MovieFirebaseJobService.class)
                // Unique tag to identify this job
                .setTag(MOVIE_SYNC_TAG)
                // Network constraint set to ON_ANY_NETWORK because of low data demand
                .setConstraints(Constraint.ON_ANY_NETWORK)
                // Job will persist forever
                .setLifetime(Lifetime.FOREVER)
                // Job will recur to keep movies up to date in regards to time constraints
                .setRecurring(true)
                // Set execution window to be performed within the allotted time frame
                .setTrigger(Trigger.executionWindow(SYNC_INTERVAL_HOURS, SYNC_FLEXTIME_SECONDS))
                // This job will replace any jobs that already exist with the given tag
                .setReplaceCurrent(true)
                // Return the job once finished building
                .build();

        // Schedule the job with the dispatcher instantiated above
        dispatcher.schedule(syncMoviesJob);
    }

    // Immediately initialize movie data if it hasn't already been initialized
    public static void initialize(final Context context) {

        // If already initialized return
        if (sInitialized) return;

        // Set sInitialized to true to indicate initialization
        sInitialized = true;

        // Triggers Movie Project One to create the task to synchronize movie data every 12 hours
        scheduleFirebaseJobDispatcherSync(context);

        // AsyncTask to take the work off the MainThread into a BackgroundThread
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {

                // Uri for regular movies
                Uri movieQueryUri = MovieContract.RegularMovies.CONTENT_URI;

                // Query to acquire cursor of regular movies
                Cursor cursor = context.getContentResolver().query(
                        movieQueryUri,
                        null,
                        null,
                        null,
                        null);

                // If the cursor has returned no information acquire information from TMDb
                if (null == cursor || cursor.getCount() == 0) {
                    startImmediateSync(context);
                }

                // Close cursor and return
                cursor.close();
                return null;
            }
        }.execute();
    }

    // Method to acquire data from TMDb immediately
    public static void startImmediateSync(@NonNull final Context context) {

        // Create an intent to start our service to sync TMDb movie data off the main thread
        Intent intentToSyncImmediately = new Intent(context, MovieSyncIntentService.class);
        context.startService(intentToSyncImmediately);
    }

}
