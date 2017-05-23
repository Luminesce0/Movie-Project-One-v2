package com.omegaspocktari.movieprojectone.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.omegaspocktari.movieprojectone.data.MovieContract.MovieColumns;
import com.omegaspocktari.movieprojectone.data.MovieContract.RegularMovies;

import static com.omegaspocktari.movieprojectone.data.MovieContract.FavoriteMovies;

/**
 * Creates and upgrades the TMDb.db tables
 *
 * Created by ${Michael} on 4/18/2017.
 */

public class MovieDbHelper extends SQLiteOpenHelper{

    // Database file name
    public static final String DATABASE_NAME = "TMDb.db";

    // Current version of the database schema
    public static final int DATABASE_VERSION = 3;

    /**
     * Create a helper object to create, open, and/or manage a database.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @param context to use to open or create the database
     */
    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creating SQL string command to create an SQL table
        final String SQL_CREATE_FAVORITE_MOVIES_TABLE =
                "CREATE TABLE " + FavoriteMovies.TABLE_NAME + " (" +
                        FavoriteMovies._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MovieColumns.COLUMN_MOVIE_ID + " LONG NOT NULL UNIQUE, " +
                        MovieColumns.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                        MovieColumns.COLUMN_MOVIE_POSTER + " TEXT NOT NULL, " +
                        MovieColumns.COLUMN_MOVIE_SYNOPSIS + " TEXT NOT NULL, " +
                        MovieColumns.COLUMN_MOVIE_USER_RATING + " FLOAT NOT NULL, " +
                        MovieColumns.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL " +
                        "); ";

        final String SQL_CREATE_MOVIES_TABLE =
                "CREATE TABLE " + RegularMovies.TABLE_NAME + " (" +
                        RegularMovies._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MovieColumns.COLUMN_MOVIE_ID + " LONG NOT NULL UNIQUE, " +
                        MovieColumns.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                        MovieColumns.COLUMN_MOVIE_POSTER + " TEXT NOT NULL, " +
                        MovieColumns.COLUMN_MOVIE_SYNOPSIS + " TEXT NOT NULL, " +
                        MovieColumns.COLUMN_MOVIE_USER_RATING + " FLOAT NOT NULL, " +
                        MovieColumns.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL " +
                        "); ";

        // Creating a table to hold movie data with the SQL command above
        db.execSQL(SQL_CREATE_FAVORITE_MOVIES_TABLE);
        db.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteMovies.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RegularMovies.TABLE_NAME);
        onCreate(db);
    }
}
