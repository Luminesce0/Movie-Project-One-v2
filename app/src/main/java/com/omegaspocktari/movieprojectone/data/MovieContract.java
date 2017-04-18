package com.omegaspocktari.movieprojectone.data;

import android.provider.BaseColumns;

/**
 * Created by ${Michael} on 4/18/2017.
 */

public class MovieContract {

    public static final class FavoriteMovies implements BaseColumns {

        // Name for favorite movie table
        public static final String TABLE_NAME = "favoriteMovies";

        // ID for the movie
        public static final String COLUMN_MOVIE_ID = "id";

        // Title for the movie
        public static final String COLUMN_MOVIE_TITLE = "title";
    }
}
