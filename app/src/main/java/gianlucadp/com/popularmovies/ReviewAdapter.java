package gianlucadp.com.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import gianlucadp.com.popularmovies.model.Review;


public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private List<Review> reviews;


    public ReviewAdapter(List<Review> reviews) {
        this.reviews = reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    @Override
    public int getItemCount() {
        if (reviews!=null) {
            return reviews.size();
        }
        else return 0;
        }


    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.recyclerview_review_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);

        return new ReviewViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        holder.loadReview(position);
    }


    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        private TextView mAuthor;
        private TextView mContent;


        public ReviewViewHolder(View view) {
            super(view);
            mAuthor = view.findViewById(R.id.tv_review_item_author);
            mContent = view.findViewById(R.id.tv_review_item_content);
        }

        public void loadReview(int position) {
            Review review = reviews.get(position);
            mAuthor.setText(review.getAuthor());
            mContent.setText(review.getContent());

        }

    }

}
