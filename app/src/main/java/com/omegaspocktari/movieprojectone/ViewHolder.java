package com.omegaspocktari.movieprojectone;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by ${Michael} on 11/14/2016.
 */

public class ViewHolder extends RecyclerView.ViewHolder {
    private static final String LOG_TAG = ViewHolder.class.getSimpleName();

    public ImageView moviePosterFragment;

    public ViewHolder(View itemView) {
        super(itemView);
        moviePosterFragment = (ImageView) itemView.findViewById(R.id.movie_poster_list);
    }

    public void bind(final Movie movie, final MovieAdapter.OnItemClickListener listener) {

        // Here we will set all the relevant data from the current movie to
        // the list item.
        Picasso.with(itemView.getContext())
                .load(movie.getMoviePoster().toString())
                .into(moviePosterFragment);
        moviePosterFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                listener.onItemClick(movie);
            }
        });
    }
}
