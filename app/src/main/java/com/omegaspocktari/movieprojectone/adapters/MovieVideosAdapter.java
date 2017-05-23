package com.omegaspocktari.movieprojectone.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.omegaspocktari.movieprojectone.MovieDetailInfo;
import com.omegaspocktari.movieprojectone.R;

/**
 * Recycler View Adapter for videos
 *
 * Created by ${Michael} on 5/15/2017.
 */

public class MovieVideosAdapter extends
        RecyclerView.Adapter<MovieVideosAdapter.MovieVideosAdapterViewHolder> {

    // LOG_TAG for debugging
    private static final String LOG_TAG = MovieVideosAdapter.class.getSimpleName();

    // Grab context to use utility methods, app resources and layout inflaters
    private final Context mContext;

    // Handle onReviewClick events
    final private MovieTrailerAdapterOnClickHandler mClickHandler;

    // Movie information detail
    private MovieDetailInfo mMDI;

    public MovieVideosAdapter(@NonNull Context context, MovieTrailerAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    @Override
    public MovieVideosAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the view for the Movie Video item
        View view = LayoutInflater.from(mContext).inflate(R.layout.movie_video_item, parent, false);

        // Allow the items to be clickable
        view.setFocusable(true);

        // Return the MovieVideosAdapterViewHolder with the inflated view
        return new MovieVideosAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieVideosAdapterViewHolder holder, int position) {
        // Acquire movie video data
        String videoType = mMDI.movieVideoType.get(position);
        String videoName = mMDI.movieVideoName.get(position);

        holder.videoTypeView.setText(videoType);
        holder.videoNameView.setText(videoName);
    }

    @Override
    public int getItemCount() {
        if (mMDI != null && mMDI.movieVideoKey != null){
            return mMDI.movieVideoKey.size();
        } else {
            return 0;
        }
    }

    /**
     * Swap the mdi object within the adapter with the mdi provided
     * Thereafter, reload the view
     *
     * @param mdi
     */
    public void swapVideoAdapterMDI(MovieDetailInfo mdi) {
        mMDI = mdi;
        notifyDataSetChanged();
    }

    /**
     * Interface that receives onReviewClick messages
     */
    public interface MovieTrailerAdapterOnClickHandler {
        void onVideoClick(String key);
    }

    class MovieVideosAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView videoGraphicView;

        final TextView videoTypeView;
        final TextView videoNameView;

        public MovieVideosAdapterViewHolder(View itemView) {
            super(itemView);

            videoGraphicView = (ImageView) itemView.findViewById(R.id.ivMovieVideoGraphic);
            videoTypeView = (TextView) itemView.findViewById(R.id.tvMovieVideoType);
            videoNameView = (TextView) itemView.findViewById(R.id.tvMovieVideoName);

            itemView.setOnClickListener(this);
        }

        // This is called by the clicked view.
        @Override
        public void onClick(View v) {
            String key = mMDI.movieVideoKey.get(getAdapterPosition());
            mClickHandler.onVideoClick(key);
        }
    }
}
