package com.omegaspocktari.movieprojectone;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.omegaspocktari.movieprojectone.data.MovieContract.MovieColumns;
import com.omegaspocktari.movieprojectone.utilities.TMDbUtils;
import com.squareup.picasso.Picasso;

/**
 * Created by ${Michael} on 11/4/2016.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();
    private final MovieAdapterOnClickHandler mClickHandler;
    private Cursor mCursor;
    private Context mContext;

    public MovieAdapter(Context context, MovieAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Gather context from parent to inflate the layout of the given XML into the layoutView view.
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);

        // Utilize View Holder to obtain all the relevant view IDs from the provided layoutView
        MovieViewHolder viewHolder = new MovieViewHolder(layoutView);


        Log.d(LOG_TAG, "MovieViewHolder");
        return viewHolder;
    }

    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Log.d(LOG_TAG, "onBindViewHolder");
        mCursor.moveToPosition(position);
        holder.bind(mCursor);
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        } else {
            return mCursor.getCount();
        }
    }

    // Create an MovieAdapterOnClickHandler interface.
    public interface MovieAdapterOnClickHandler {
        void onListItemClick(int movie);
    }

    /**
     * MovieViewHolder for MovieAdapter.
     */
    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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
            itemView.setOnClickListener(this);
        }

        public void bind(Cursor movieCursor) {

            Log.d(LOG_TAG, "Does movie Cursor equal null?");
            if (movieCursor != null) {

                String photoPath = movieCursor.getString(movieCursor.getColumnIndex(MovieColumns.COLUMN_MOVIE_POSTER));

                if (TMDbUtils.currentSortingMethod.equals(mContext.getString(R.string.pref_sorting_favorites))) {
                    Log.d(LOG_TAG, "Inside the favorite utils");
                    String movieId = movieCursor.
                            getString(movieCursor.getColumnIndex(MovieColumns.COLUMN_MOVIE_ID));
                    String moviePath = movieCursor.
                            getString(movieCursor.getColumnIndex(MovieColumns.COLUMN_MOVIE_POSTER));
                    // Binding picture to the relevant view
                    TMDbUtils.loadImageFromSystem(moviePath, listItemMoviePoster);
                } else {
                    Log.d(LOG_TAG, "Inside the Popularity/Rating utils");
                    // Binding picture to the relevant view
                    Picasso.with(itemView.getContext())
                            .load(photoPath)
                            .into(listItemMoviePoster);
                }

            } else {
                // Do Nothing
                Log.d(LOG_TAG, "yes!");
            }
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            Log.d(LOG_TAG, "\n\nThis is the onClick run within the MovieViewHolder\n\n");
            int adapterPosition = getAdapterPosition();

            mClickHandler.onListItemClick(adapterPosition);
        }
    }
}