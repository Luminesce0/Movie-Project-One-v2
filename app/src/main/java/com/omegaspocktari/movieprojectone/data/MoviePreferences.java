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

    public static boolean areNotificationsEnabled(Context context) {
        String displayNotificationsKey = context.getString(R.string.pref_enable_notifications_key);

        // Check to see if notifications should be displayed
        boolean shouldDisplayNotificationsByDefault = context
                .getResources()
                .getBoolean(R.bool.show_notification_by_default);

        // Default SharedPreferences to acceess user's preferences
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        // If a value is stored, extract it. If not, default will be used
        boolean shouldDisplayNotifications = sp
                .getBoolean(displayNotificationsKey, shouldDisplayNotificationsByDefault);

        return shouldDisplayNotifications;

    }

    /**
     * Method that access SharedPreferences.Editor and allocates the time of the
     * most recent notification
     */
    public static void saveLastNotificationTime(Context context, long timeOfNotification) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        String lastNotificationKey = context.getString(R.string.pref_last_notification);
        editor.putLong(lastNotificationKey, timeOfNotification);
        editor.apply();

    }

    public static long getElapsedTimeSinceLastNotification(Context context) {
        // Time that the last notification was sent
        long lastNotificationTimeMillis = 
                MoviePreferences.getLastNotificationTimeInMillis(context);

        // Return the time since the last notification
        return System.currentTimeMillis() - lastNotificationTimeMillis;
    }

    private static long getLastNotificationTimeInMillis(Context context) {
        // Get reference to the pref_last_notification key
        String lastNotificationKey = context.getString(R.string.pref_last_notification);

        // Get default shared preferences
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        // Get and return the long stored in lastNotificationKey. If it doesn't exist, get 0
        return sp.getLong(lastNotificationKey, 0);

    }
}
