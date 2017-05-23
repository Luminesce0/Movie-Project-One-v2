package com.omegaspocktari.movieprojectone;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.omegaspocktari.movieprojectone.adapters.MovieReviewsAdapter;
import com.omegaspocktari.movieprojectone.adapters.MovieVideosAdapter;
import com.omegaspocktari.movieprojectone.data.MovieContract.FavoriteMovies;
import com.omegaspocktari.movieprojectone.data.MovieContract.MovieColumns;
import com.omegaspocktari.movieprojectone.data.MovieContract.RegularMovies;
import com.omegaspocktari.movieprojectone.databinding.ActivityMovieDetailBinding;
import com.omegaspocktari.movieprojectone.utilities.TMDbUtils;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.io.File;

import static com.omegaspocktari.movieprojectone.utilities.TMDbUtils.currentSortingMethod;
import static com.omegaspocktari.movieprojectone.utilities.TMDbUtils.extractMovieReviewJsonDataToMDI;
import static com.omegaspocktari.movieprojectone.utilities.TMDbUtils.extractMovieVideoJsonDataToMDI;

/**
 * Activity that expands upon the specifics of a movie, greatly elaborating on the many details of
 * the movie, showcasing reviews, videos, and an option to save the movie
 * <p>
 * Created by ${Michael} on 11/11/2016.
 */

