package com.omegaspocktari.movieprojectone;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import com.omegaspocktari.movieprojectone.data.MovieContract.FavoriteMovies;
import com.omegaspocktari.movieprojectone.data.MovieDbHelper;
import com.omegaspocktari.movieprojectone.data.MoviePreferences;
import com.omegaspocktari.movieprojectone.utilities.TMDbJsonUtils;

import java.util.ArrayList;

/**
 * Created by ${Michael} on 11/9/2016.
 */

//TODO: understand more about savedinstancestates (Bundle passing)
    // TODO: Possibly get rid of refresh button.
public class MovieFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<ArrayList<Movie>>,
        MovieAdapter.MovieAdapterOnClickHandler{

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
    private RecyclerView.Adapter mAdapter;
    private ArrayList<Movie> mMovies = new ArrayList<>();
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

        // Create a DB helper
        MovieDbHelper dbHelper = new MovieDbHelper(getContext());

        // Get a readable database
        mDb = dbHelper.getReadableDatabase();

        // Setup the adapter to a default
        mAdapter = new MovieAdapter(getContext(), mMovies, this);

        // Set the adapter to our recycler view
        mRecyclerView.setAdapter(mAdapter);

        if (mAdapter.getItemCount() <= 0) {
            // Setup the empty state view should no data be acquired.
            mEmptyStateView = (TextView) rootView.findViewById(R.id.tv_no_results);
            mEmptyStateView.setText(R.string.no_movies_found);
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Every time onStart is called update the movie list.
        updateMovies();
    }

    private void updateMovies() {

        // Should a network connection be present, attempt to fetch data.
        if (networkInfo != null && networkInfo.isConnected()) {

            // Gather preference with the default being popularity.
            String sortingPreference = MoviePreferences.getPreferredMovieSorting(getContext());

            Bundle movieUpdateBundle = new Bundle();
            movieUpdateBundle.putString(MOVIE_PREFERENCES, sortingPreference);

            LoaderManager loaderManager = getActivity().getSupportLoaderManager();
            Loader<String> movieLoader = loaderManager.getLoader(MOVIE_RESULTS_LOADER);
            if (movieLoader == null) {
                loaderManager.initLoader(MOVIE_RESULTS_LOADER, movieUpdateBundle, this);
            } else {
                loaderManager.restartLoader(MOVIE_RESULTS_LOADER, movieUpdateBundle, this);
            }
        }
    }

    @Override
    public void onListItemClick(Movie movie) {
        Log.d(LOG_TAG, "\n\nThis is the onClick run within the instantiation of" +
                "mAdapter/creating a new MovieAdapter\n\n");

        Intent movieDetail = new Intent(getContext(), MovieDetailActivity.class);

        movieDetail.putExtra(getString(R.string.movie_key), movie);

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
    public Loader<ArrayList<Movie>> onCreateLoader(int id, final Bundle args) {
        // Generate an AsyncTask that will obtain the movie information within loader.

        return new AsyncTaskLoader<ArrayList<Movie>>(getContext()) {

            public void onStartLoading() {
                    mProgressBar.setVisibility(View.VISIBLE);
                    forceLoad();
            }

            @Override
            public ArrayList<Movie> loadInBackground() {

                String jsonUrlPreferences = args.getString(MOVIE_PREFERENCES);


                // Get all info and store in a cursor
                Cursor cursor = getFavoriteMovies();

                // Run the methods from TMDbJsonUtils to acquire an array list of movie objects
                // derived from user preference inputs/defaults and JSON queries.
                ArrayList<Movie> movieList = (ArrayList<Movie>)
                        TMDbJsonUtils.getMovieDataFromJson(getContext(), jsonUrlPreferences,  cursor);

                // Return the output of QueryUtil methods.
                return movieList;

            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> movieList) {
        // Hide progress bar
        mProgressBar.setVisibility(View.GONE);

        // If the Array List was populated with movie objects insert them into the adapter.
        if (movieList != null) {
            mMovies.clear();
            mMovies.addAll(movieList);
            mAdapter.notifyDataSetChanged();
            mEmptyStateView.setVisibility(View.GONE);
            mRefreshButton.setVisibility(View.GONE);
        } else {
            mEmptyStateView.setVisibility(View.VISIBLE);
            mRefreshButton.setVisibility(View.VISIBLE);
        }
    }

    //TODO: Figure out why this is necessary and must be implemented when it is never run.
    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, Movie data) {

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {

    }

    /**
     * Query the mDb and get all guests from the waitlist table
     *
     * @return Cursor containing the list of guests
     */
    private Cursor getFavoriteMovies() {
        return getContext().getContentResolver().query(
                FavoriteMovies.CONTENT_URI,
                null,
                null,
                null,
                null);
    }
}
