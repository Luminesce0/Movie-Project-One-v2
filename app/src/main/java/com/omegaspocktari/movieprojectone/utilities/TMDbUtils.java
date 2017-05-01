package com.omegaspocktari.movieprojectone.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.omegaspocktari.movieprojectone.BuildConfig;
import com.omegaspocktari.movieprojectone.R;
import com.omegaspocktari.movieprojectone.data.MovieContract.FavoriteMovies;
import com.omegaspocktari.movieprojectone.data.MovieContract.MovieColumns;
import com.omegaspocktari.movieprojectone.data.MovieContract.RegularMovies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import static com.omegaspocktari.movieprojectone.utilities.NetworkUtils.createUrl;
import static com.omegaspocktari.movieprojectone.utilities.NetworkUtils.makeHTTPRequest;

/**
 * Created by ${Michael} on 11/7/2016.
 */
public class TMDbUtils {

    private static final String LOG_TAG = TMDbUtils.class.getSimpleName();
    private static final String SCHEME = "http";
    private static final String AUTHORITY = "api.themoviedb.org";
    private static final String API_VERSION_PARAM = "3";
    private static final String API_QUERY = "api_key";
    public static String currentSortingMethod;

    /**
     * This class's static methods & variables are directly accessible from the class name.
     */
    private TMDbUtils() {
    }

    public static Cursor getFavoriteMovieData(Context context) {
        // Used within onFinishedLoader
        currentSortingMethod = context.getString(R.string.pref_sorting_favorites);

        return context.getContentResolver().query(
                FavoriteMovies.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    public static Cursor getMovieDataFromJson(Context context, String sortingMethodPath) {
        // Used within onFinishedLoader
        currentSortingMethod = sortingMethodPath;

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

        extractJsonDataToCursor(jsonResponse, context);

        return context.getContentResolver().query(
                RegularMovies.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    private static void extractJsonDataToCursor(String jsonResponse, Context context) {
        int deleted = context.getContentResolver().delete(RegularMovies.CONTENT_URI, null, null);
        Log.d(LOG_TAG, "DELETED MOVIES: " + deleted);
        try {

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
            String movieUserRating;
            String moviePlotSynopsis;
            int movieId;

            JSONObject movieJson = new JSONObject(jsonResponse);
            JSONArray movieResultsArray = movieJson.getJSONArray(TMDB_RESULTS);

            for (int i = 0; i < movieResultsArray.length(); i++) {

                // JSON Objects that must be extracted.
                JSONObject movieJsonObject = movieResultsArray.getJSONObject(i);

                // Gather relevant information from the result objects in movieResultsArray.
                movieTitle = movieJsonObject.getString(TMDB_TITLE);
                movieReleaseDate = movieJsonObject.getString(TMDB_RELEASE_DATE);
                moviePoster = context.getString(R.string.poster_base_url)
                        + context.getString(R.string.poster_size)
                        + movieJsonObject.getString(TMDB_MOVIE_POSTER);
                movieUserRating = movieJsonObject.getString(TMDB_VOTE_AVERAGE);
                moviePlotSynopsis = movieJsonObject.getString(TMDB_PLOT_SYNOPSIS);
                movieId = movieJsonObject.getInt(TMDB_MOVIE_ID);

                // Setup selection and arguments for it to determine if this row exists or not
                String selection = MovieColumns.COLUMN_MOVIE_ID + "=?";
                String[] selectionArgs = new String[]{Integer.toString(movieId)};

                // Acquire a cursor that represents if it exists with null true or movie
                Log.d(LOG_TAG, "Here is the selection for movieDatabase cursor: " + movieId);
                Cursor movieDatabase = context.getContentResolver().query(RegularMovies.CONTENT_URI,
                        null,
                        selection,
                        selectionArgs,
                        null);

                // Set the appropriate button for a non-null/null response
                if (movieDatabase.getCount() > 0) {
                    Log.d(LOG_TAG, "Nothing was done! Movie: " + movieTitle);
                    Log.d(LOG_TAG, "Iteration(Not Done): " + i);
//                     do nothing
                    movieDatabase.close();
                } else {
                    Log.d(LOG_TAG, "Iterration(Done): " + i);
                    Log.d(LOG_TAG, "Movie added! " + movieTitle);
                    ContentValues cv = new ContentValues();
                    cv.put(MovieColumns.COLUMN_MOVIE_ID, movieId);
                    cv.put(MovieColumns.COLUMN_MOVIE_TITLE, movieTitle);
                    cv.put(MovieColumns.COLUMN_MOVIE_POSTER, moviePoster);
                    cv.put(MovieColumns.COLUMN_MOVIE_SYNOPSIS, moviePlotSynopsis);
                    cv.put(MovieColumns.COLUMN_MOVIE_USER_RATING, movieUserRating);
                    cv.put(MovieColumns.COLUMN_MOVIE_RELEASE_DATE, movieReleaseDate);

                    context.getContentResolver().insert(RegularMovies.CONTENT_URI, cv);
                    movieDatabase.close();
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the JSON results [Query Utils].", e);
        }
    }
}



