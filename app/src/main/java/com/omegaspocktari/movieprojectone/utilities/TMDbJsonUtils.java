package com.omegaspocktari.movieprojectone.utilities;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.omegaspocktari.movieprojectone.BuildConfig;
import com.omegaspocktari.movieprojectone.Movie;
import com.omegaspocktari.movieprojectone.R;
import com.omegaspocktari.movieprojectone.data.MovieContract.FavoriteMovies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.omegaspocktari.movieprojectone.utilities.NetworkUtils.createUrl;
import static com.omegaspocktari.movieprojectone.utilities.NetworkUtils.makeHTTPRequest;

/**
 * Created by ${Michael} on 11/7/2016.
 */
public class TMDbJsonUtils {

    private static final String LOG_TAG = TMDbJsonUtils.class.getSimpleName();
    private static final String SCHEME = "http";
    private static final String AUTHORITY = "api.themoviedb.org";
    private static final String API_VERSION_PARAM = "3";
    private static final String API_QUERY = "api_key";

    /**
     * This class's static methods & variables are directly accessible from the class name.
     */
    private TMDbJsonUtils() {
    }

    public static List<Movie> getMovieDataFromJson(String sortingMethodPath, Context context) {
        // Create a URL Object
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(API_VERSION_PARAM)
                .appendEncodedPath(sortingMethodPath)
                .appendQueryParameter(API_QUERY, BuildConfig.THE_MOVIE_DB_API_KEY);
        URL url = createUrl(builder.build().toString());

        // Perform HTTP request to the URL & recieve a JSON response back.
        String jsonResponse = null;
        try {
            jsonResponse = makeHTTPRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP Request [Query Utils].", e);
        }

        // Extract relevant fields from the JSON Response & create a list of news items through parsing
        List<Movie> movies = extractMovieFavoritesFromJson(jsonResponse, context);

        // Return list of movies
        return movies;
    }



