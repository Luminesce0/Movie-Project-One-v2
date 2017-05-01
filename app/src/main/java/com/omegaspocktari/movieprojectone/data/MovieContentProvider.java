package com.omegaspocktari.movieprojectone.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.omegaspocktari.movieprojectone.data.MovieContract.FavoriteMovies;
import com.omegaspocktari.movieprojectone.data.MovieContract.MovieColumns;
import com.omegaspocktari.movieprojectone.data.MovieContract.RegularMovies;

/**
 * Created by ${Michael} on 4/22/2017.
 */

public class MovieContentProvider extends ContentProvider {

    // Directory of movies
    public static final int REGULAR_MOVIES = 0;
    // Items in directory of movies
    public static final int REGULAR_MOVIES_WITH_ID = 1;

    // Directory of favorite movies
    public static final int FAVORITE_MOVIES = 100;
    // Items in directory of favorite movies
    public static final int FAVORITE_MOVIES_WITH_ID = 101;

    // URI matcher
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    // Member variable that's initialized in the onCreate method
    private MovieDbHelper mMovieDbHelper;

    // Associates URI's with their int match
    private static UriMatcher buildUriMatcher() {

        // Initializing the Uri Matcher with no matches
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // Add paths with a corresponding int variable.
        // Directory Uri Matcher
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_FAVORITE_MOVIES, FAVORITE_MOVIES);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_REGULAR_MOVIES, REGULAR_MOVIES);

        // Single Item Uri Matcher
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_FAVORITE_MOVIES + "/#", FAVORITE_MOVIES_WITH_ID);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_REGULAR_MOVIES + "/#", REGULAR_MOVIES_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mMovieDbHelper = new MovieDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Acquiring the database to enact queries
        SQLiteDatabase db = mMovieDbHelper.getReadableDatabase();
        // Return the produced cursor
        Cursor returnCursor;
        // Acquire the Uri Matcher's match whether we are getting a certain item or a directory


        switch (sUriMatcher.match(uri)) {
            // Favorite Movies
            case FAVORITE_MOVIES:
                returnCursor = db.query(FavoriteMovies.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case FAVORITE_MOVIES_WITH_ID:
                String fmID = uri.getLastPathSegment();
                String[] fmSelectionArguments = new String[]{fmID};

                returnCursor = db.query(
                        FavoriteMovies.TABLE_NAME,
                        projection,
                        MovieColumns.COLUMN_MOVIE_ID + "=? ",
                        fmSelectionArguments,
                        null,
                        null,
                        sortOrder);
                break;

            // Regular Movies
            case REGULAR_MOVIES:
                returnCursor = db.query(RegularMovies.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case REGULAR_MOVIES_WITH_ID:
                String rmID = uri.getLastPathSegment();
                String[] rmSelectionArguments = new String[]{rmID};

                returnCursor = db.query(
                        RegularMovies.TABLE_NAME,
                        projection,
                        MovieColumns.COLUMN_MOVIE_ID + "=? ",
                        rmSelectionArguments,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        // notify of cursor changes
        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        // Return the resulting cursor
        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // Acquiring the database to enact queries
        SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        // Returns the successful Uri
        Uri returnUri;
        // Acquire the Uri Matcher's match whether we are getting a certain item or a directory
        switch (sUriMatcher.match(uri)) {
            // Favorite Movies
            case FAVORITE_MOVIES:
                // Insert new values into database
                long fmID = db.insert(FavoriteMovies.TABLE_NAME, null, values);
                if (fmID > 0) {
                    returnUri = ContentUris.withAppendedId(FavoriteMovies.CONTENT_URI, fmID);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;

            // Regular Movies
            case REGULAR_MOVIES:
                // Insert new values into database
                long rmID = db.insert(RegularMovies.TABLE_NAME, null, values);
                if (rmID > 0) {
                    returnUri = ContentUris.withAppendedId(RegularMovies.CONTENT_URI, rmID);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        // Notify the resolver of any potential uri changes
        getContext().getContentResolver().notifyChange(uri, null);
        // Return constructed uri that points to inserted row of data
        return returnUri;
    }

    // TODO: Implement this if we decide to move JSON data to a database

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        return super.bulkInsert(uri, values);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Acquiring the database to enact queries
        SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        // Return int
        int rowsDeleted;
        // Acquire the Uri Matcher's match whether we are getting a certain item or a directory

        switch (sUriMatcher.match(uri)) {
            // Favorite Movies
            case FAVORITE_MOVIES:
                // Delete all database entries.
                // TODO: Potentially remove if not necessary
                rowsDeleted = db.delete(FavoriteMovies.TABLE_NAME, null, null);
                break;
            case FAVORITE_MOVIES_WITH_ID:
                // Acquire the movie ID from the URI path
                String fmID = uri.getPathSegments().get(1);
                // Selection / Selection args will filter what to delete
                rowsDeleted = db.delete(FavoriteMovies.TABLE_NAME, "_id=?", new String[]{fmID});
                break;

            // Regular Movies
            case REGULAR_MOVIES:
                // Delete all database entries.
                // TODO: Potentially remove if not necessary
                rowsDeleted = db.delete(RegularMovies.TABLE_NAME, null, null);
                break;
            case REGULAR_MOVIES_WITH_ID:
                // Acquire the movie ID from the URI path
                String rmID = uri.getPathSegments().get(1);
                // Selection / Selection args will filter what to delete
                rowsDeleted = db.delete(RegularMovies.TABLE_NAME, "_id=?", new String[]{rmID});
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        // Notify resolver of a change
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the amount of deleted movies
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
