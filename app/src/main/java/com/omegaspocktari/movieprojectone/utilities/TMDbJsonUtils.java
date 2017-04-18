package com.omegaspocktari.movieprojectone.utilities;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.omegaspocktari.movieprojectone.BuildConfig;
import com.omegaspocktari.movieprojectone.Movie;
import com.omegaspocktari.movieprojectone.R;

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
        List<Movie> movies = extractMoviesFromJson(jsonResponse, context);

        // Return list of movies
        return movies;
    }

    private static List<Movie> extractMoviesFromJson(String jsonResponse, Context context) {

        // If the JSON string is empty or null return.
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        List<Movie> movies = new ArrayList<>();

        try {
            // JSON Objects that must be extracted.
            final String TMDB_RESULTS = "results";
            final String TMDB_TITLE = "original_title";
            final String TMDB_RELEASE_DATE = "release_date";
            final String TMDB_MOVIE_POSTER = "poster_path";
            final String TMDB_VOTE_AVERAGE = "vote_average";
            final String TMDB_PLOT_SYNOPSIS = "overview";
            final String TMDB_MOVIE_ID = "id";

            String movieTitle;
            String movieReleaseDate;
            String moviePoster;
            String movieVoteAverage;
            String moviePlotSynopsis;
            int movieId;

            JSONObject movieJson = new JSONObject(jsonResponse);
            JSONArray movieResultsArray = movieJson.getJSONArray(TMDB_RESULTS);

            for (int i = 0; i < movieResultsArray.length(); i++) {

                // Gather relevant information from the result objects in movieResultsArray.
                //
                JSONObject movieJsonObject = movieResultsArray.getJSONObject(i);
                movieTitle = movieJsonObject.getString(TMDB_TITLE);
                movieReleaseDate = movieJsonObject.getString(TMDB_RELEASE_DATE);
                moviePoster = context.getString(R.string.poster_base_url)
                        + context.getString(R.string.poster_size)
                        + movieJsonObject.getString(TMDB_MOVIE_POSTER);
                movieVoteAverage = movieJsonObject.getString(TMDB_VOTE_AVERAGE);
                moviePlotSynopsis = movieJsonObject.getString(TMDB_PLOT_SYNOPSIS);
                movieId = movieJsonObject.getInt(TMDB_MOVIE_ID);

                // Create an [@link Movie} object with the parsed data.
                Movie movieObject = new Movie(movieTitle, moviePlotSynopsis, movieVoteAverage,
                        movieReleaseDate, moviePoster, movieId);

                movies.add(movieObject);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the JSON results [Query Utils].", e);
        }

        return movies;
    }
}

