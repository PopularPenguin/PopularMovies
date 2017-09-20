package com.popularpenguin.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class ListActivity extends AppCompatActivity {

    private MovieAdapter mAdapter;
    private RecyclerView mMovieList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mMovieList = (RecyclerView) findViewById(R.id.rv_movies);

        // TODO: Optional - Set the column variable to a different value depending on the screen
        // orientation

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2 /* columns */,
                LinearLayoutManager.VERTICAL, false /* reversed? */);
        mMovieList.setLayoutManager(layoutManager);
        mMovieList.setHasFixedSize(true);

        mAdapter = new MovieAdapter(this);
        mMovieList.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_list, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        switch (itemId) {

            case R.id.action_sort_popular:
                if (item.isChecked()) break;

                // TODO: Code for sorting by popularity
                Toast.makeText(this, "Sort by Popularity", Toast.LENGTH_SHORT).show();

                item.setChecked(true);
                break;

            case R.id.action_sort_rating:
                if (item.isChecked()) break;

                // TODO: Code for sorting by rating
                Toast.makeText(this, "Sort by Rating", Toast.LENGTH_SHORT).show();

                item.setChecked(true);
                break;

            default:
                throw new UnsupportedOperationException("Invalid menu id");
        }

        return super.onOptionsItemSelected(item);
    }
}
