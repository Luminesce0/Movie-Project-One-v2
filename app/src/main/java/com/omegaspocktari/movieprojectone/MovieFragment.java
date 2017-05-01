package com.omegaspocktari.movieprojectone;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.omegaspocktari.movieprojectone.data.MovieContract;
import com.omegaspocktari.movieprojectone.data.MoviePreferences;
import com.omegaspocktari.movieprojectone.utilities.TMDbUtils;

/**
 * Created by ${Michael} on 11/9/2016.
 */

//TODO: understand more about savedinstancestates (Bundle passing)
// TODO: Possibly get rid of refresh button.
public class MovieFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        MovieAdapter.MovieAdapterOnClickHandler {

    // Logging Tag
    private final static String LOG_TAG = MovieFragment.class.getSimpleName();

    // Loader ID
    private static final int MOVIE_RESULTS_LOADER = 0;

    // Key for movie results
    private static final String MOVIE_PREFERENCES = "preferences";

    // NetworkInfo to check network connectivity
    private NetworkInfo networkInfo;

    // Views for class
    private ProgressBar mProgressBar;
    private TextView mEmptyStateView;
    private RecyclerView mRecyclerView;
    private Button mRefreshButton;

    // Adapter and relevant objects
    private MovieAdapter mAdapter;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;

    // Cursor and Database
    private SQLiteDatabase mDb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Allow fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null (which
     * is the default implementation).  This will be called between
     * {@link #onCreate(Bundle)} and {@link #onActivityCreated(Bundle)}.
     * <p/>
     * <p>If you return a View from here, you will later be called in
     * {@link #onDestroyView} when the view is being released.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);

        // Progress Bar
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.pb_network);

        // Empty State TextView
        mEmptyStateView = (TextView) rootView.findViewById(R.id.tv_no_results);

        // Refresh Button
        mRefreshButton = (Button) rootView.findViewById(R.id.b_refresh);
        mRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMovies();
            }
        });

        // Acquire a connectivity manager to see if the network is connected.
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get the current active network's info.
        networkInfo = connectivityManager.getActiveNetworkInfo();

        // This will allow for our RecyclerView to be bound to the layout programmatically.
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_movie_fragment);

        // This improves performance if the changes in content do not change layout size
        mRecyclerView.setHasFixedSize(true);

        // Set the layout manager appropriately.
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);

        // Setup the adapter to a default
        mAdapter = new MovieAdapter(getContext(), this);

        // Set the adapter to our recycler view
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d(LOG_TAG, "onStart called");
        // Every time onStart is called update the movie list.
        updateMovies();
    }

    // TODO: Update this with other stuff.
    private void updateMovies() {
        Log.d(LOG_TAG, "updateMovies()");

        String sortingPreference = MoviePreferences.getPreferredMovieSorting(getContext());

        // Should a network connection be present, attempt to fetch data,
        // however, should the sorting preference be favorites, bypass network
        // for offline capabilities.
        if ((sortingPreference.equals(getString(R.string.pref_sorting_favorites)))
        || (networkInfo != null && networkInfo.isConnected())) {

            // Gather preference with the default being popularity.

            Bundle movieUpdateBundle = new Bundle();
            movieUpdateBundle.putString(MOVIE_PREFERENCES, sortingPreference);

            LoaderManager loaderManager = getActivity().getSupportLoaderManager();
            Loader<String> movieLoader = loaderManager.getLoader(MOVIE_RESULTS_LOADER);
            if (movieLoader == null) {
                loaderManager.initLoader(MOVIE_RESULTS_LOADER, movieUpdateBundle, this).forceLoad();
            } else {
                loaderManager.restartLoader(MOVIE_RESULTS_LOADER, movieUpdateBundle, this).forceLoad();
                // Do nothing.
            }
        }
    }

    @Override
    public void onListItemClick(int id) {
        Log.d(LOG_TAG, "\n\nThis is the onClick run within the instantiation of" +
                "mAdapter/creating a new MovieAdapter\n\n");

        Intent movieDetail = new Intent(getContext(), MovieDetailActivity.class);

        if (TMDbUtils.currentSortingMethod.equals(getString(R.string.pref_sorting_favorites))) {
            Uri uri = MovieContract.FavoriteMovies.CONTENT_URI.
                    buildUpon().
                    appendPath(Integer.toString(id)).
                    build();
            movieDetail.putExtra(getString(R.string.movie_key), uri);
        } else {
            Uri uri = MovieContract.RegularMovies.CONTENT_URI.
                    buildUpon().
                    appendPath(Integer.toString(id)).
                    build();
            movieDetail.putExtra(getString(R.string.movie_key), uri);
        }

        startActivity(movieDetail);
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle args) {
        Log.d(LOG_TAG, "onCreateLoader()");
        // Generate an AsyncTask that will obtain the movie information within loader.

        return new AsyncTaskLoader<Cursor>(getContext()) {

            public void onStartLoading() {
                Log.d(LOG_TAG, "onStartLoading()");

                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public Cursor loadInBackground() {

                Log.d(LOG_TAG, "loadInBackground()");
                // Acquire the preference for the listed movies
                String jsonUrlPreferences = args.getString(MOVIE_PREFERENCES);

                // Run the methods from TMDbUtils to acquire an array list of movie objects
                // derived from user preference inputs/defaults and JSON queries.
                if (jsonUrlPreferences.equals(getString(R.string.pref_sorting_popularity)) ||
                        jsonUrlPreferences.equals((getString(R.string.pref_sorting_rating)))) {
                    Log.d(LOG_TAG, "" + jsonUrlPreferences);
                    Log.d(LOG_TAG, "Returning POPULARITY or RATING results");
                    return TMDbUtils.getMovieDataFromJson(getContext(), jsonUrlPreferences);

                } else {
                    // Derive data set by favoriting movies
                    Log.d(LOG_TAG, "" + jsonUrlPreferences);
                    Log.d(LOG_TAG, "Returning FAVORITES");
                    return TMDbUtils.getFavoriteMovieData(getContext());
                }

            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor movieList) {
        // Hide progress bar
        mProgressBar.setVisibility(View.GONE);

        // If the Array List was populated with movie objects insert them into the adapter.
        if (movieList != null && movieList.getCount() > 0) {
            mAdapter.swapCursor(movieList);
            mEmptyStateView.setVisibility(View.GONE);
            mRefreshButton.setVisibility(View.GONE);
            Log.d(LOG_TAG, "if Success! Adapter Swapped");

        } else if (TMDbUtils.currentSortingMethod.equals(R.string.pref_sorting_favorites)) {
            mEmptyStateView.setVisibility(View.VISIBLE);
            mEmptyStateView.setText(R.string.no_movies_favorited);
            mRefreshButton.setVisibility(View.VISIBLE);
            Log.d(LOG_TAG, "No favorite movies");
        } else {
            mEmptyStateView.setVisibility(View.VISIBLE);
            mEmptyStateView.setText(R.string.no_movies_found);
            mRefreshButton.setVisibility(View.VISIBLE);
            Log.d(LOG_TAG, "No rated/popular movies");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
