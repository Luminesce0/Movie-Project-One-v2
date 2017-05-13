package com.omegaspocktari.movieprojectone;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.omegaspocktari.movieprojectone.sync.MovieSyncUtils;
import com.omegaspocktari.movieprojectone.utilities.TMDbUtils;

import static java.security.AccessController.getContext;

/**
 * Created by ${Michael} on 11/9/2016.
 */

//TODO: understand more about savedinstancestates (Bundle passing)
// TODO: Possibly get rid of refresh button.
public class MovieFragment extends Fragment implements
        MovieAdapter.MovieAdapterOnClickHandler {

    // Logging Tag
    private final static String LOG_TAG = MovieFragment.class.getSimpleName();

    // Loader ID
    private static final int MOVIE_RESULTS_LOADER = 0;

    // Key for movie results
    private static final String MOVIE_PREFERENCES = "preferences";

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

        // Initialize movie data from TMDb if database is empty
        MovieSyncUtils.initialize(getContext());

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

        Cursor movieList = TMDbUtils.getMovieData(getContext());
        if (movieList != null && movieList.moveToFirst()) {
            mAdapter.swapCursor(movieList);
            mEmptyStateView.setVisibility(View.GONE);
            mRefreshButton.setVisibility(View.GONE);
            Log.d(LOG_TAG, "if Success! Adapter Swapped");

            // if sorting method equals favorite movies show this error
        } else if (TMDbUtils.currentSortingMethod.equals(getString(R.string.pref_sorting_favorites))) {
            mEmptyStateView.setVisibility(View.VISIBLE);
            mEmptyStateView.setText(R.string.no_movies_favorited);
            mRefreshButton.setVisibility(View.GONE);
            Log.d(LOG_TAG, "No favorite movies");
            // if sorting method is not equal to favorites, show this response
        } else {
            mEmptyStateView.setVisibility(View.VISIBLE);
            mEmptyStateView.setText(R.string.no_movies_found);
            mRefreshButton.setVisibility(View.VISIBLE);
            Log.d(LOG_TAG, "No rated/popular movies");
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
}
