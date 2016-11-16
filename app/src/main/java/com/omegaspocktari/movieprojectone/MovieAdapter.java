package com.omegaspocktari.movieprojectone;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by ${Michael} on 11/4/2016.
 */

// TODO: Main logic for this adapter is set up, simply need to fill up the ArrayList.
public class MovieAdapter extends RecyclerView.Adapter<ViewHolder> {
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();
    private ArrayList<Movie> mMoviesList;
    private Context mContext;

    // Create an OnItemClickListener interface.
    public interface OnItemClickListener{
        void onItemClick(Movie movie);
    }

    private final OnItemClickListener mOnItemClickListener;

    public MovieAdapter(Context context, ArrayList<Movie> movieList, OnItemClickListener onItemClickListener) {
        mMoviesList = movieList;
        mContext = context;
        mOnItemClickListener = onItemClickListener;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Gather context from parent to inflate the layout of the given XML into the layoutView view.
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);

        // Utilize View Holder to obtain all the relevant view IDs from the provided layoutView
        ViewHolder viewHolder = new ViewHolder(layoutView);


        return viewHolder;
    }


    //TODO: Might need to do MovieAdapter.ViewHolder
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.v(LOG_TAG, "Hey! This just ran through the onBindViewHolder of [MovieAdapter]");
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

}