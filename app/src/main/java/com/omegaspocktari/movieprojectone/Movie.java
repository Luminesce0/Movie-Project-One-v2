package com.omegaspocktari.movieprojectone;

import java.io.Serializable;

/**
 * Created by ${Michael} on 11/4/2016.
 * <p>
 * External Sources Utilized:
 * http://prasanta-paul.blogspot.com/2010/06/android-parcelable-example.html - Parcelable
 */
public class Movie implements Serializable {

    // TODO: Add a way to check for a favorite movie and update results?
    // General information on movie.
    private String mMovieTitle;
    private String mMoviePlot;
    private String mMovieUserRating;
    private String mMovieRelease;
    private String mMoviePoster;
    private int mMovieId;
    private boolean mIsMovieAFavorite;
    // TODO: Add reviews/trailer bits

    public Movie(String mMovieTitle, String mMoviePlot, String mMovieUserRating, String mMovieRelease,
                 String mMoviePoster, int mMovieId) {
        this.mMovieTitle = mMovieTitle;
        this.mMoviePlot = mMoviePlot;
        this.mMovieUserRating = mMovieUserRating;
        this.mMovieRelease = mMovieRelease;
        this.mMoviePoster = mMoviePoster;
        this.mMovieId = mMovieId;
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

    public int getMovieId() {
        return mMovieId;
    }
}
