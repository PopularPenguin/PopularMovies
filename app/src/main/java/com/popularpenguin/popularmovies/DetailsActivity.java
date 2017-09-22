package com.popularpenguin.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import static com.popularpenguin.popularmovies.BuildConfig.MOVIE_API_KEY;

public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = DetailsActivity.class.getSimpleName();

    private static final String INTENT_EXTRA_MOVIE = "movie";

    private Movie mMovie;

    private TextView testText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        testText = (TextView) findViewById(R.id.tv_test);

        Intent intent = getIntent();

        // TODO: Remove this TextView once the layout is set up
        if (intent.hasExtra(INTENT_EXTRA_MOVIE)) {
            mMovie = intent.getParcelableExtra(INTENT_EXTRA_MOVIE);
            testText.append("Title: " + mMovie.getTitle());
            testText.append("\nDate: " + mMovie.getReleaseDate());
            testText.append("\nUser Average: " + mMovie.getAverage());
            testText.append("\nOverview: " + mMovie.getOverview());
        }

        // TODO: Add a ConstraintLayout
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }

        return true;
    }
}
