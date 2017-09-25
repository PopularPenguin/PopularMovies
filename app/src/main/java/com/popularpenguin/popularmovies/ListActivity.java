package com.popularpenguin.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
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

import java.util.ArrayList;

import static com.popularpenguin.popularmovies.NetworkUtils.getMovies;

@SuppressWarnings("FieldCanBeLocal")
public class ListActivity extends AppCompatActivity implements
        MovieAdapter.MovieAdapterOnClickHandler {

    private static final String TAG = ListActivity.class.getSimpleName();

    private static final String MOVIE_LIST_KEY = "movie_list";
    private static final String POPULAR_KEY = "popular";

    private final String INTENT_EXTRA_MOVIE = "movie";

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
            new GetMovies(mPopularIsSelected).execute();
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
                new GetMovies(true).execute();

                break;

            case R.id.action_sort_rating:
                item.setChecked(true);

                mPopularIsSelected = false;
                new GetMovies(false).execute();

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
                mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
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

    /** Async task for fetching the list of movies from the web API */
    private class GetMovies extends AsyncTask<String, Void, ArrayList<Movie>> {

        private final boolean isPopularSelected;

        public GetMovies(boolean isPopularSelected) {
            this.isPopularSelected = isPopularSelected;
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
            return getMovies(ListActivity.this, isPopularSelected);
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> result) {
            if (result == null || result.isEmpty()) {
                Toast.makeText(ListActivity.this, R.string.con_error, Toast.LENGTH_SHORT).show();
                return;
            }

            mMovieList = result;

            setUpRecyclerView();
        }
    }
}
