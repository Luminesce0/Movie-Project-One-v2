package com.omegaspocktari.movieprojectone.sync;


import android.content.Context;
import android.os.AsyncTask;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by ${Michael} on 5/9/2017.
 *
 * Class to update TMDb data periodically
 */

public class MovieFirebaseJobService extends JobService {

    // Instantiate AsyncTask to take work off the Main Thread
    private AsyncTask<Void, Void, Void> mFetchMovieTask;

    // Beginning of job
    @Override
    public boolean onStartJob(final JobParameters job) {

        mFetchMovieTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                // Refresh TMDb movie data
                Context context = getApplicationContext();
                MovieSyncTask.syncMovies(context);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                jobFinished(job, false);
            }
        };

        mFetchMovieTask.execute();
        return true;
    }

    // Should the job be interrupted for whatever reason
    @Override
    public boolean onStopJob(JobParameters job) {
        if (mFetchMovieTask != null) {
            mFetchMovieTask.cancel(true);
        }
        return true;
    }
}
