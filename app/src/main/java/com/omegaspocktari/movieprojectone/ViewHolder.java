package com.omegaspocktari.movieprojectone;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by ${Michael} on 11/14/2016.
 */

public class ViewHolder extends RecyclerView.ViewHolder {
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
