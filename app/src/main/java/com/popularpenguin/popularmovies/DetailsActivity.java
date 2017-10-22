package com.popularpenguin.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.popularpenguin.popularmovies.data.Movie;
import com.popularpenguin.popularmovies.utils.DetailsAdapter;
import com.popularpenguin.popularmovies.utils.ReviewsLoader;
import com.popularpenguin.popularmovies.utils.TrailersLoader;
import com.squareup.picasso.Picasso;

import com.popularpenguin.popularmovies.data.FavoritesContract.FavoritesEntry;

import java.util.ArrayList;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class DetailsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<ArrayList<String[]>>,
        DetailsAdapter.DetailsAdapterOnClickHandler {

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

    private DetailsAdapter mDetailsAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private Movie mMovie;

    private TextView mTitleText;
    private TextView mReleaseDateText;
    private TextView mRating;
    private TextView mOverview;
    private ImageView mPosterImage;
    private Button mFavoritesButton;

    // Trailer views
    private ImageView mPlayImage;
    private TextView mTrailerText;

    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_details);

        // TODO: Data binding?
        mTitleText = (TextView) findViewById(R.id.tv_title);
        mReleaseDateText = (TextView) findViewById(R.id.tv_release_date);
        mRating = (TextView) findViewById(R.id.tv_rating);
        mOverview = (TextView) findViewById(R.id.tv_overview);
        mPosterImage = (ImageView) findViewById(R.id.iv_details_poster);
        mFavoritesButton = (Button) findViewById(R.id.btn_favorites);

        // TODO: Use a URI builder instead

        Intent intent = getIntent();

        if (intent.hasExtra(INTENT_EXTRA_MOVIE)) {
            mMovie = intent.getParcelableExtra(INTENT_EXTRA_MOVIE);

            mTitleText.setText(mMovie.getTitle());
            mReleaseDateText.setText(mMovie.getReleaseDate());
            mRating.setText(mMovie.getAverage());
            mOverview.setText(mMovie.getOverview());

            setFavoritesButton(); // set the color and text of the favorites button

            Picasso.with(this).load(mMovie.getPosterPath()).into(mPosterImage);

            getSupportLoaderManager().initLoader(TRAILER_LOADER_ID, null, this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_details, menu);

        return true;
    }

    /** Stop up button from creating a new instance of the parent activity so it doesn't
     * fetch the data again */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.action_share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "Movie to share");

                startActivity(Intent.createChooser(intent, mMovie.getTitle()));

                break;

            case android.R.id.home:
                super.onBackPressed();
                break;

            default:
                throw new UnsupportedOperationException("Invalid menu id");
        }
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

        // check if the data is empty
        if (data.isEmpty()) return;

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

    /** Set the color, text, and listener of the favorites button */
    private void setFavoritesButton() {
        if (!mMovie.isFavorite()) {
            mFavoritesButton.setText(R.string.btn_add_favorite);
        }
        else {
            mFavoritesButton.setText(R.string.btn_remove_favorite);
        }

        mFavoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mMovie.isFavorite()) {
                    mMovie.setFavorite(true);

                    ContentValues cv = new ContentValues();

                    cv.put(FavoritesEntry.COLUMN_MOVIE_ID, mMovie.getId());
                    cv.put(FavoritesEntry.COLUMN_MOVIE_TITLE, mMovie.getTitle());
                    cv.put(FavoritesEntry.COLUMN_MOVIE_OVERVIEW, mMovie.getOverview());
                    cv.put(FavoritesEntry.COLUMN_MOVIE_POSTER_PATH, mMovie.getPosterPath());
                    cv.put(FavoritesEntry.COLUMN_MOVIE_RATING, mMovie.getAverage());
                    cv.put(FavoritesEntry.COLUMN_MOVIE_RELEASE_DATE, mMovie.getReleaseDate());

                    getContentResolver().insert(FavoritesEntry.CONTENT_URI, cv);

                    getContentResolver().notifyChange(FavoritesEntry.CONTENT_URI, null);

                    mFavoritesButton.setText(R.string.btn_remove_favorite);

                    Toast.makeText(DetailsActivity.this, R.string.favorite_added,
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    mMovie.setFavorite(false);

                    String idString = String.valueOf(mMovie.getId());

                    Uri uri = FavoritesEntry.CONTENT_URI.buildUpon().appendPath(idString).build();

                    getContentResolver().delete(uri, FavoritesEntry.COLUMN_MOVIE_ID + "=?",
                            new String[] { idString });

                    getContentResolver().notifyChange(FavoritesEntry.CONTENT_URI, null);

                    mFavoritesButton.setText(R.string.btn_add_favorite);

                    Toast.makeText(DetailsActivity.this, R.string.favorite_removed,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
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
        mDetailsAdapter = new DetailsAdapter(this, mMovie, this);
        mLayoutManager = new LinearLayoutManager(this);

        mRecyclerView.setAdapter(mDetailsAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
    }


    @Override
    public void onClickTrailer(int position, int itemType) {
        switch (itemType) {
            case DetailsAdapter.TRAILER_VIEW:
                playTrailer(position);
                break;

            case DetailsAdapter.REVIEW_VIEW:
                openReviewInBrowser(position);
        }
    }
}