public class MovieDetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        MovieVideosAdapter.MovieTrailerAdapterOnClickHandler,
        MovieReviewsAdapter.MovieReviewsAdapterOnClickHandler {

    // Key
    public final static String BUNDLE_KEY = "mdi_key";
    // LOG_TAG implementation
    private static final String LOG_TAG = MovieDetailActivity.class.getSimpleName();
    // Loader id
    private static final int ID_MOVIE_LOADER = 0;
    // String value of file path
    private static String mFavoriteMovieLocation;
    // Views
    ActivityMovieDetailBinding mBinding;

    // Movie Data
    // Uri to access relevant data
    private int mPosition;
    private MovieDetailInfo mMDI;
    private Button mFavoriteMovieButton;
    private RecyclerView mRecyclerViewVideos;
    private RecyclerView mRecyclerViewReviews;

    // Recycler View Adapters
    private MovieVideosAdapter mMovieVideoAdapter;
    private MovieReviewsAdapter mMovieReviewsAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        // Replaces set content view
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail);

        // Initialize mMDI
        if (mMDI == null) {
            mMDI = new MovieDetailInfo();
            mMDI.mdiCreated = true;
        }

        // Grab views
        mFavoriteMovieButton = (Button) findViewById(R.id.bFavoriteMovie);
        mRecyclerViewVideos = (RecyclerView) findViewById(R.id.rvMovieVideos);
        mRecyclerViewReviews = (RecyclerView) findViewById(R.id.rvMovieReviews);

        // Layout Managers to appropriately measure and position views within the recycler views
        LinearLayoutManager layoutManagerVideos =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        LinearLayoutManager layoutManagerReviews =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        // Associate the layout managers with the relevant recycler views
        mRecyclerViewVideos.setLayoutManager(layoutManagerVideos);
        mRecyclerViewReviews.setLayoutManager(layoutManagerReviews);

        // Item decorations for both recycler views
        DividerItemDecoration dividerItemDecorationMovies = new DividerItemDecoration(mRecyclerViewVideos.getContext(),
                layoutManagerVideos.getOrientation());
        mRecyclerViewVideos.addItemDecoration(dividerItemDecorationMovies);

        DividerItemDecoration dividerItemDecorationReviews = new DividerItemDecoration(mRecyclerViewReviews.getContext(),
                layoutManagerVideos.getOrientation());
        mRecyclerViewReviews.addItemDecoration(dividerItemDecorationReviews);

        // Adapters
        mMovieVideoAdapter = new MovieVideosAdapter(this, this);
        mMovieReviewsAdapter = new MovieReviewsAdapter(this, this);

        // Plug adapters into relevant recycler views
        mRecyclerViewVideos.setAdapter(mMovieVideoAdapter);
        mRecyclerViewReviews.setAdapter(mMovieReviewsAdapter);

        // Acquire the correct row to load
        Bundle bundle = getIntent().getExtras();
        Uri mUri = (Uri) bundle.get(getString(R.string.movie_key));
        String position = mUri.getLastPathSegment();
        mPosition = Integer.valueOf(position);

        // Connect activity to loader
        getSupportLoaderManager().initLoader(ID_MOVIE_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_settings, menu);
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        getIntent().putExtra(BUNDLE_KEY, Parcels.wrap(mMDI));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            MovieDetailInfo mdi = Parcels.unwrap(bundle.getParcelable(BUNDLE_KEY));
            if (mdi != null) {
                mMDI = mdi;
                displayMovieDetails();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new AsyncTaskLoader<Cursor>(this) {

            public void onStartLoading() {
                forceLoad();
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Cursor loadInBackground() {
                Cursor cursor;
                if (currentSortingMethod.equals(getString(R.string.pref_sorting_favorites))) {
                    cursor = getContext().getContentResolver().query(FavoriteMovies.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);

                    cursor.moveToPosition(mPosition);


                } else {
                    cursor = getContext().getContentResolver().query(RegularMovies.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);

                    cursor.moveToPosition(mPosition);
                }
                String movieID = cursor.getString(cursor.getColumnIndex(MovieColumns.COLUMN_MOVIE_ID));

                if (!mMDI.mdiListDataStored) {
                    mMDI = extractMovieVideoJsonDataToMDI(getContext(), movieID, mMDI);
                    mMDI = extractMovieReviewJsonDataToMDI(getContext(), movieID, mMDI);
                    mMDI.mdiListDataStored = true;
                }
                return cursor;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        // Check integrity of the information
        boolean cursorHasValidData = false;
        if (data != null) {
            cursorHasValidData = true;
        }

        if (!cursorHasValidData) {
            return;
        }
        // Generate MovieDetailPage
        if (!mMDI.mdiDataStored) {
            mMDI = TMDbUtils.generateMovieDetailInfo(data, mMDI);
        }
        displayMovieDetails();

        // initialize button
        initializeMovieFavoriteButton(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onVideoClick(String key) {

        // Generate the youtube browser URI
        String videoUri = this.getString(R.string.movie_video_uri_base) + key;

        // Create intents, one for youtube and a fallback for a web browser
        Intent videoIntentApp = new Intent(Intent.ACTION_VIEW, Uri.parse(key));
        Intent videoIntentWeb = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUri));

        try {
            // Start in youtube app if available
            startActivity(videoIntentApp);
        } catch (ActivityNotFoundException ex) {
            // Start in web browser if youtube is not available
            startActivity(videoIntentWeb);
        }
    }

    @Override
    public void onReviewClick(String reviewUri) {
        // Create intent for the review to open up in the browser
        Intent reviewIntentWeb = new Intent(Intent.ACTION_VIEW, Uri.parse(reviewUri));

        // start intent
        startActivity(reviewIntentWeb);
    }

    /**
     * Initialize movie button with helper methods
     *
     * @param movie
     */
    private void initializeMovieFavoriteButton(Cursor movie) {
        int movieId = movie.
                getInt(movie.getColumnIndex(MovieColumns.COLUMN_MOVIE_ID));

        // Create selection and the necessary arguments to delete this specific movie
        String selection = MovieColumns.COLUMN_MOVIE_ID + "=?";
        String[] selectionArgs = new String[]{Integer.toString(movieId)};

        Cursor favoriteMovieDatabase = getContentResolver().query(FavoriteMovies.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null);

        // Set the appropriate button for a non-null/null response
        if (favoriteMovieDatabase.moveToFirst()) {
            setMovieFavoriteButtonStatus(true, movie);
        } else {
            setMovieFavoriteButtonStatus(false, movie);
        }
    }

    /**
     * Sets up the status of the movie button depending on whether or not it has been favorited
     *
     * @param favoriteStatus
     * @param movie
     */
    private void setMovieFavoriteButtonStatus(boolean favoriteStatus, final Cursor movie) {
        if (favoriteStatus == true) {
            mFavoriteMovieButton.setText(getResources().getString(R.string.btn_favorite_false));

            mFavoriteMovieButton.setOnClickListener(new View.OnClickListener() {
                /**
                 * Called when a view has been clicked.
                 *
                 * @param v The view that was clicked.
                 */
                @Override
                public void onClick(View v) {
                    removeFavoriteMovie(movie);
                }
            });
        } else {
            mFavoriteMovieButton.setText(getResources().getString(R.string.btn_favorite_true));

            mFavoriteMovieButton.setOnClickListener(new View.OnClickListener() {
                /**
                 * Called when a view has been clicked.
                 *
                 * @param v The view that was clicked.
                 */
                @Override
                public void onClick(View v) {
                    addNewFavoriteMovie(movie);
                }
            });
        }
    }

    /**
     * onClickListener to remove favorite movie
     *
     * @param movie
     */
    private void removeFavoriteMovie(Cursor movie) {

        // Gather information of movie
        int movieId = movie.
                getInt(movie.getColumnIndex(MovieColumns.COLUMN_MOVIE_ID));
        String movieTitle = movie.
                getString(movie.getColumnIndex(MovieColumns.COLUMN_MOVIE_TITLE));

        // Create selection and the necessary arguments to delete this specific movie
        String selection = MovieColumns.COLUMN_MOVIE_ID + "=?";
        String[] selectionArgs = new String[]{Integer.toString(movieId)};

        // Get the favorite movie in question to be able to acquire the photoPath stored on the device
        Cursor favoriteMovie = this.getContentResolver().query(
                FavoriteMovies.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null);

        // Move to the current favorite movie
        favoriteMovie.moveToFirst();

        // Movie poster path to delete movie file
        String posterPath = favoriteMovie.
                getString(favoriteMovie.getColumnIndex(MovieColumns.COLUMN_MOVIE_ID));

        // Get integer for deleted row to check deletion success
        int deletedRow = getContentResolver().
                delete(FavoriteMovies.CONTENT_URI, selection, selectionArgs);

        // Delete movie and return result as a toast
        if (deletedRow <= 0) {
            // Toast to verify that the deletion of the movie was unsuccessful
            Toast.makeText(this, "Error Deleting Movie: " + movieTitle,
                    Toast.LENGTH_LONG).show();
        } else {
            // Delete stored image
            File moviePosterPath = new File(this.getFilesDir(), posterPath);
            moviePosterPath.delete();

            // Toast to verify deletion of favorite movie to user
            Toast.makeText(this, movieTitle + " \nRemoved from Your Favorites",
                    Toast.LENGTH_LONG).show();

            // Set status to be able to refavorite the movie should the user decide
            setMovieFavoriteButtonStatus(false, movie);
        }
        // Close cursor
        favoriteMovie.close();

        // If currently sorted into Favorite movies when removing favorite, return to activity fragment
        if (TMDbUtils.currentSortingMethod.equals(getString(R.string.pref_sorting_favorites))) {
            Intent intent = new Intent(this, MovieActivity.class);

            startActivity(intent);
        }
    }

    /**
     * onClickListener to add favorite movie
     *
     * @param movie
     */
    public void addNewFavoriteMovie(Cursor movie) {
        // Gather all relevant information
        ContentValues cv = new ContentValues();
        int movieRow = movie.
                getInt(movie.getColumnIndex(MovieColumns.COLUMN_MOVIE_ID));
        String movieTitle = movie.
                getString(movie.getColumnIndex(MovieColumns.COLUMN_MOVIE_TITLE));
        String moviePlot = movie.
                getString(movie.getColumnIndex(MovieColumns.COLUMN_MOVIE_SYNOPSIS));
        Float movieUserRating = movie.
                getFloat(movie.getColumnIndex(MovieColumns.COLUMN_MOVIE_USER_RATING));
        String movieReleaseDate = movie.
                getString(movie.getColumnIndex(MovieColumns.COLUMN_MOVIE_RELEASE_DATE));

        // Create selection and the necessary arguments to add this specific movie
        String selection = MovieColumns.COLUMN_MOVIE_ID + "=?";
        String[] selectionArgs = new String[]{Integer.toString(movieRow)};

        // Query to see if this exists
        Cursor movieDatabase = getContentResolver().query(FavoriteMovies.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null);

        if (!(movieDatabase.moveToFirst())) {
            // Get movie id
            String movieId = movie.
                    getString(movie.getColumnIndex(MovieColumns.COLUMN_MOVIE_ID));
            Bitmap bitmap = ((BitmapDrawable) mBinding.ivMoviePoster.getDrawable()).getBitmap();

            mFavoriteMovieLocation = TMDbUtils.saveToInternalStorage(bitmap, movieId, this);

            if (mFavoriteMovieLocation != null) {
                cv.put(MovieColumns.COLUMN_MOVIE_ID, movieId);
                cv.put(MovieColumns.COLUMN_MOVIE_TITLE, movieTitle);
                cv.put(MovieColumns.COLUMN_MOVIE_POSTER, mFavoriteMovieLocation);
                cv.put(MovieColumns.COLUMN_MOVIE_SYNOPSIS, moviePlot);
                cv.put(MovieColumns.COLUMN_MOVIE_USER_RATING, movieUserRating);
                cv.put(MovieColumns.COLUMN_MOVIE_RELEASE_DATE, movieReleaseDate);

                // Add movie and return response as a toast
                Uri uri = getContentResolver().insert(FavoriteMovies.CONTENT_URI, cv);
                int id = Integer.valueOf(uri.getLastPathSegment());

                if (id <= 0) {
                    Toast.makeText(getApplicationContext(), "Error Adding Movie: "
                            + movieTitle, Toast.LENGTH_LONG).show();
                } else {
                    setMovieFavoriteButtonStatus(true, movie);
                    Toast.makeText(getApplicationContext(), movieTitle + " \nAdded to your Favorites",
                            Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(getApplicationContext(), "Error: Database row exists",
                        Toast.LENGTH_LONG).show();
            }
            movieDatabase.close();
        }
    }

    /**
     * Display the movie details from the data held in MDI
     */
    private void displayMovieDetails() {
        // Display picture with the correct means
        if (TMDbUtils.currentSortingMethod.equals(getString(R.string.pref_sorting_favorites))) {
            TMDbUtils.loadImageFromSystem(mMDI.moviePoster, mBinding.ivMoviePoster);
        } else {
            Picasso.with(this)
                    .load(mMDI.moviePoster)
                    .into(mBinding.ivMoviePoster);
        }

        // display the rest of the content
        mBinding.tvMovieSynopsis.setText(mMDI.moviePlot);
        mBinding.tvMovieReleaseDate.setText(mMDI.movieReleaseDate);
        mBinding.tvMovieTitle.setText(mMDI.movieTitle);
        mBinding.rbRating.setRating(mMDI.movieUserRating / 2);

        mMovieVideoAdapter.swapVideoAdapterMDI(mMDI);
        mMovieReviewsAdapter.swapReviewAdapterMDI(mMDI);
    }
}
