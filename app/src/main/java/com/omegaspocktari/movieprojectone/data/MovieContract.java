package com.omegaspocktari.movieprojectone.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ${Michael} on 4/18/2017.
 */

public class MovieContract {

    // Authority for our content provider
    public static final String AUTHORITY = "com.omegaspocktari.movieprojectone";

    // Uri for the base content
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Paths for accessing data within favorite movie directory
    public static final String PATH_FAVORITE_MOVIES = "favoriteMovies";

    // Path for accessing data within movie directory
    public static final String PATH_MOVIES = "movies";

    public static final class FavoriteMovies implements BaseColumns {

        // FavoriteMovies content uri
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE_MOVIES).build();

        // Name for favorite movie table
        public static final String TABLE_NAME = "favoriteMovies";

        // ID for the movie
        public static final String COLUMN_MOVIE_ID = "id";

        // Title for the movie
        public static final String COLUMN_MOVIE_TITLE = "title";

        // TODO: Look into this
        // Movie poster
        public static final String COLUMN_MOVIE_POSTER = "posterPath";

        // Synopsis
        public static final String COLUMN_MOVIE_SYNOPSIS = "synopsis";

        // User Rating
        public static final String COLUMN_MOVIE_USER_RATING = "userRating";

        // Release Date
        public static final String COLUMN_MOVIE_RELEASE_DATE = "releaseDate";
    }
}
