package com.popularpenguin.popularmovies.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.popularpenguin.popularmovies.R;
import com.popularpenguin.popularmovies.data.Movie;

/** This class is responsible for the list of trailers and reviews in DetailsActivity */
public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.DetailsViewHolder> {

    private static final String TAG = DetailsAdapter.class.getSimpleName();

    public static final int TRAILER_VIEW = 0;
    public static final int REVIEW_VIEW = 1;

    private final int TRAILER_COUNT;
    private final int REVIEW_COUNT;

    private final DetailsAdapterOnClickHandler mClickHandler;

    public interface DetailsAdapterOnClickHandler {
        void onClickTrailer(int position, int itemType);
    }

    private final Context ctx;
    private final Movie mMovie;

    public DetailsAdapter(Context ctx, Movie movie, DetailsAdapterOnClickHandler handler) {

        this.ctx = ctx;
        mMovie = movie;
        mClickHandler = handler;

        TRAILER_COUNT = movie.getTrailerKeys().length;
        REVIEW_COUNT = movie.getReviewAuthors().length;
    }

    @Override
    public DetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        final int layout;

        switch (viewType) {
            case TRAILER_VIEW:
                layout = R.layout.trailer_item;
                break;

            case REVIEW_VIEW:
                layout = R.layout.review_item;
                break;

            default:
                throw new UnsupportedOperationException("Invalid view type");
        }

        View view = inflater.inflate(layout, parent, false);

        return new DetailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DetailsViewHolder holder, int position) {
        holder.bind(mMovie);
    }

    @Override
    public int getItemCount() {
        return TRAILER_COUNT + REVIEW_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < TRAILER_COUNT) {
            return TRAILER_VIEW;
        }
        else {
            return REVIEW_VIEW;
        }
    }

    class DetailsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final View itemView;

        int itemPosition;
        int itemType;

        public DetailsViewHolder(View itemView) {
            super(itemView);

            this.itemView = itemView;
            itemView.setOnClickListener(this);
        }

        void bind(Movie movie) {
            switch (getItemViewType()) {
                case TRAILER_VIEW:
                    itemType = TRAILER_VIEW;
                    itemPosition = getAdapterPosition();
                    setTrailer(movie);
                    break;

                case REVIEW_VIEW:
                    itemType = REVIEW_VIEW;
                    itemPosition = getAdapterPosition() - TRAILER_COUNT;
                    setReview(movie);
                    break;

                default:
                    throw new UnsupportedOperationException("Invalid view type");
            }
        }

        void setTrailer(Movie movie) {
            TextView trailerTitle = (TextView) itemView.findViewById(R.id.tv_trailer_item);
            trailerTitle.setText(movie.getTrailerNames()[itemPosition]);
        }

        void setReview(Movie movie) {
            TextView authorText = (TextView) itemView.findViewById(R.id.tv_review_author);
            authorText.setText(movie.getReviewAuthors()[itemPosition]);

            TextView contextText = (TextView) itemView.findViewById(R.id.tv_review_content);
            // Set the text up to a certain length, user can tap the review to see more
            String fullText = movie.getReviewContent()[itemPosition];
            int maxLength = 500;
            int length = (fullText.length() < maxLength) ? fullText.length() : maxLength;
            String partialText = fullText.substring(0, length) + "...";
            contextText.setText(partialText);
        }

        @Override
        public void onClick(View v) {

            mClickHandler.onClickTrailer(itemPosition, itemType);
        }
    }
}
