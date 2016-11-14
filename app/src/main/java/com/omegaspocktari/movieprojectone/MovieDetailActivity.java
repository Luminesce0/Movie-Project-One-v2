package com.omegaspocktari.movieprojectone;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by ${Michael} on 11/11/2016.
 */

public class MovieDetailActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Movie movieItem = getIntent().getExtras().getParcelable(getString(R.string.movie_key));

        GenerateMovieDetailPage(movieItem);
    }

    private void GenerateMovieDetailPage(Movie movieItem) {
        // Do these return the original item values?
        int movieTitle = movieItem.getmMovieTitleParcel();
        int moviePlot = movieItem.getmMoviePlotParcel();
        int movieUserRating = movieItem.getmMovieUserRatingParcel();
        int movieRelease = movieItem.getmMovieReleaseParcel();
        int moviePoster = movieItem.getmMoviePosterParcel();

        ImageView poster = (ImageView) findViewById(R.id.movie_poster);
        Picasso.with(getApplicationContext())
                .load(moviePoster)
                .into(poster);

        TextView title = (TextView) findViewById(R.id.movie_title);
        title.setText(movieTitle);

        TextView release = (TextView) findViewById(R.id.movie_release);
        release.setText(movieRelease);

        RatingBar rating = (RatingBar) findViewById(R.id.movie_rating);
        rating.setRating(movieUserRating);

        TextView plot = (TextView) findViewById(R.id.movie_plot);
        plot.setText(moviePlot);
    }


}
