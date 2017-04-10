package com.omegaspocktari.movieprojectone;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ${Michael} on 11/9/2016.
 */
public class MovieFragment extends Fragment implements MovieAdapter.MovieAdapterOnClickHandler{

    private final static String LOG_TAG = MovieFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;

    private ArrayList<Movie> mMovies = new ArrayList<>();

    private RecyclerView.Adapter mAdapter;

    private StaggeredGridLayoutManager staggeredGridLayoutManager;

    private TextView mEmptyStateView;

    private NetworkInfo networkInfo;

    private ProgressBar mProgressBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Allow fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movie_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
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

            // Generate the AsyncTask.
            FetchMoviesTask moviesTask = new FetchMoviesTask();

            // Gather preference with the default being popularity.
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            String sortingPreference = prefs.getString(
                    getString(R.string.pref_sorting_key),
                    getString(R.string.pref_sorting_popularity));

            // Run AsyncTask with the preference gathered from the user.
            moviesTask.execute(sortingPreference);

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

    public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>> {
        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        protected ArrayList<Movie> doInBackground(String... params) {

            // Run the methods from QueryUtils to acquire an array list of movie objects
            // derived from user preference inputs/defaults and JSON queries.
            ArrayList<Movie> movieList = (ArrayList<Movie>)
                    QueryUtils.getMovieDataFromJson(params[0], getContext());

            // Return the output of QueryUtil methods.
            return movieList;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movieList) {
            super.onPostExecute(movieList);

            // Hide progress bar
            mProgressBar.setVisibility(View.GONE);

            // If the Array List was populated with movie objects insert them into the adapter.
            if (movieList != null) {
                mMovies.clear();
                mMovies.addAll(movieList);
                mAdapter.notifyDataSetChanged();
                mEmptyStateView.setVisibility(View.GONE);

            }
        }
    }
}
