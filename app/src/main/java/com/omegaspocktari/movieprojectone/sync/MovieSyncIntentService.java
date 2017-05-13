package com.omegaspocktari.movieprojectone.sync;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by ${Michael} on 5/8/2017.
 *
 * Class to handle task in the background of the application
 */

public class MovieSyncIntentService extends IntentService {
    public MovieSyncIntentService() {
        super ("MovieSyncIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        MovieSyncTask.syncMovies(this);
    }
}
