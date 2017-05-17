package com.omegaspocktari.movieprojectone;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${Michael} on 5/13/2017.
 */

public class MovieDetailInfo {
    // Lists already instantiated?
    boolean videoListsInstantiated = false;
    boolean reviewListsInstantiated = false;

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

    public void instantiateVideoLists () {
        if (videoListsInstantiated != true) {
            movieVideoKey = new ArrayList<>();
            movieVideoType = new ArrayList<>();
            movieVideoName = new ArrayList<>();

            videoListsInstantiated = true;
        }
    }

    public void instantiateReviewLists () {
        if (reviewListsInstantiated != true) {
            movieReviewAuthor = new ArrayList<>();
            movieReviewContent = new ArrayList<>();
            movieReviewUrl = new ArrayList<>();

            reviewListsInstantiated = true;
        }
    }
}
