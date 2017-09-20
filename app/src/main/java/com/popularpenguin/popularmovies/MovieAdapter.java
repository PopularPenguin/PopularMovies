package com.popularpenguin.popularmovies;

import android.content.Context;
import android.graphics.Movie;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder>{

    private static final String TAG = MovieAdapter.class.getSimpleName();

    private Context ctx;
    private int mViewCount; // number of total views created

    public MovieAdapter(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mViewCount++; // added another view

        return null;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public MovieViewHolder(View itemView) {
            super(itemView);

            // TODO: Set image id findViewById here
            // ...

            itemView.setOnClickListener(this);
        }

        void bind(int listIndex) {
            // TODO: Set image for the ViewHolder here
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(ctx, "Go to detail activity", Toast.LENGTH_SHORT).show();
        }
    }
}
