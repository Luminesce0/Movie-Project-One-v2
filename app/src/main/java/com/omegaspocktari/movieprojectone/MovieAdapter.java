package com.omegaspocktari.movieprojectone;

import android.content.Context;
import android.content.Intent;
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

// TODO: Main logic for this adapter is set up, simply need to fill up the ArrayList.
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();
    private ArrayList<Movie> mMoviesList;
    private Context mContext;

    public MovieAdapter(Context context, ArrayList<Movie> movieDataset) {
        mMoviesList = movieDataset;
        mContext = context;
    }

    public MovieAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Gather context from parent to inflate the layout of the given XML into the layoutView view.
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);

        // Utilize View Holder to obtain all the relevant view IDs from the provided layoutView
        ViewHolder viewHolder = new ViewHolder(layoutView);

        return viewHolder;
    }

    //TODO: Might need to do MovieAdapter.ViewHolder
    public void onBindViewHolder(ViewHolder holder, final int position) {


        Log.v(LOG_TAG, " Anything here? " + mMoviesList.get(position)
                .getMoviePoster().toString());

        // Here we will set all the relevant data from the current movie to
        // the list item.
        Picasso.with(mContext)
                .load(mMoviesList.get(position)
                        .getMoviePoster().toString())
                .into(holder.moviePosterFragment);

        holder.moviePosterFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent movieDetail = new Intent(mContext, MovieDetailActivity.class);

                movieDetail.putExtra(mContext.getString(R.string.movie_key), mMoviesList.get(position));

                mContext.startActivity(movieDetail);
            }
        });
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView moviePosterFragment;
//        public TextView movieTitle;
//        public TextView movieRelease;
//        public RatingBar movieRating;
//        public TextView moviePlot;

        public ViewHolder(View v) {
            super(v);
            moviePosterFragment = (ImageView) v.findViewById(R.id.movie_poster_list);
//            movieTitle = (TextView) v.findViewById(R.id.movie_title);
//            movieRelease = (TextView) v.findViewById(R.id.movie_release);
//            movieRating = (RatingBar) v.findViewById(R.id.movie_rating);
//            moviePlot = (TextView) v.findViewById(R.id.movie_plot);
        }
    }

}