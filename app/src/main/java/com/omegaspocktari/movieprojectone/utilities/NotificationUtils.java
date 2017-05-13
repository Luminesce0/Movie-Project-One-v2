package com.omegaspocktari.movieprojectone.utilities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;

import com.omegaspocktari.movieprojectone.MovieFragment;
import com.omegaspocktari.movieprojectone.R;
import com.omegaspocktari.movieprojectone.data.MoviePreferences;

/**
 * Created by ${Michael} on 5/10/2017.
 */

public class NotificationUtils {

    // Identify the notification for updates/canceling
    private static final int MOVIE_NOTIFICATION_ID = 0;


    public static void notifyUserOfMovieUpdate(Context context) {

        // Strings for the notification
        String notificationTitle = context.getString(R.string.app_name);
        String notificationText = context.getString(R.string.notification_text);

        // Build the notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                //.setSmallIcon(smallIcon)
                //.setLargeIcon(largeIcon)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setAutoCancel(true);

        // Create intent to open up our app
        Intent movieIntent = new Intent(context, MovieFragment.class);

        // Create pending intent
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addNextIntentWithParentStack(movieIntent);
        PendingIntent resultPendingIntent = taskStackBuilder.
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Set pending intent for the notification
        notificationBuilder.setContentIntent(resultPendingIntent);

        // Use notification manager to notify the user
        NotificationManager notificationmanager = (NotificationManager)
                context.getSystemService(context.NOTIFICATION_SERVICE);

        // Notify the user
        notificationmanager.notify(MOVIE_NOTIFICATION_ID, notificationBuilder.build());

        // Save the time at which the user was last notify/updated
        MoviePreferences.saveLastNotificationTime(context, System.currentTimeMillis());
    }


}
