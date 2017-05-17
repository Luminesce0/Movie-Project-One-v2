package com.omegaspocktari.movieprojectone.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.omegaspocktari.movieprojectone.MovieDetailInfo;
import com.omegaspocktari.movieprojectone.R;

/**
 * Created by ${Michael} on 5/15/2017.
 */

public class MovieReviewsAdapter extends
        RecyclerView.Adapter<MovieReviewsAdapter.MovieReviewsAdapterViewHolder> {

    // LOG_TAG for debugging
    private static final String LOG_TAG = MovieReviewsAdapter.class.getSimpleName();

    // Grab context to use utility methods, app resources and layout inflaters
    private final Context mContext;

    // Handle onReviewClick events
    final private MovieReviewsAdapterOnClickHandler mClickHandler;

    // Movie information details
    private MovieDetailInfo mMDI;

    public MovieReviewsAdapter(@NonNull Context context, MovieReviewsAdapterOnClickHandler clickHandler) {
        Log.d(LOG_TAG, "Look at me!");
        mContext = context;
        mClickHandler = clickHandler;
    }

    @Override
    public MovieReviewsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the view for the Movie Review item
        View view = LayoutInflater.from(mContext).inflate(R.layout.movie_review_item, parent, false);

        // Allow the items to be clickable
        view.setFocusable(true);

        // Return the MovieReviewsAdapterViewHolder with the inflated view
        return new MovieReviewsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieReviewsAdapterViewHolder holder, int position) {
        // Acquire movie review data
        String reviewAuthor = mMDI.movieReviewAuthor.get(position);
        String reviewContent = mMDI.movieReviewContent.get(position);

        holder.reviewAuthorView.append(" " + reviewAuthor);
        holder.reviewContentView.setText(reviewContent);
    }

    @Override
    public int getItemCount() {
        if (mMDI != null && mMDI.movieReviewAuthor != null) {
            return mMDI.movieReviewAuthor.size();
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
    public void swapReviewAdapterMDI(MovieDetailInfo mdi) {
        Log.d(LOG_TAG, "SWAP REVIEW DETAILS");
        mMDI = mdi;
        notifyDataSetChanged();
    }

    /**
     * Interface that receives onReviewClick messages
     */
    public interface MovieReviewsAdapterOnClickHandler {
        void onReviewClick(String reviewUri);
    }

    public class MovieReviewsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        final TextView reviewAuthorView;
        final TextView reviewContentView;

        public MovieReviewsAdapterViewHolder(View itemView) {
            super(itemView);

            reviewAuthorView = (TextView) itemView.findViewById(R.id.tvMovieReviewAuthor);
            reviewContentView = (TextView) itemView.findViewById(R.id.tvMovieReviewContent);

            itemView.setOnClickListener(this);
        }

        // This is called by the clicked view
        @Override
        public void onClick(View v) {
            String reviewUri = mMDI.movieReviewUrl.get(getAdapterPosition());
            mClickHandler.onReviewClick(reviewUri);
        }
    }
}
