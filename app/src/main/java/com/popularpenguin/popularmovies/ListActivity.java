package com.popularpenguin.popularmovies;

import android.content.Intent;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.widget.Toast;

import com.popularpenguin.popularmovies.data.Movie;
import com.popularpenguin.popularmovies.utils.MovieAdapter;
import com.popularpenguin.popularmovies.utils.MovieLoader;
import com.popularpenguin.popularmovies.utils.NetworkUtils;

import java.util.ArrayList;

@SuppressWarnings("FieldCanBeLocal")
public class ListActivity extends AppCompatActivity implements
        MovieAdapter.MovieAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<ArrayList<Movie>> {

    private static final String TAG = ListActivity.class.getSimpleName();

    private static final String MOVIE_LIST_KEY = "movie_list";
    private static final String POPULAR_KEY = "popular";

    private final String INTENT_EXTRA_MOVIE = "movie";

    public static final int NETWORK_LOADER_ID = 1;
    public static final int FAVORITES_LOADER_ID = 2;

    private MovieAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<Movie> mMovieList;

    private boolean mPopularIsSelected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);

        mMovieList = new ArrayList<>();

        // Check if there is data in savedInstanceState, if not download the movie data
        if (savedInstanceState != null) {
            mMovieList = savedInstanceState.getParcelableArrayList(MOVIE_LIST_KEY);
            mPopularIsSelected = savedInstanceState.getBoolean(POPULAR_KEY);
            setUpRecyclerView();
        }
        else {
            // if there is no saved state, initialize the loader
            getSupportLoaderManager().initLoader(NETWORK_LOADER_ID, null, this);
        }
    }

    /** Save the entire movie array so you don't have to fetch it if the view is recreated
     * and save the position of the menu checked item */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MOVIE_LIST_KEY, mMovieList);
        outState.putBoolean(POPULAR_KEY, mPopularIsSelected);
        super.onSaveInstanceState(outState);
    }

    /** Create the menu */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_list, menu);

        // set the menu item checked
        // NOTE: If the menu order is changed, the indexes here will have to change too
        if (mPopularIsSelected) {
            menu.getItem(0).setChecked(true);
        }
        else {
            menu.getItem(1).setChecked(true);
        }

        return true;
    }

    /** Menu options to sort by popularity or rating, fetch new data when changing options */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!NetworkUtils.isConnected(this)) {
            Toast.makeText(this, R.string.con_error, Toast.LENGTH_SHORT).show();
            return false;
        }

        int itemId = item.getItemId();

        switch (itemId) {

            case R.id.action_sort_popular:
                item.setChecked(true);

                mPopularIsSelected = true;
                getSupportLoaderManager().restartLoader(NETWORK_LOADER_ID, null, this);

                break;

            case R.id.action_sort_rating:
                item.setChecked(true);

                mPopularIsSelected = false;
                getSupportLoaderManager().restartLoader(NETWORK_LOADER_ID, null, this);

                break;

            case R.id.action_sort_favorites:
                item.setChecked(true);

                // TODO: Implement the favorites menu item

                break;

            default:
                throw new UnsupportedOperationException("Invalid menu id");
        }

        return super.onOptionsItemSelected(item);
    }

    /** Set up the RecyclerView, setting the LayoutManager to a grid if the orientation is
     * in portrait and a horizontal LinearLayout if the orientation is in landscape */
    private void setUpRecyclerView() {
        mAdapter = new MovieAdapter(this, mMovieList, this);
        mRecyclerView.setAdapter(mAdapter);

        // Set up a different layout manager according to screen orientation
        int orientation = getWindowManager().getDefaultDisplay().getRotation();

        Log.d(TAG, "orientation = " + orientation);

        switch (orientation) {
            case Surface.ROTATION_0:
            case Surface.ROTATION_180:
                mLayoutManager = new GridLayoutManager(this, 2 /* columns */,
                        LinearLayoutManager.VERTICAL, false /* reversed? */);
                break;

            case Surface.ROTATION_90:
            case Surface.ROTATION_270:
                mLayoutManager = new GridLayoutManager(this, 1 /* columns */,
                        LinearLayoutManager.HORIZONTAL, false);
                break;

            default:
                mLayoutManager = new GridLayoutManager(this, 2 /* columns */,
                        LinearLayoutManager.VERTICAL, false /* reversed? */);
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
    }

    /** Start the DetailsActivity when a movie is clicked */
    @Override
    public void onClick(Movie movie) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(INTENT_EXTRA_MOVIE, movie);

        startActivity(intent);
    }

    /** Loader callbacks */
    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int id, final Bundle args) {
        switch (id) {
            case NETWORK_LOADER_ID:
                return new MovieLoader(this, mPopularIsSelected);

            case FAVORITES_LOADER_ID:
                // TODO: Implement and call the FavoritesLoader
                throw new UnsupportedOperationException("Loader not yet implemented");

            default:
                throw new UnsupportedOperationException("Invalid loader id");
        }
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> result) {
        if (result == null || result.isEmpty()) {
            // Error loading data from network
            if (loader.getId() == NETWORK_LOADER_ID) {
                Toast.makeText(ListActivity.this, R.string.con_error,
                        Toast.LENGTH_SHORT).show();
                return;
            }
            // Favorites database is empty
            else {
                Toast.makeText(ListActivity.this, R.string.fav_empty, Toast.LENGTH_SHORT)
                        .show();
                return;
            }
        }

        mMovieList = result;

        setUpRecyclerView();
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {
        // Not implemented
    }
}