    // TODO : Potentially remove. Will be implementing cursor based info.
    // Potentially Remove
//    private static List<Movie> extractMovieFavoritesFromJson(List<String> jsonResponses, Context context, Cursor cursor) {
//
//        // If the JSON string is empty or null return.
//        if (jsonResponses == null) {
//            return null;
//        }
//
//        // List to store our movies
//        List<Movie> movies = new ArrayList<>();
//
//        // Json response
//        String jsonResponse;
//
//        for (int i = 0; i < jsonResponses.size(); i++) {
//
//            jsonResponse = jsonResponses.get(i);
//
//            try {
//                // JSON Objects that must be extracted.
//                final String TMDB_RESULTS = "results";
//                final String TMDB_TITLE = "original_title";
//                final String TMDB_RELEASE_DATE = "release_date";
//                final String TMDB_MOVIE_POSTER = "poster_path";
//                final String TMDB_VOTE_AVERAGE = "vote_average";
//                final String TMDB_PLOT_SYNOPSIS = "overview";
//                final String TMDB_MOVIE_ID = "id";
//
//                String movieTitle;
//                String movieReleaseDate;
//                String moviePoster;
//                String movieVoteAverage;
//                String moviePlotSynopsis;
//                int movieId;
//
//                JSONObject movieJsonObject = new JSONObject(jsonResponse);
//
//                // Gather relevant information from the result objects in movieResultsArray.
//                movieTitle = movieJsonObject.getString(TMDB_TITLE);
//                movieReleaseDate = movieJsonObject.getString(TMDB_RELEASE_DATE);
//                moviePoster = context.getString(R.string.poster_base_url)
//                        + context.getString(R.string.poster_size)
//                        + movieJsonObject.getString(TMDB_MOVIE_POSTER);
//                movieVoteAverage = movieJsonObject.getString(TMDB_VOTE_AVERAGE);
//                moviePlotSynopsis = movieJsonObject.getString(TMDB_PLOT_SYNOPSIS);
//                movieId = movieJsonObject.getInt(TMDB_MOVIE_ID);
//
//                // Create an [@link Movie} object with the parsed data.
//                Movie movieObject = new Movie(movieTitle, moviePlotSynopsis, movieVoteAverage,
//                        movieReleaseDate, moviePoster, movieId);
//
//                movies.add(movieObject);
//            } catch (JSONException e) {
//                Log.e(LOG_TAG, "Problem parsing the JSON results [Query Utils].", e);
//            }
//        }
//        return movies;
//    }
//    private static List<Movie> extractMovieFavoritesFromJson(String jsonResponse, Context context) {
//
//        // If the JSON string is empty or null return.
//        if (TextUtils.isEmpty(jsonResponse)) {
//            return null;
//        }
//
//        List<Movie> movies = new ArrayList<>();
//
//        try {
//            // JSON Objects that must be extracted.
//            final String TMDB_RESULTS = "results";
//            final String TMDB_TITLE = "original_title";
//            final String TMDB_RELEASE_DATE = "release_date";
//            final String TMDB_MOVIE_POSTER = "poster_path";
//            final String TMDB_VOTE_AVERAGE = "vote_average";
//            final String TMDB_PLOT_SYNOPSIS = "overview";
//            final String TMDB_MOVIE_ID = "id";
//
//            String movieTitle;
//            String movieReleaseDate;
//            String moviePoster;
//            String movieVoteAverage;
//            String moviePlotSynopsis;
//            int movieId;
//
//            JSONObject movieJson = new JSONObject(jsonResponse);
//            JSONArray movieResultsArray = movieJson.getJSONArray(TMDB_RESULTS);
//
//            for (int i = 0; i < movieResultsArray.length(); i++) {
//
//                // Gather relevant information from the result objects in movieResultsArray.
//                //
//                JSONObject movieJsonObject = movieResultsArray.getJSONObject(i);
//                movieTitle = movieJsonObject.getString(TMDB_TITLE);
//                movieReleaseDate = movieJsonObject.getString(TMDB_RELEASE_DATE);
//                moviePoster = context.getString(R.string.poster_base_url)
//                        + context.getString(R.string.poster_size)
//                        + movieJsonObject.getString(TMDB_MOVIE_POSTER);
//                movieVoteAverage = movieJsonObject.getString(TMDB_VOTE_AVERAGE);
//                moviePlotSynopsis = movieJsonObject.getString(TMDB_PLOT_SYNOPSIS);
//                movieId = movieJsonObject.getInt(TMDB_MOVIE_ID);
//
//                // Create an [@link Movie} object with the parsed data.
//                Movie movieObject = new Movie(movieTitle, moviePlotSynopsis, movieVoteAverage,
//                        movieReleaseDate, moviePoster, movieId);
//
//                movies.add(movieObject);
//            }
//        } catch (JSONException e) {
//            Log.e(LOG_TAG, "Problem parsing the JSON results [Query Utils].", e);
//        }
//
//        return movies;
//    }
////    //TODO: Make a no favorites text view and option/return
////    //TODO: Generate multiple URIs to loop through
//    private static List<Movie> getMovieFavoritesFromJson(String sortingMethodPath, Context context, Cursor cursor) {
//
//        // List to store favorite movie integers
//        List<Integer> movieIdList = new ArrayList<>();
//        List<String> movieUrlList = new ArrayList<>();
//
//        // TODO: Setup a content provider to do this more effectively later. Will continue now for learning
//        // http://stackoverflow.com/questions/903343/get-the-field-value-with-a-cursor
//
//        if (cursor.getCount() != 0) {
//            if (cursor.moveToFirst()) {
//                do {
//                    // Add integer from the cursors.getInt function that is supplied the column index
//                    movieIdList.add(cursor.getInt(cursor.getColumnIndex(FavoriteMovies.COLUMN_MOVIE_ID)));
//                } while (cursor.moveToNext());
//            }
//            cursor.close();
//        } else {
//            // Check for null as a return when getMovieFavorites is called, supply the correct text
//            // view to say that no favorite movies were found.
//            cursor.close();
//            return null;
//        }
//
//        int cursorSize = cursor.getCount();
//
//        for (int i = 0; i < cursorSize; i++) {
//
//            // Create a URL Object for each individual movie ID
//            Uri.Builder builder = new Uri.Builder();
//            builder.scheme(SCHEME)
//                    .authority(AUTHORITY)
//                    .appendPath(API_VERSION_PARAM)
//                    .appendEncodedPath(sortingMethodPath)
//                    .appendPath(Integer.toString(movieIdList.get(i)))
//                    .appendQueryParameter(API_QUERY, BuildConfig.THE_MOVIE_DB_API_KEY);
//            URL url = createUrl(builder.build().toString());
//
//            //TODO: remove me later
//            Log.d(LOG_TAG, "Check it out! Succesful?\n" + url + "\n");
//
//            // Perform HTTP request to the URL & receive a JSON response back.
//            try {
//                movieUrlList.add(makeHTTPRequest(url));
//            } catch (IOException e) {
//                Log.e(LOG_TAG, "Problem making the HTTP Request [Query Utils].", e);
//            }
//        }
//
//        // Extract relevant fields from the JSON Response & create a list of news items through parsing
//        List<Movie> movies = extractMovieFavoritesFromJson(movieUrlList, context, cursor);
//
//        // Return list of movies
//        return movies;
//    }

    // Decide which json utility to use
    public static List<Movie> getMovieDataFromJson(Context context, String sortingMethodPath, Cursor cursor) {
        String popularity = context.getResources().getString(R.string.pref_sorting_popularity);
        String topRated = context.getResources().getString(R.string.pref_sorting_rating);

        if (sortingMethodPath == popularity || sortingMethodPath == topRated) {
           return getMovieDataFromJson(sortingMethodPath, context);
        } else {
//            return getMovieFavoritesFromJson(sortingMethodPath, context, cursor);
        }
    }
}



