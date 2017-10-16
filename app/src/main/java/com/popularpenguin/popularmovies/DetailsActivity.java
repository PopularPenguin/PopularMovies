package com.popularpenguin.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.popularpenguin.popularmovies.data.Movie;
import com.popularpenguin.popularmovies.utils.ReviewsAdapter;
import com.popularpenguin.popularmovies.utils.TrailersAdapter;
import com.popularpenguin.popularmovies.utils.ReviewsLoader;
import com.popularpenguin.popularmovies.utils.TrailersLoader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class DetailsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<ArrayList<String[]>>,
        TrailersAdapter.DetailsAdapterOnClickHandler,
        ReviewsAdapter.ReviewsAdapterOnClickHandler {

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

    private TrailersAdapter mTrailersAdapter;
    private ReviewsAdapter mReviewsAdapter;
    private RecyclerView mTrailersRecyclerView;
    private RecyclerView mReviewsRecyclerView;
    private RecyclerView.LayoutManager mTrailersLayoutManager;
    private RecyclerView.LayoutManager mReviewsLayoutManager;

    private Movie mMovie;

    private TextView mTitleText;
    private TextView mReleaseDateText;
    private TextView mRating;
    private TextView mOverview;
    private ImageView mPosterImage;
    private Button mFavoritesButton;

    // Trailer views
    private ImageView mPlayImage;
    // TODO: Remove this when you use the view holders
    private TextView mTrailerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mTrailersRecyclerView = (RecyclerView) findViewById(R.id.rv_trailers);
        mReviewsRecyclerView = (RecyclerView) findViewById(R.id.rv_reviews);

        // TODO: Data binding?
        mTitleText = (TextView) findViewById(R.id.tv_title);
        mReleaseDateText = (TextView) findViewById(R.id.tv_release_date);
        mRating = (TextView) findViewById(R.id.tv_rating);
        mOverview = (TextView) findViewById(R.id.tv_overview);
        mPosterImage = (ImageView) findViewById(R.id.iv_details_poster);
        mFavoritesButton = (Button) findViewById(R.id.btn_favorites);

        // TODO: Add db functionality for button
        mFavoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DetailsActivity.this, "Favorites clicked", Toast.LENGTH_SHORT).show();
            }
        });

        // TODO: Use a URI builder instead

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
                throw new UnsupportedOperationException("Invalid loader id");
        }
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<String[]>> loader, ArrayList<String[]> data) {
        switch (loader.getId()) {
            case TRAILER_LOADER_ID:
                mMovie.setTrailerKeys(data.get(TRAILER_KEYS_INDEX));
                mMovie.setTrailerNames(data.get(TRAILER_NAMES_INDEX));

                getSupportLoaderManager().initLoader(REVIEW_LOADER_ID, null, this);

                break;

            case REVIEW_LOADER_ID:
                mMovie.setReviewAuthors(data.get(REVIEW_AUTHORS_INDEX));
                mMovie.setReviewContent(data.get(REVIEW_CONTENT_INDEX));
                mMovie.setReviewUrls(data.get(REVIEW_URLS_INDEX));

                //Toast.makeText(this, String.valueOf(mMovie.getReviewAuthors().length), Toast.LENGTH_SHORT).show();

                setUpRecyclerView();

                break;

            default:
                throw new UnsupportedOperationException("Invalid loader id in onLoadFinished()");
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<String[]>> loader) {
        // Not implemented
    }

    private void playTrailer(int position) {
        String key = mMovie.getTrailerKeys()[position];

        if (key == null || key.equals("")) return;

        String uriString = YOUTUBE_BASE_URL + key;

        Intent playVideoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriString));
        if (playVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(playVideoIntent);
        }
    }

    private void openReviewInBrowser(int position) {
        String url = mMovie.getReviewUrls()[position];

        if (url == null || url.equals("")) return;

        Intent openReviewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        if (openReviewIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(openReviewIntent);
        }
    }

    /** Only call this after the data is loaded! */
    private void setUpRecyclerView() {
        mTrailersAdapter = new TrailersAdapter(this, mMovie, this);
        mReviewsAdapter = new ReviewsAdapter(this, mMovie, this);
        mTrailersLayoutManager = new LinearLayoutManager(this);
        mReviewsLayoutManager = new LinearLayoutManager(this);

        mTrailersRecyclerView.setAdapter(mTrailersAdapter);
        mTrailersRecyclerView.setLayoutManager(mTrailersLayoutManager);
        mTrailersRecyclerView.setHasFixedSize(true);

        mReviewsRecyclerView.setAdapter(mReviewsAdapter);
        mReviewsRecyclerView.setLayoutManager(mReviewsLayoutManager);
        mReviewsRecyclerView.setHasFixedSize(true);
    }

    // TODO: Fix listener to work!
    @Override
    public void onClickTrailer(int position) { playTrailer(position); }

    @Override
    public void onClickReview(int position) { openReviewInBrowser(position); }
}
