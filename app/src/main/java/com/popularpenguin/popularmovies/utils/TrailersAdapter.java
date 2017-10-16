package com.popularpenguin.popularmovies.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.popularpenguin.popularmovies.R;
import com.popularpenguin.popularmovies.data.Movie;

// TODO: Clean up
// TODO: Split into 2? Or try some other implementation?

/** This class is responsible for the list of trailers and reviews in DetailsActivity */
public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailerViewHolder> {

    private static final String TAG = TrailersAdapter.class.getSimpleName();

    private final DetailsAdapterOnClickHandler mClickHandler;

    public interface DetailsAdapterOnClickHandler {
        void onClickTrailer(int position);
    }

    private final Context ctx;
    private final Movie mMovie;

    public TrailersAdapter(Context ctx, Movie movie, DetailsAdapterOnClickHandler handler) {

        this.ctx = ctx;
        mMovie = movie;
        mClickHandler = handler;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ctx);

        View view = inflater.inflate(R.layout.trailer_item, parent, false);

        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        holder.bind(mMovie);
    }

    @Override
    public int getItemCount() {
        return mMovie.getTrailerKeys().length;
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final View itemView;

        public TrailerViewHolder(View itemView) {
            super(itemView);

            this.itemView = itemView;
            itemView.setOnClickListener(this);
        }

        void bind(Movie movie) {
            int position = getAdapterPosition();
            TextView trailerTitle = (TextView) itemView.findViewById(R.id.tv_trailer_item);
            trailerTitle.setText(movie.getTrailerNames()[position]);
        }

/*
        void bindReview(Movie movie) {
            int position = getAdapterPosition() - REVIEW_START_POSITION;

            TextView authorText = (TextView) itemView.findViewById(R.id.tv_review_author);
            authorText.setText(movie.getReviewAuthors()[position]);

            TextView contextText = (TextView) itemView.findViewById(R.id.tv_review_content);
            contextText.setText(movie.getReviewContent()[position]);
        } */

        @Override
        public void onClick(View v) {

            mClickHandler.onClickTrailer(getAdapterPosition());

            /*
            Log.d(TAG, "onClick");
            int position = getAdapterPosition();
            if (position < REVIEW_START_POSITION) {
                mClickHandler.onClick(position, TRAILER_VIEW);
            }
            else {
                int reviewPosition = position - mMovie.getTrailerKeys().length;
                mClickHandler.onClick(reviewPosition, REVIEW_VIEW);
            } */
        }
    }
}
