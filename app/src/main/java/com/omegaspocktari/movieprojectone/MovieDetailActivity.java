package com.omegaspocktari.movieprojectone;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.omegaspocktari.movieprojectone.data.MovieDbHelper;
import com.squareup.picasso.Picasso;

import static com.omegaspocktari.movieprojectone.data.MovieContract.FavoriteMovies;

/**
 * Created by ${Michael} on 11/11/2016.
 */

public class MovieDetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = MovieDetailActivity.class.getSimpleName();

    private SQLiteDatabase mDb;
    private Button mFavoriteMovieButton;
    private int mMovieId;
    private String mMovieTitle;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Movie movieItem = (Movie) getIntent().getExtras().getSerializable(getString(R.string.movie_key));

        // Use dbHelper to generate a writable database
        MovieDbHelper dbHelper = new MovieDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        GenerateMovieDetailPage(movieItem);

        // Setup button
        mFavoriteMovieButton = (Button) findViewById(R.id.b_favorite_movie);
        mFavoriteMovieButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                ContentValues cv = new ContentValues();
                addNewMovie(mMovieId, mMovieTitle, cv);
            }
        });
    }

    private void GenerateMovieDetailPage(Movie movieItem) {

        mMovieId = movieItem.getMovieId();
        mMovieTitle = movieItem.getMovieTitle();

        // Do these return the original item values?
        String movieTitle = movieItem.getMovieTitle();
        String moviePlot = movieItem.getMoviePlot();
        Float movieUserRating = movieItem.getMovieUserRating();
        String movieRelease = movieItem.getMovieRelease();
        String moviePoster = movieItem.getMoviePoster();

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

    public void addNewMovie(int movieId, String movieTitle, ContentValues cv) {
        cv.put(FavoriteMovies.COLUMN_MOVIE_ID, movieId);
        cv.put(FavoriteMovies.COLUMN_MOVIE_TITLE, movieTitle);
        if (mDb.insert(FavoriteMovies.TABLE_NAME, null, cv) <= 0) {
            Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_LONG);
        }
    }
}
