package com.omegaspocktari.movieprojectone;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * MovieDetailInfo works as a storage place for values of individual movies
 *
 * Created by ${Michael} on 5/13/2017.
 */
@Parcel
public class MovieDetailInfo{
    // MDI data allocated?
    public boolean mdiDataStored = false;
    public boolean mdiListDataStored = false;
    public boolean mdiCreated = false;

    // Lists already instantiated?
    public boolean videoListsInstantiated = false;
    public boolean reviewListsInstantiated = false;

    // Stored information for
    public String moviePoster;
    public String movieTitle;
    public String movieReleaseDate;
    public String moviePlot;
    public float movieUserRating;
    public String movieId;

    public List<String> movieVideoKey;
    public List<String> movieVideoType;
    public List<String> movieVideoName;

    public List<String> movieReviewAuthor;
    public List<String> movieReviewContent;
    public List<String> movieReviewUrl;

    public MovieDetailInfo() {
    }

    public MovieDetailInfo(boolean mdiDataStored, boolean mdiListDataStored, boolean videoListsInstantiated,
                           boolean reviewListsInstantiated, String moviePoster, String movieTitle,
                           String movieReleaseDate, String moviePlot, float movieUserRating, String movieId,
                           List<String> movieVideoKey, List<String> movieVideoType, List<String> movieVideoName,
                           List<String> movieReviewAuthor, List<String> movieReviewContent, List<String> movieReviewUrl,
                           boolean mdiCreated) {
        this.mdiDataStored = mdiDataStored;
        this.mdiListDataStored = mdiListDataStored;
        this.videoListsInstantiated = videoListsInstantiated;
        this.reviewListsInstantiated = reviewListsInstantiated;
        this.moviePoster = moviePoster;
        this.movieTitle = movieTitle;
        this.movieReleaseDate = movieReleaseDate;
        this.moviePlot = moviePlot;
        this.movieUserRating = movieUserRating;
        this.movieId = movieId;
        this.movieVideoKey = movieVideoKey;
        this.movieVideoType = movieVideoType;
        this.movieVideoName = movieVideoName;
        this.movieReviewAuthor = movieReviewAuthor;
        this.movieReviewContent = movieReviewContent;
        this.movieReviewUrl = movieReviewUrl;
        this.mdiCreated = mdiCreated;
    }

    public void instantiateVideoLists () {
        if (!videoListsInstantiated) {
            movieVideoKey = new ArrayList<>();
            movieVideoType = new ArrayList<>();
            movieVideoName = new ArrayList<>();

            videoListsInstantiated = true;
        }
    }

    public void instantiateReviewLists () {
        if (!reviewListsInstantiated) {
            movieReviewAuthor = new ArrayList<>();
            movieReviewContent = new ArrayList<>();
            movieReviewUrl = new ArrayList<>();

            reviewListsInstantiated = true;
        }
    }

    public boolean isMdiDataStored() {
        return mdiDataStored;
    }

    public boolean isMdiListDataStored() {
        return mdiListDataStored;
    }

    public boolean isVideoListsInstantiated() {
        return videoListsInstantiated;
    }

    public boolean isReviewListsInstantiated() {
        return reviewListsInstantiated;
    }

    public String getMoviePoster() {
        return moviePoster;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public String getMovieReleaseDate() {
        return movieReleaseDate;
    }

    public String getMoviePlot() {
        return moviePlot;
    }

    public float getMovieUserRating() {
        return movieUserRating;
    }

    public String getMovieId() {
        return movieId;
    }

    public List<String> getMovieVideoKey() {
        return movieVideoKey;
    }

    public List<String> getMovieVideoType() {
        return movieVideoType;
    }

    public List<String> getMovieVideoName() {
        return movieVideoName;
    }

    public List<String> getMovieReviewAuthor() {
        return movieReviewAuthor;
    }

    public List<String> getMovieReviewContent() {
        return movieReviewContent;
    }

    public List<String> getMovieReviewUrl() {
        return movieReviewUrl;
    }

    public Boolean getMDICreated() {
        return mdiCreated;
    }
}
