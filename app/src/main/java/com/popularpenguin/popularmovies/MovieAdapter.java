package com.popularpenguin.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private static final String TAG = MovieAdapter.class.getSimpleName();

    private final MovieAdapterOnClickHandler mClickHandler;

    public interface MovieAdapterOnClickHandler {
        void onClick(Movie m);
    }

    private final Context ctx;
    private final ArrayList<Movie> mMovieList;

    public MovieAdapter(Context ctx, ArrayList<Movie> movieList,
                        MovieAdapterOnClickHandler handler) {

        this.ctx = ctx;
        mMovieList = movieList;
        mClickHandler = handler;
    }

    @Override
    public MovieAdapter.MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        int layout = R.layout.movie_item;
        LayoutInflater inflater = LayoutInflater.from(ctx);

        View view = inflater.inflate(layout, parent, false);

        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Movie movie = mMovieList.get(position);

        holder.bind(movie.getPosterPath());
    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }

    /** The view holder to hold a movie poster */
    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView imageView; // the image for the movie poster

        public MovieViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.iv_movie);

            itemView.setOnClickListener(this);
        }

        void bind(String posterPath) {
            // Set image for the ViewHolder here
            Picasso.with(ctx).load(posterPath).into(imageView);
        }

        @Override
        public void onClick(View v) {
            mClickHandler.onClick(mMovieList.get(getAdapterPosition()));
        }
    }
}
