package com.omegaspocktari.movieprojectone.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.omegaspocktari.movieprojectone.R;

/**
 * Created by ${Michael} on 4/13/2017.
 */

public class MoviePreferences {
    public static String getPreferredMovieSorting(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(
                context.getString(R.string.pref_sorting_key),
                context.getString(R.string.pref_sorting_popularity));
    }
}
