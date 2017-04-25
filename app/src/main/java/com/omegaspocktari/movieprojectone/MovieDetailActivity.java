package com.omegaspocktari.movieprojectone;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.omegaspocktari.movieprojectone.data.MovieContract.FavoriteMovies;

/**
 * Created by ${Michael} on 11/11/2016.
 */

// TODO: https://classroom.udacity.com/nanodegrees/nd801/parts/cd689fc8-4765-4588-8e6b-eeb997b5647a/modules/f00afa8d-bde2-43d7-9c7e-4d7d52e28a27/lessons/950e6939-1786-4659-89de-5af2dec70716/concepts/0936369f-d687-479a-9de9-0a31ec5d61cd
public class MovieDetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = MovieDetailActivity.class.getSimpleName();

    private Movie movieItem;
    private Button mFavoriteMovieButton;
    private String mFavoriteMovieLocation;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        movieItem = (Movie) getIntent().getExtras().getSerializable(getString(R.string.movie_key));

        // Generate MovieDetailPage
        GenerateMovieDetailPage(movieItem);

        // Setup button
        mFavoriteMovieButton = (Button) findViewById(R.id.b_favorite_movie);
    }

    // Gather all movie relevant data and set the data to relevant views
    private void GenerateMovieDetailPage(Movie movieItem) {
        // Gathering all movie data
        String movieTitle = movieItem.getMovieTitle();
        String moviePlot = movieItem.getMoviePlot();
        Float movieUserRating = movieItem.getMovieUserRating();
        String movieRelease = movieItem.getMovieRelease();
        String moviePoster = movieItem.getMoviePoster();

        // Setting movie data
        ImageView poster = (ImageView) findViewById(R.id.iv_movie_poster);
        Picasso.with(getApplicationContext())
                .load(moviePoster)
                .into(poster);

        TextView title = (TextView) findViewById(R.id.movie_title);
        title.setText(movieTitle);

        TextView release = (TextView) findViewById(R.id.movie_release);
        release.setText(movieRelease);

        RatingBar rating = (RatingBar) findViewById(R.id.movie_rating);
        rating.setRating((movieUserRating / 2));

        TextView plot = (TextView) findViewById(R.id.movie_plot);
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
    private void initializeMovieFavoriteButton() {
        int movieId = movieItem.getMovieId();

        String selection = FavoriteMovies.COLUMN_MOVIE_ID + "=?";
        String[] selectionArgs = new String[]{Integer.toString(movieId)};

        Cursor movieDatabase = getContentResolver().query(FavoriteMovies.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null);

        if (movieDatabase != null) {
            setMovieFavoriteButtonStatus(true);
        } else {
            setMovieFavoriteButtonStatus(false);
        }
    }

    // Sets up the status of the movie button depending on whether or not it has been favorited
    private void setMovieFavoriteButtonStatus(boolean favoriteStatus) {
        if (favoriteStatus == true) {
            mFavoriteMovieButton.setText(getResources().getString(R.string.btn_favorite_true));

            mFavoriteMovieButton.setOnClickListener(new View.OnClickListener() {
                /**
                 * Called when a view has been clicked.
                 *
                 * @param v The view that was clicked.
                 */
                @Override
                public void onClick(View v) {
                    removeFavoriteMovie();
                }
            });
        } else {
            mFavoriteMovieButton.setText(getResources().getString(R.string.btn_favorite_false));

            mFavoriteMovieButton.setOnClickListener(new View.OnClickListener() {
                /**
                 * Called when a view has been clicked.
                 *
                 * @param v The view that was clicked.
                 */
                @Override
                public void onClick(View v) {
                    addNewFavoriteMovie();
                }
            });
        }
    }

    // onClickListener to remove favorite movie
    private void removeFavoriteMovie() {
        // Gather information of movie
        int movieId = movieItem.getMovieId();

        // Create selection and the necessary arguments to delete this specific movie
        String selection = FavoriteMovies.COLUMN_MOVIE_ID + "=?";
        String[] selectionArgs = new String[]{Integer.toString(movieId)};

        // Delete movie and return result as a toast
        if (getContentResolver().delete(FavoriteMovies.CONTENT_URI, selection, selectionArgs) <= 0) {
            Toast.makeText(getApplicationContext(), "Error Deleting Movie: "
                    + movieItem.getMovieTitle(), Toast.LENGTH_LONG);
        } else {
            Toast.makeText(getApplicationContext(), movieItem.getMovieTitle()
                    + " deleted successfully.", Toast.LENGTH_LONG);
        }
    }

    // onClickListener to add favorite movie
    public void addNewFavoriteMovie() {

        ContentValues cv = new ContentValues();

        // Gather information of movie
        int movieId = movieItem.getMovieId();
        String movieTitle = movieItem.getMovieTitle();
        String moviePoster = movieItem.getMoviePoster();
        String moviePlot = movieItem.getMoviePlot();
        Float movieUserRating = movieItem.getMovieUserRating();
        String movieReleaseDate = movieItem.getMovieRelease();

        String selection = FavoriteMovies.COLUMN_MOVIE_ID + "=?";
        String[] selectionArgs = new String[]{Integer.toString(movieId)};

        Cursor movieDatabase = getContentResolver().query(FavoriteMovies.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null);

        if (movieDatabase != null) {
            Picasso.with(this).load(moviePoster).into(
                    saveImageToSystem(getApplicationContext(), FavoriteMovies.TABLE_NAME,
                            movieTitle + " " + movieId));

            cv.put(FavoriteMovies.COLUMN_MOVIE_ID, movieId);
            cv.put(FavoriteMovies.COLUMN_MOVIE_TITLE, movieTitle);
            cv.put(FavoriteMovies.COLUMN_MOVIE_POSTER, mFavoriteMovieLocation);
            cv.put(FavoriteMovies.COLUMN_MOVIE_SYNOPSIS, moviePlot);
            cv.put(FavoriteMovies.COLUMN_MOVIE_USER_RATING, movieUserRating);
            cv.put(FavoriteMovies.COLUMN_MOVIE_RELEASE_DATE, movieReleaseDate);

            // Add movie and return response as a toast
            Uri uri = getContentResolver().insert(FavoriteMovies.CONTENT_URI, cv);
            int id = Integer.valueOf(uri.getLastPathSegment());

            if ( id <= 0) {
                Toast.makeText(getApplicationContext(), "Error Adding Movie: "
                        + movieTitle, Toast.LENGTH_LONG);
            } else {
                Toast.makeText(getApplicationContext(), movieTitle + " added successfully.",
                        Toast.LENGTH_LONG);
            }
        }
        movieDatabase.close();
    }

    // Citing: http://www.codexpedia.com/android/android-download-and-save-image-through-picasso/
    // Helper method to addNewFavoriteMovie to download movie pictures for offline access
    private Target saveImageToSystem(Context context, final String imageDirectory, final String imageName) {
        // TODO: Is this really necessary?
        ContextWrapper cw = new ContextWrapper(context);
        //MODE_PRIVATE for file creation
        final File directory = cw.getDir(imageDirectory, context.MODE_PRIVATE);
        return new Target() {

            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // Creates image file with directory and name
                        final File myImageFile = new File(directory, imageName);
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(myImageFile);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        mFavoriteMovieLocation = myImageFile.getAbsolutePath();
                        Log.i("Image", "Image Saved To: " + myImageFile.getAbsolutePath());
                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                if (placeHolderDrawable != null) {
                }
            }
        };
    }
}
