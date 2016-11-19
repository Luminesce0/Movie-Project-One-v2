package com.omegaspocktari.movieprojectone;

import java.io.Serializable;

/**
 * Created by ${Michael} on 11/4/2016.
 *
 * External Sources Utilized:
 * http://prasanta-paul.blogspot.com/2010/06/android-parcelable-example.html - Parcelable
 *
 */
public class Movie implements Serializable {

    // General information on movie.
    private String mMovieTitle;
    private String mMoviePlot;
    private String mMovieUserRating;
    private String mMovieRelease;
    private String mMoviePoster;

    public Movie(String mMovieTitle, String mMoviePlot, String mMovieUserRating, String mMovieRelease,
                 String mMoviePoster) {
        this.mMovieTitle = mMovieTitle;
        this.mMoviePlot = mMoviePlot;
        this.mMovieUserRating = mMovieUserRating;
        this.mMovieRelease = mMovieRelease;
        this.mMoviePoster = mMoviePoster;
    }

    public String getMoviePlot() {
        return mMoviePlot;
    }

    public Float getMovieUserRating() {
        return Float.valueOf(mMovieUserRating);
    }

    public String getMovieRelease() {
        return mMovieRelease;
    }

    public String getMoviePoster() {
        return mMoviePoster;
    }

    public String getMovieTitle() {
        return mMovieTitle;
    }
}
