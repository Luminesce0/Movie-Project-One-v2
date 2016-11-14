package com.omegaspocktari.movieprojectone;

import android.content.Context;
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
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ${Michael} on 11/9/2016.
 */
public class MovieFragment extends Fragment {

    private final static String LOG_TAG = MovieFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;

    private RecyclerView.Adapter mAdapter;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;

    private TextView mEmptyStateView;

    private NetworkInfo networkInfo;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Allow fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO: Create fragment for menu
        inflater.inflate(R.menu.movie_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO: Check this out later.
//        int id = item.getItemId();
//        if (id == R.id.action_refresh) {
//            // TODO: make this too.
//            updateMovies();
//            return true;
//        }
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

        // Acquire a connectivity manager to see if the network is connected.
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get the current active network's info.
        networkInfo = connectivityManager.getActiveNetworkInfo();

        // This will allow for our RecyclerView to be bound to the layout programmatically.
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);

        // This improves performance if the changes in content do not change layout size
        mRecyclerView.setHasFixedSize(true);

        // Grid layout manager does not function with the getActivity() method so we must
        // figure out what to do for that little tidbit.
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);

        // Setup the adapter to a default
        mAdapter = new MovieAdapter(getContext(), new ArrayList<Movie>());

        if (mAdapter.getItemCount() <= 0 ) {
            // Setup the empty state view should no data be acquired.
            mEmptyStateView = (TextView) rootView.findViewById(R.id.empty_view);
            mEmptyStateView.setText(R.string.no_movies_found);
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Every time onStart is called update the movie list.
        Log.v(LOG_TAG, "Update Movies in onStart");
        updateMovies();
    }

    // TODO: Generate Movie Option Sorting Choice somewhere. Probably not here... why is this here?
    private void updateMovies() {

        Log.v(LOG_TAG, "updateMovies method.");
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

    public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>> {
        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        protected ArrayList<Movie> doInBackground(String... params) {

            // Run the methods from QueryUtils to acquire an array list of movie objects
            // derived from user preference inputs/defaults and JSON queries.
            ArrayList<Movie> movies = (ArrayList<Movie>)
                    QueryUtils.getMovieDataFromJson(params[0], getContext());

            // Return the output of QueryUtil methods.
            return movies;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            super.onPostExecute(movies);

            // If the Array List was populated with movie objects insert them into the adapter.
            Log.v(LOG_TAG, "Are movies null?");
            if (movies != null) {
                mAdapter = new MovieAdapter(getContext(), movies);
                Log.v(LOG_TAG, "How many items? " + mAdapter.getItemCount());
                Log.v(LOG_TAG, "Are movies null?! NOOOOOOO :D ");
                mEmptyStateView.setVisibility(View.GONE);

            }
        }
    }
}
