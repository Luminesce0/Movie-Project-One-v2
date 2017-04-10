package com.omegaspocktari.movieprojectone;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ${Michael} on 11/4/2016.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();
    private final MovieAdapterOnClickHandler mClickHandler;
    private ArrayList<Movie> mMoviesList;
    private Context mContext;

    public MovieAdapter(Context context, ArrayList<Movie> movieList, MovieAdapterOnClickHandler clickHandler) {
        mMoviesList = movieList;
        mContext = context;
        mClickHandler = clickHandler;
    }

    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Gather context from parent to inflate the layout of the given XML into the layoutView view.
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);

        // Utilize View Holder to obtain all the relevant view IDs from the provided layoutView
        MovieViewHolder viewHolder = new MovieViewHolder(layoutView);


        return viewHolder;
    }

    public void onBindViewHolder(MovieViewHolder holder, final int position) {
        holder.bind(mMoviesList.get(position), mClickHandler);
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return mMoviesList.size();
    }

    // Create an MovieAdapterOnClickHandler interface.
    public interface MovieAdapterOnClickHandler {
        void onListItemClick(Movie movie);
    }

    /**
     * MovieViewHolder for MovieAdapter.
     */
    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final String LOG_TAG = MovieViewHolder.class.getSimpleName();

        // ImageView to display a movie poster
        public ImageView listItemMoviePoster;
        private Movie listItemMovie;

        /**
         * Constructor for MovieViewHolder. The constructor obtains a reference to the ImageViews
         *
         * @param itemView
         */
        public MovieViewHolder(View itemView) {
            super(itemView);
            listItemMoviePoster = (ImageView) itemView.findViewById(R.id.iv_movie_poster);
            itemView.setOnClickListener(this);
        }

        public void bind(final Movie movie, final MovieAdapterOnClickHandler listener) {

            // Here we will set all the relevant data from the current movie to
            // the list item.

            // Binding picture to the relevant view
            Picasso.with(itemView.getContext())
                    .load(movie.getMoviePoster().toString())
                    .into(listItemMoviePoster);

            listItemMovie = movie;
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            Log.d(LOG_TAG, "\n\nThis is the onClick run within the MovieViewHolder\n\n");
            Movie movie = listItemMovie;
            mClickHandler.onListItemClick(movie);
        }
    }
}