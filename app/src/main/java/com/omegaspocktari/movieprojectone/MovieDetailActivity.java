package com.omegaspocktari.movieprojectone;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.omegaspocktari.movieprojectone.data.MovieContract.FavoriteMovies;
import com.omegaspocktari.movieprojectone.data.MovieContract.MovieColumns;
import com.omegaspocktari.movieprojectone.data.MovieContract.RegularMovies;
import com.omegaspocktari.movieprojectone.utilities.TMDbUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by ${Michael} on 11/11/2016.
 */

// TODO: https://classroom.udacity.com/nanodegrees/nd801/parts/cd689fc8-4765-4588-8e6b-eeb997b5647a/modules/f00afa8d-bde2-43d7-9c7e-4d7d52e28a27/lessons/950e6939-1786-4659-89de-5af2dec70716/concepts/0936369f-d687-479a-9de9-0a31ec5d61cd
public class MovieDetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MovieDetailActivity.class.getSimpleName();
    // Loader id
    private static final int ID_MOVIE_LOADER = 0;

    // String value of file path
    private static String mFavoriteMovieLocation;
    // Uri to access relevant data
    private int mPosition;
    private Cursor movie;

    // Views
    private TextView title;
    private TextView release;
    private TextView plot;
    private RatingBar rating;
    private Button mFavoriteMovieButton;
    private ImageView poster;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        // Get Views.
        poster = (ImageView) findViewById(R.id.iv_movie_poster);
        title = (TextView) findViewById(R.id.movie_title);
        release = (TextView) findViewById(R.id.movie_release);
        rating = (RatingBar) findViewById(R.id.movie_rating);
        plot = (TextView) findViewById(R.id.movie_plot);
        mFavoriteMovieButton = (Button) findViewById(R.id.b_favorite_movie);

        // Acquire the correct row to load
        Bundle bundle = getIntent().getExtras();
        Uri mUri = (Uri) bundle.get(getString(R.string.movie_key));
        String position = mUri.getLastPathSegment();
        mPosition = Integer.valueOf(position);

        Log.d(LOG_TAG, "URI: " + mUri);
        Log.d(LOG_TAG, "Position: " + mPosition);
        // Connect activity to loader
        getSupportLoaderManager().initLoader(ID_MOVIE_LOADER, null, this);
    }

    // Gather all movie relevant data and set the data to relevant views
    private void GenerateMovieDetailPage(Cursor movieCursor) {

        // Gathering all movie data
        String movieTitle = movieCursor.
                getString(movieCursor.getColumnIndex(MovieColumns.COLUMN_MOVIE_TITLE));
        String moviePlot = movieCursor.
                getString(movieCursor.getColumnIndex(MovieColumns.COLUMN_MOVIE_SYNOPSIS));
        Float movieUserRating = movieCursor.
                getFloat(movieCursor.getColumnIndex(MovieColumns.COLUMN_MOVIE_USER_RATING));
        String movieReleaseDate = movieCursor.
                getString(movieCursor.getColumnIndex(MovieColumns.COLUMN_MOVIE_RELEASE_DATE));
        String moviePoster = movieCursor.
                getString(movieCursor.getColumnIndex(MovieColumns.COLUMN_MOVIE_POSTER));

        Log.d(LOG_TAG, "Movie Path: " + moviePoster);
        // Setting movie data
        Picasso.with(getApplicationContext())
                .load(moviePoster)
                .into(poster);

        // Set View Data
        title.setText(movieTitle);
        release.setText(movieReleaseDate);
        rating.setRating((movieUserRating / 2));
        plot.setText(moviePlot);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    // Initialize movie button with helper methods
    private void initializeMovieFavoriteButton(Cursor movie) {
        int movieId = movie.
                getInt(movie.getColumnIndex(MovieColumns.COLUMN_MOVIE_ID));

        // Create selection and the necessary arguments to delete this specific movie
        String selection = MovieColumns.COLUMN_MOVIE_ID + "=?";
        String[] selectionArgs = new String[]{Integer.toString(movieId)};

        Cursor favoriteMovieDatabase = getContentResolver().query(FavoriteMovies.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null);

        // Set the appropriate button for a non-null/null response
        if (favoriteMovieDatabase.moveToFirst()) {
            Log.d(LOG_TAG, "setMovieFavoriteButtonStatus - True - Unavorite");
            setMovieFavoriteButtonStatus(true, movie);
        } else {
            Log.d(LOG_TAG, "setMovieFavoriteButtonStatus - False - Favorite");
            setMovieFavoriteButtonStatus(false, movie);
        }
    }

    // Sets up the status of the movie button depending on whether or not it has been favorited
    private void setMovieFavoriteButtonStatus(boolean favoriteStatus, final Cursor movie) {
        if (favoriteStatus == true) {
            mFavoriteMovieButton.setText(getResources().getString(R.string.btn_favorite_false));

            mFavoriteMovieButton.setOnClickListener(new View.OnClickListener() {
                /**
                 * Called when a view has been clicked.
                 *
                 * @param v The view that was clicked.
                 */
                @Override
                public void onClick(View v) {
                    removeFavoriteMovie(movie);
                }
            });
        } else {
            mFavoriteMovieButton.setText(getResources().getString(R.string.btn_favorite_true));

            mFavoriteMovieButton.setOnClickListener(new View.OnClickListener() {
                /**
                 * Called when a view has been clicked.
                 *
                 * @param v The view that was clicked.
                 */
                @Override
                public void onClick(View v) {
                    addNewFavoriteMovie(movie);
                }
            });
        }
    }

    // onClickListener to remove favorite movie
    private void removeFavoriteMovie(Cursor movie) {

        // Gather information of movie
        Log.d(LOG_TAG, "removeFavoriteMovie");
        int movieId = movie.
                getInt(movie.getColumnIndex(MovieColumns.COLUMN_MOVIE_ID));
        String movieTitle = movie.
                getString(movie.getColumnIndex(MovieColumns.COLUMN_MOVIE_TITLE));
        String moviePath = movie.
                getString(movie.getColumnIndex(MovieColumns.COLUMN_MOVIE_POSTER));

        // Create selection and the necessary arguments to delete this specific movie
        String selection = MovieColumns.COLUMN_MOVIE_ID + "=?";
        String[] selectionArgs = new String[]{Integer.toString(movieId)};

        // Get the favorite movie in question to be able to acquire the photoPath stored on the device
        Cursor favoriteMovie = this.getContentResolver().query(
                FavoriteMovies.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null);

        int deletedRow = getContentResolver().
                delete(FavoriteMovies.CONTENT_URI, selection, selectionArgs);

        // Delete movie and return result as a toast
        Log.d(LOG_TAG, "Pre if statement");
        if (deletedRow <= 0) {
            // Toast to verify that the deletion of the movie was unsuccessful
            Toast.makeText(this, "Error Deleting Movie: " + movieTitle,
                    Toast.LENGTH_LONG).show();
        } else {
            // Delete stored image
            getApplicationContext().deleteFile(moviePath);

            // Toast to verify deletion of favorite movie to user
            Toast.makeText(this, movieTitle + " deleted successfully.",
                    Toast.LENGTH_LONG).show();

            // Set status to be able to refavorite the movie should the user decide
            setMovieFavoriteButtonStatus(false, movie);
        }
        favoriteMovie.close();
    }

    // onClickListener to add favorite movie
    public void addNewFavoriteMovie(Cursor movie) {
        // Gather all relevant information
        Log.d(LOG_TAG, "addFavoriteMovie");
        ContentValues cv = new ContentValues();
        int movieId = movie.
                getInt(movie.getColumnIndex(MovieColumns.COLUMN_MOVIE_ID));
        String movieTitle = movie.
                getString(movie.getColumnIndex(MovieColumns.COLUMN_MOVIE_TITLE));
        String moviePlot = movie.
                getString(movie.getColumnIndex(MovieColumns.COLUMN_MOVIE_SYNOPSIS));
        Float movieUserRating = movie.
                getFloat(movie.getColumnIndex(MovieColumns.COLUMN_MOVIE_USER_RATING));
        String movieReleaseDate = movie.
                getString(movie.getColumnIndex(MovieColumns.COLUMN_MOVIE_RELEASE_DATE));
        String moviePoster = movie.
                getString(movie.getColumnIndex(MovieColumns.COLUMN_MOVIE_POSTER));

        // Create selection and the necessary arguments to add this specific movie
        String selection = MovieColumns.COLUMN_MOVIE_ID + "=?";
        String[] selectionArgs = new String[]{Integer.toString(movieId)};

        // Query to see if this exists
        Cursor movieDatabase = getContentResolver().query(FavoriteMovies.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null);

        if (!(movieDatabase.moveToFirst())) {
//            Picasso.with(this).load(moviePoster).into(
//                    saveImageToSystem(getApplicationContext(), moviePoster, + movieId));

            Picasso.with(this).load(moviePoster).
                    into(saveImageToSystem(this, Integer.toString(movieId)));



            Log.d(LOG_TAG, "inside if statement of first if, POSTER PATH: " + mFavoriteMovieLocation);
            if (mFavoriteMovieLocation != null) {
                cv.put(MovieColumns.COLUMN_MOVIE_ID, movieId);
                cv.put(MovieColumns.COLUMN_MOVIE_TITLE, movieTitle);
                cv.put(MovieColumns.COLUMN_MOVIE_POSTER, mFavoriteMovieLocation);
                cv.put(MovieColumns.COLUMN_MOVIE_SYNOPSIS, moviePlot);
                cv.put(MovieColumns.COLUMN_MOVIE_USER_RATING, movieUserRating);
                cv.put(MovieColumns.COLUMN_MOVIE_RELEASE_DATE, movieReleaseDate);

                // Add movie and return response as a toast
                Uri uri = getContentResolver().insert(FavoriteMovies.CONTENT_URI, cv);
                int id = Integer.valueOf(uri.getLastPathSegment());

                if (id <= 0) {
                    Toast.makeText(getApplicationContext(), "Error Adding Movie: "
                            + movieTitle, Toast.LENGTH_LONG).show();
                } else {
                    setMovieFavoriteButtonStatus(true, movie);
                    Toast.makeText(getApplicationContext(), movieTitle + " added successfully.",
                            Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(getApplicationContext(), "Error: Database row exists",
                        Toast.LENGTH_LONG).show();
            }
            movieDatabase.close();
        }
    }




    // Citing: http://www.codexpedia.com/android/android-download-and-save-image-through-picasso/
    // Helper method to addNewFavoriteMovie to download movie pictures for offline access
    private Target saveImageToSystem(Context context, final String imageName) {
        final File myImageFile = new File(context.getFilesDir(), imageName);
        mFavoriteMovieLocation = myImageFile.getAbsolutePath();
        Log.d(LOG_TAG, "Image Location: " + mFavoriteMovieLocation);


        return new Target() {

            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // Creates image file with directory and name
                        Log.d(LOG_TAG, "void Run inside bitmap");

                        FileOutputStream fos = null;
                        try {
                            Log.d(LOG_TAG, "try");
                            fos = new FileOutputStream(myImageFile);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                            fos.flush();
                        } catch (IOException e) {
                            Log.d(LOG_TAG, "Catch");
                            e.printStackTrace();
                        } finally {
                            Log.d(LOG_TAG, "Finally");
                            try {
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.d(LOG_TAG, "onBitmapFailed");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                if (placeHolderDrawable != null) {
                }
            }
        };
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new AsyncTaskLoader<Cursor>(this) {

            public void onStartLoading() {
                forceLoad();
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Cursor loadInBackground() {
                if (TMDbUtils.currentSortingMethod.equals(getString(R.string.pref_sorting_favorites))) {
                    Cursor cursor = getContext().getContentResolver().query(FavoriteMovies.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);
                    Log.d(LOG_TAG, "NUMBER OF STUFF " + cursor.getCount());
                    Log.d(LOG_TAG, "WAHT IS HAPPEN");
                    cursor.moveToPosition(mPosition);
                    return cursor;
                } else {
                    Cursor cursor = getContext().getContentResolver().query(RegularMovies.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);
                    Log.d(LOG_TAG, "NUMBER OF STUFF " + cursor.getCount());
                    Log.d(LOG_TAG, "WAHT IS HAPPEN");
                    cursor.moveToPosition(mPosition);
                    return cursor;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        // Check integrity of the information
        Log.d(LOG_TAG, "ON LOAD FINISHED");
        boolean cursorHasValidData = false;
        if (data != null) {
            cursorHasValidData = true;
        }

        if (!cursorHasValidData) {
            Log.d(LOG_TAG, "Cursor doesn't have valid data: " + mPosition);
            return;
        }
        // Generate MovieDetailPage
        Log.d(LOG_TAG, "Generating Detail Page");
        GenerateMovieDetailPage(data);
        // Setup button
        // initialize button
        initializeMovieFavoriteButton(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
