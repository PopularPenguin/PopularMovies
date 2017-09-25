package com.popularpenguin.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = DetailsActivity.class.getSimpleName();

    private static final String INTENT_EXTRA_MOVIE = "movie";

    private Movie mMovie;

    private TextView mTitleText;
    private TextView mReleaseDateText;
    private TextView mRating;
    private TextView mOverview;
    private ImageView mPosterImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mTitleText = (TextView) findViewById(R.id.tv_title);
        mReleaseDateText = (TextView) findViewById(R.id.tv_release_date);
        mRating = (TextView) findViewById(R.id.tv_rating);
        mOverview = (TextView) findViewById(R.id.tv_overview);
        mPosterImage = (ImageView) findViewById(R.id.iv_details_poster);

        Intent intent = getIntent();

        if (intent.hasExtra(INTENT_EXTRA_MOVIE)) {
            mMovie = intent.getParcelableExtra(INTENT_EXTRA_MOVIE);

            mTitleText.setText(mMovie.getTitle());
            mReleaseDateText.setText(mMovie.getReleaseDate());
            mRating.setText(mMovie.getAverage());
            mOverview.setText(mMovie.getOverview());

            Picasso.with(this).load(mMovie.getPosterPath()).into(mPosterImage);
        }
    }

    /** Stop up button from creating a new instance of the parent activity so it doesn't
     * fetch the data again */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }

        return true;
    }
}
