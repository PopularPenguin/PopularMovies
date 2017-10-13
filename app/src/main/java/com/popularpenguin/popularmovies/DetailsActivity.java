package com.popularpenguin.popularmovies;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.popularpenguin.popularmovies.data.Movie;
import com.popularpenguin.popularmovies.utils.ReviewsLoader;
import com.popularpenguin.popularmovies.utils.TrailersLoader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class DetailsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<ArrayList<String[]>>,
        View.OnClickListener {

    private static final String TAG = DetailsActivity.class.getSimpleName();

    private static final String INTENT_EXTRA_MOVIE = "movie";

    private static final int TRAILER_LOADER_ID = 0;
    private static final int REVIEW_LOADER_ID = 1;

    private static final int TRAILER_KEYS_INDEX = 0;
    private static final int TRAILER_NAMES_INDEX = 1;

    private static final int REVIEW_AUTHORS_INDEX = 0;
    private static final int REVIEW_CONTENT_INDEX = 1;
    private static final int REVIEW_URLS_INDEX = 2;

    private static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";

    private Movie mMovie;

    private TextView mTitleText;
    private TextView mReleaseDateText;
    private TextView mRating;
    private TextView mOverview;
    private ImageView mPosterImage;

    // Trailer views
    private ImageView mPlayImage;
    // TODO: Remove this when you use the view holders
    private TextView mTrailerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // TODO: Data binding?
        mTitleText = (TextView) findViewById(R.id.tv_title);
        mReleaseDateText = (TextView) findViewById(R.id.tv_release_date);
        mRating = (TextView) findViewById(R.id.tv_rating);
        mOverview = (TextView) findViewById(R.id.tv_overview);
        mPosterImage = (ImageView) findViewById(R.id.iv_details_poster);

        mPlayImage = (ImageView) findViewById(R.id.iv_play_trailer);
        mTrailerText = (TextView) findViewById(R.id.tv_trailer_1);

        // TODO: Use a URI builder instead
        // TODO: Make a RecyclerView for trailers and pass in the index of the view to the key index
        mPlayImage.setOnClickListener(this);
        mTrailerText.setOnClickListener(this);

        Intent intent = getIntent();

        if (intent.hasExtra(INTENT_EXTRA_MOVIE)) {
            mMovie = intent.getParcelableExtra(INTENT_EXTRA_MOVIE);

            mTitleText.setText(mMovie.getTitle());
            mReleaseDateText.setText(mMovie.getReleaseDate());
            mRating.setText(mMovie.getAverage());
            mOverview.setText(mMovie.getOverview());

            Picasso.with(this).load(mMovie.getPosterPath()).into(mPosterImage);

            getSupportLoaderManager().initLoader(TRAILER_LOADER_ID, null, this);
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

    /** Trailer loader callbacks */
    @Override
    public Loader<ArrayList<String[]>> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case TRAILER_LOADER_ID:
                return new TrailersLoader(this, mMovie.getId());

            case REVIEW_LOADER_ID:
                return new ReviewsLoader(this, mMovie.getId());

            default:
                throw new IllegalArgumentException("Invalid loader id");
        }
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<String[]>> loader, ArrayList<String[]> data) {
        switch (loader.getId()) {
            case TRAILER_LOADER_ID:
                mMovie.setTrailerKeys(data.get(TRAILER_KEYS_INDEX));
                mMovie.setTrailerNames(data.get(TRAILER_NAMES_INDEX));

                // TODO: Remove this when the RecyclerView is set up
                mTrailerText.setText(mMovie.getTrailerNames()[0]);

                break;

            case REVIEW_LOADER_ID:
                mMovie.setReviewAuthors(data.get(REVIEW_AUTHORS_INDEX));
                mMovie.setReviewContent(data.get(REVIEW_CONTENT_INDEX));
                mMovie.setReviewUrls(data.get(REVIEW_URLS_INDEX));
                break;

            default:
                throw new IllegalArgumentException("Invalid loader id in onLoadFinished()");
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<String[]>> loader) {
        // Not implemented
    }

    // TODO: Remove the playtrailer part when the RecyclerView is set up
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_play_trailer:
            case R.id.tv_trailer_1:
                playTrailer();

                break;

            default:
                throw new UnsupportedOperationException("Invalid view id");
        }
    }

    private void playTrailer() {
        if (mMovie.getTrailerKeys() == null) {
            Toast.makeText(DetailsActivity.this, "Trailer data is null", Toast.LENGTH_SHORT)
                    .show();

            return;
        }
        String key = mMovie.getTrailerKeys()[0];

        if (key == null || key.equals("")) return;

        String uriString = YOUTUBE_BASE_URL + key;

        Intent playVideoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriString));
        if (playVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(playVideoIntent);
        }
    }
}
