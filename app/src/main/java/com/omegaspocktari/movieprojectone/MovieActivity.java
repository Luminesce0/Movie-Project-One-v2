package com.omegaspocktari.movieprojectone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Credits...
 * http://www.vogella.com/tutorials/AndroidRecyclerView/article.html (Recycler View)...
 * https://developer.android.com/training/material/lists-cards.html#RVExamples
 *
 * Paragraph 3 - Attribution
 *  Use TMDb logo to identify use of the TMDb API
 *  "This product uses the TMDb API but is not endorsed or certified by TMDb."... Prominently?
 *  TMDb logo shall be less prominent than the logo or mark that primarily describes the application
 *      and the logo shall not imply and endorsement by TMDb
 *
 *
 */
public class MovieActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MovieFragment())
                    .commit();
        }
    }

    //TODO: Create a menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //TODO: make sure I'm working
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
        }
        return true;
    }
}
