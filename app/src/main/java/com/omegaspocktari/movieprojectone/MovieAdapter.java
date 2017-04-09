package com.omegaspocktari.movieprojectone;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
    private final ListItemClickListener mOnItemClickListener;
    private ArrayList<Movie> mMoviesList;
    private Context mContext;

    public MovieAdapter(Context context, ArrayList<Movie> movieList, ListItemClickListener listener) {
        mMoviesList = movieList;
        mContext = context;
        mOnItemClickListener = listener;
    }

    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Gather context from parent to inflate the layout of the given XML into the layoutView view.
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);

        // Utilize View Holder to obtain all the relevant view IDs from the provided layoutView
        MovieViewHolder viewHolder = new MovieViewHolder(layoutView);


        return viewHolder;
    }

    public void onBindViewHolder(MovieViewHolder holder, final int position) {
        holder.bind(mMoviesList.get(position), mOnItemClickListener);
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

    // Create an ListItemClickListener interface.
    public interface ListItemClickListener {
        void onListItemClick(Movie movie);
    }

    /**
     * MovieViewHolder for MovieAdapter.
     */
    public class MovieViewHolder extends RecyclerView.ViewHolder {
        private final String LOG_TAG = MovieViewHolder.class.getSimpleName();

        // ImageView to display a movie poster
        public ImageView listItemMoviePoster;

        /**
         * Constructor for MovieViewHolder. The constructor obtains a reference to the ImageViews
         *
         * @param itemView
         */
        public MovieViewHolder(View itemView) {
            super(itemView);
            listItemMoviePoster = (ImageView) itemView.findViewById(R.id.iv_movie_poster);
        }

        public void bind(final Movie movie, final ListItemClickListener listener) {

            // Here we will set all the relevant data from the current movie to
            // the list item.

            // Binding picture to the relevant view
            Picasso.with(itemView.getContext())
                    .load(movie.getMoviePoster().toString())
                    .into(listItemMoviePoster);

            // Binding onClickListener to the view
            listItemMoviePoster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    listener.onListItemClick(movie);
                }
            });
        }
    }
}