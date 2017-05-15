package com.omegaspocktari.movieprojectone.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.omegaspocktari.movieprojectone.BuildConfig;
import com.omegaspocktari.movieprojectone.MovieDetailInfo;
import com.omegaspocktari.movieprojectone.R;
import com.omegaspocktari.movieprojectone.data.MovieContract.FavoriteMovies;
import com.omegaspocktari.movieprojectone.data.MovieContract.MovieColumns;
import com.omegaspocktari.movieprojectone.data.MovieContract.RegularMovies;
import com.omegaspocktari.movieprojectone.data.MoviePreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

    public static Cursor getMovieData(Context context) {
        String sortingPreference = MoviePreferences.getPreferredMovieSorting(context);

        if (sortingPreference.equals(context.getString(R.string.pref_sorting_favorites))) {
            currentSortingMethod = MoviePreferences.getPreferredMovieSorting(context);
            return getFavoriteMovieData(context);
        } else {
            currentSortingMethod = MoviePreferences.getPreferredMovieSorting(context);
            return getRegularMovieData(context);
        }


    }

    public static Cursor getFavoriteMovieData(Context context) {
        // Used within onFinishedLoader

        return context.getContentResolver().query(
                FavoriteMovies.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    public static Cursor getRegularMovieData(Context context) {
        // Used within onFinishedLoader

        return context.getContentResolver().query(
                FavoriteMovies.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    public static void extractMovieJsonDataToDatabase(Context context, String sortingMethodPath) {
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

        putMovieDataIntoDatabase(jsonResponse, context);
    }

    private static void putMovieDataIntoDatabase(String jsonResponse, Context context) {
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

    public static MovieDetailInfo generateMovieDetailInfo(Cursor movieCursor) {
        MovieDetailInfo mdi = new MovieDetailInfo();

        // Gathering all movie data
        mdi.movieTitle = movieCursor.
                getString(movieCursor.getColumnIndex(MovieColumns.COLUMN_MOVIE_TITLE));
        mdi.moviePlot = movieCursor.
                getString(movieCursor.getColumnIndex(MovieColumns.COLUMN_MOVIE_SYNOPSIS));
        mdi.movieUserRating = movieCursor.
                getFloat(movieCursor.getColumnIndex(MovieColumns.COLUMN_MOVIE_USER_RATING));
        mdi.movieReleaseDate = movieCursor.
                getString(movieCursor.getColumnIndex(MovieColumns.COLUMN_MOVIE_RELEASE_DATE));
        mdi.moviePoster = movieCursor.
                getString(movieCursor.getColumnIndex(MovieColumns.COLUMN_MOVIE_POSTER));

        return mdi;
    }

    public static String saveToInternalStorage(Bitmap bitmapImage, String imageFile, Context context) {
        ContextWrapper cw = new ContextWrapper(context);

        File filePath = new File(cw.getFilesDir(), imageFile);
        Log.d(LOG_TAG, "File Directory inside saveToInternalStorage: " + filePath);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return filePath.getAbsolutePath();
    }

    public static void loadImageFromSystem(String imagePath, ImageView iv) {
        FileInputStream fis = null;
        try {
//            File moviePoster = new File(imagePath);
            fis = new FileInputStream(new File(imagePath));
//            Log.d(LOG_TAG, "File Directory inside loadImageFromSystem " + moviePoster);
//            Bitmap moviePosterImage = BitmapFactory.decodeStream(new FileInputStream(moviePoster));
            Bitmap moviePosterImage = BitmapFactory.decodeStream(fis);
            iv.setImageBitmap(moviePosterImage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}



