package gianlucadp.com.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import gianlucadp.com.popularmovies.model.Movie;
import gianlucadp.com.popularmovies.utils.Constants;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private Context context;
    private List<Movie> movies;


    public MovieAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    @Override
    public int getItemCount() {
        if (movies == null) {
            return 20;
        } else {
            return movies.size();
        }
    }


    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.recyclerview_movie_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);

        return new MovieViewHolder(view);
    }


    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.loadImage(position);
    }


    public class MovieViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageViewPoster;

        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Context context = view.getContext();
                Intent intent = new Intent(context, MovieDetailActivity.class);
                intent.putExtra(Constants.MOVIE_INTENT, movies.get(getAdapterPosition()));

                context.startActivity(intent);

            }
        };

        public MovieViewHolder(View view) {
            super(view);

            mImageViewPoster = view.findViewById(R.id.iv_movie_item);
            view.setOnClickListener(mOnClickListener);

        }

        public void loadImage(int position) {
            if (movies != null) {
                mImageViewPoster.setVisibility(View.VISIBLE);
                if (movies.get(position).getPosterImg() != null && movies.get(position).getPosterImg().length > 0) {
                    Bitmap pic = BitmapFactory.decodeByteArray(movies.get(position).getPosterImg(), 0, movies.get(position).getPosterImg().length);
                    mImageViewPoster.setImageBitmap(pic);
                } else {
                    Picasso.with(context).load(Constants.IMG_BASE_PATH + movies.get(position).getPosterPath()).placeholder(R.drawable.img_loading).error(R.drawable.img_missing).into(mImageViewPoster);
                }
            }
        }

    }
}
