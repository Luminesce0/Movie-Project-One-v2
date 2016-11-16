package com.omegaspocktari.movieprojectone;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${Michael} on 11/7/2016.
 */
public class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();
    private static final String SCHEME = "http";
    private static final String AUTHORITY = "api.themoviedb.org";
    private static final String API_VERSION_PARAM = "3";
    private static final String API_QUERY = "api_key";

    /**
     * This class's static methods & variables are directly accessible from the class name.
     */
    private QueryUtils() {
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
    private static URL createUrl(String stringUrl) {
        URL url = null;

        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem constructing URL Object [QueryUtils].", e);
        }

        return url;
    }

    private static String makeHTTPRequest(URL url) throws IOException {
        String jsonResponse = "";

        // End HTTP Request if the url is null
        if (url == null) {
            return jsonResponse;
        }

        // URL Connection to acquire data from the web and the variable we'll be storing it in
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(1000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error making an HTTP Request... ", e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            } if (inputStream != null) {
                inputStream.close();
            }
        }

        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();

        if (inputStream != null) {
            // Byte stream to character stream. Decodes bytes utilizing the given charset.
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            // Good to wrap buffered reader around Readers whose read() operations may be costly.
            BufferedReader reader = new BufferedReader(inputStreamReader);
            // Simple loop to read through the inputStream.
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }

        return output.toString();
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

            String movieTitle;
            String movieReleaseDate;
            String moviePoster;
            String movieVoteAverage;
            String moviePlotSynopsis;

            JSONObject movieJson = new JSONObject(jsonResponse);
            JSONArray movieResultsArray = movieJson.getJSONArray(TMDB_RESULTS);

            for (int i = 0; i < movieResultsArray.length(); i++) {

                // Gather relevant information from the result objects in movieResultsArray.
                JSONObject movieJsonObject = movieResultsArray.getJSONObject(i);
                movieTitle = movieJsonObject.getString(TMDB_TITLE);
                movieReleaseDate = movieJsonObject.getString(TMDB_RELEASE_DATE);
                moviePoster = context.getString(R.string.poster_base_url)
                        + context.getString(R.string.poster_size)
                        + movieJsonObject.getString(TMDB_MOVIE_POSTER);
                movieVoteAverage = movieJsonObject.getString(TMDB_VOTE_AVERAGE);
                moviePlotSynopsis = movieJsonObject.getString(TMDB_PLOT_SYNOPSIS);

                // Create an [@link Movie} object with the parsed data.
                Movie movieObject = new Movie(movieTitle, moviePlotSynopsis, movieVoteAverage,
                        movieReleaseDate, moviePoster);

                movies.add(movieObject);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the JSON results [Query Utils].", e);
        }

        return movies;
    }
}

