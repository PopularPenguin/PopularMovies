package com.popularpenguin.popularmovies.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.popularpenguin.popularmovies.R;
import com.popularpenguin.popularmovies.data.Movie;


public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {

    private static final String TAG = ReviewsAdapter.class.getSimpleName();

    private final ReviewsAdapterOnClickHandler mClickHandler;

    public interface ReviewsAdapterOnClickHandler {
        void onClickReview(int position);
    }

    private final Context ctx;
    private final Movie mMovie;

    public ReviewsAdapter(Context ctx, Movie movie, ReviewsAdapterOnClickHandler handler) {
        this.ctx = ctx;
        mMovie = movie;
        mClickHandler = handler;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ctx);

        View view = inflater.inflate(R.layout.review_item, parent, false);

        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        holder.bind(mMovie);
    }

    @Override
    public int getItemCount() {
        return mMovie.getReviewAuthors().length;
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final View itemView;

        public ReviewViewHolder(View itemView) {
            super(itemView);

            this.itemView = itemView;
            itemView.setOnClickListener(this);
        }

        void bind(Movie movie) {
            int position = getAdapterPosition();

            TextView authorText = (TextView) itemView.findViewById(R.id.tv_review_author);
            authorText.setText(movie.getReviewAuthors()[position]);

            TextView contextText = (TextView) itemView.findViewById(R.id.tv_review_content);
            // Set the text up to a certain length, user can tap the review to see more
            String fullText = movie.getReviewContent()[position];
            int length = (fullText.length() < 500) ? fullText.length() : 500;
            String partialText = fullText.substring(0, length) + "...\n" +
                    ctx.getResources().getString(R.string.review_more_content);
            contextText.setText(partialText);
        }

        @Override
        public void onClick(View v) {
            mClickHandler.onClickReview(getAdapterPosition());
        }
    }
}
