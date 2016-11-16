package com.omegaspocktari.movieprojectone;

import java.io.Serializable;

/**
 * Created by ${Michael} on 11/4/2016.
 *
 * External Sources Utilized:
 * http://prasanta-paul.blogspot.com/2010/06/android-parcelable-example.html - Parcelable
 *
 */
public class Movie implements Serializable {

    // General information on movie.
    private String mMovieTitle;
    private String mMoviePlot;
    private String mMovieUserRating;
    private String mMovieRelease;
    private String mMoviePoster;

    // General information on movie Parcelable variables.
//    private static final int NO_RESOURCE_PROVIDED = -1;
//
//    private int mMovieTitleParcel = NO_RESOURCE_PROVIDED;
//    private int mMoviePlotParcel = NO_RESOURCE_PROVIDED;
//    private int mMovieUserRatingParcel = NO_RESOURCE_PROVIDED;
//    private int mMovieReleaseParcel = NO_RESOURCE_PROVIDED;
//    private int mMoviePosterParcel = NO_RESOURCE_PROVIDED;

    public Movie(String mMovieTitle, String mMoviePlot, String mMovieUserRating, String mMovieRelease,
                 String mMoviePoster) {
        this.mMovieTitle = mMovieTitle;
        this.mMoviePlot = mMoviePlot;
        this.mMovieUserRating = mMovieUserRating;
        this.mMovieRelease = mMovieRelease;
        this.mMoviePoster = mMoviePoster;
    }

    public String getMoviePlot() {
        return mMoviePlot;
    }

    public Float getMovieUserRating() {
        return Float.valueOf(mMovieUserRating);
    }

    public String getMovieRelease() {
        return mMovieRelease;
    }

    public String getMoviePoster() {
        return mMoviePoster;
    }

    public String getMovieTitle() {
        return mMovieTitle;
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // ~~~~~ Here begins the parcelable data version of Movie...  ~~~~~
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
//        @Override
//        public Movie createFromParcel(Parcel in) {
//            return new Movie(in);
//        }
//
//        @Override
//        public Movie[] newArray(int size) {
//            return new Movie[size];
//        }
//    };

    /**
     * Defines the kind of object which is going to become Parcel'd.
     *
     * @return
     */
//    @Override
//    public int describeContents() {
//        return hashCode();
//    }

    /**
     * Actual object serialization/flattening to byte stream occurs here.
     * Each object must be individually parcel'd.
     *
     * @param parcel
     * @param i
     */
//    @Override
//    public void writeToParcel(Parcel parcel, int i) {
//        parcel.writeInt(mMovieTitleParcel);
//        parcel.writeInt(mMoviePlotParcel);
//        parcel.writeInt(mMovieUserRatingParcel);
//        parcel.writeInt(mMovieReleaseParcel);
//        parcel.writeInt(mMoviePosterParcel);
//    }
//
//    private Movie (Parcel in) {
//        mMovieTitleParcel = in.readInt();
//        mMoviePlotParcel = in.readInt();
//        mMovieUserRatingParcel = in.readInt();
//        mMovieReleaseParcel = in.readInt();
//        mMoviePosterParcel = in.readInt();
//    }
//
//    public int getmMovieTitleParcel() {
//        return mMovieTitleParcel;
//    }
//
//    public int getmMoviePlotParcel() {
//        return mMoviePlotParcel;
//    }
//
//    public int getmMovieUserRatingParcel() {
//        return mMovieUserRatingParcel;
//    }
//
//    public int getmMovieReleaseParcel() {
//        return mMovieReleaseParcel;
//    }
//
//    public int getmMoviePosterParcel() {
//        return mMoviePosterParcel;
//    }
}
