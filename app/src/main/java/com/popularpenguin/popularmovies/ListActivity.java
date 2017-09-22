package com.popularpenguin.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

public class ListActivity extends AppCompatActivity implements
        MovieAdapter.MovieAdapterOnClickHandler {

    private static final String TAG = ListActivity.class.getSimpleName();

    private static final String MOVIE_LIST_KEY = "movie_list";

    private final String INTENT_EXTRA_MOVIE = "movie";
    private final String INTENT_EXTRA_LIST = "movies";
    private final int REQUEST_CODE = 1;

    private MovieAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<Movie> mMovieList;

    boolean mPopularIsSelected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);

        mMovieList = new ArrayList<>();

        // Check if there is data in savedInstanceState,
        // if not check the network connection and download the movie data
        if (savedInstanceState != null) {
            Toast.makeText(this, "Getting saved data", Toast.LENGTH_SHORT).show();
            mMovieList = savedInstanceState.getParcelableArrayList(MOVIE_LIST_KEY);
            setUpRecyclerView();
        }
        else {
            // Check if there is an active network connection
            ConnectivityManager cm =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            boolean isConnected = info != null && info.isConnectedOrConnecting();

            if (isConnected) {
                Toast.makeText(this, "Downloading data", Toast.LENGTH_SHORT).show();
                new GetMovies(mPopularIsSelected).execute();
            }
            else {
                Toast.makeText(this, R.string.error_connectivity, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /** Save the entire movie array so you don't have to refetch it if the view is recreated */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MOVIE_LIST_KEY, mMovieList);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if (data.hasExtra(INTENT_EXTRA_LIST)) {
                Toast.makeText(this, "Activity returned", Toast.LENGTH_SHORT).show();
                mMovieList = data.getParcelableArrayListExtra(INTENT_EXTRA_LIST);
            }
        }
    }

    /** Create the menu */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_list, menu);

        return true;
    }

    /** Menu options to sort by popularity or rating, fatch new data when changing options */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        switch (itemId) {

            case R.id.action_sort_popular:
                if (item.isChecked()) break;

                Toast.makeText(this, "Sort by Popularity", Toast.LENGTH_SHORT).show();

                item.setChecked(true);

                mPopularIsSelected = true;
                new GetMovies(mPopularIsSelected).execute();

                break;

            case R.id.action_sort_rating:
                if (item.isChecked()) break;

                Toast.makeText(this, "Sort by Rating", Toast.LENGTH_SHORT).show();

                item.setChecked(true);

                mPopularIsSelected = false;
                new GetMovies(mPopularIsSelected).execute();

                break;

            default:
                throw new UnsupportedOperationException("Invalid menu id");
        }

        return super.onOptionsItemSelected(item);
    }

    /** Set up the RecyclerView, setting the LayoutManager to a grid if the orientation is
     * in portrait and a horizontal LinearLayout if the orientation is in landscape */
    private void setUpRecyclerView() {
        Log.d(TAG, "setUpRecyclerView");

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
                // TODO: Change to a grid layout?
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
        intent.putParcelableArrayListExtra(INTENT_EXTRA_LIST, mMovieList);

        startActivityForResult(intent, REQUEST_CODE);
    }


    /** Async task for fetching the list of movies from the web API */
    private class GetMovies extends AsyncTask<String, Void, ArrayList<Movie>> {

        private boolean isPopularSelected;

        public GetMovies(boolean isPopularSelected) {
            this.isPopularSelected = isPopularSelected;
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
            return getMovies(isPopularSelected);
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> result) {
            mMovieList = result;

            setUpRecyclerView();
        }
    }
}
