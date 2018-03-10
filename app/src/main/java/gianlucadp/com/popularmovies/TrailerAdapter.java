package gianlucadp.com.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import gianlucadp.com.popularmovies.utils.Constants;


public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {
    private Context context;
    private List<String> trailersIds;


    public TrailerAdapter(Context context, List<String> trailersIds) {
        this.context = context;
        this.trailersIds = trailersIds;
    }

    public void setTrailersIds(List<String> trailersIds) {
        this.trailersIds = trailersIds;
    }

    @Override
    public int getItemCount() {
        if (trailersIds != null) {
            return trailersIds.size();
        } else return 0;
    }


    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.recyclerview_trailer_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);

        return new TrailerViewHolder(view);
    }


    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        holder.loadReview(position);
    }


    public class TrailerViewHolder extends RecyclerView.ViewHolder {
        private ImageView mTrailerFrame;

        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailersIds.get(getAdapterPosition())));
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(Constants.YOUTUBE_URL + trailersIds.get(getAdapterPosition())));
                try {
                    context.startActivity(appIntent);
                } catch (ActivityNotFoundException ex) {
                    context.startActivity(webIntent);
                }

            }
        };

        public TrailerViewHolder(View view) {
            super(view);
            mTrailerFrame = view.findViewById(R.id.im_movie_trailer);
            view.setOnClickListener(mOnClickListener);
        }

        public void loadReview(int position) {
            Picasso.with(context).load(Constants.YOUTUBE_FRAME_ULR + trailersIds.get(position) + Constants.YOUTUBE_FRAME_QUALITY).placeholder(R.drawable.img_loading_background).error(R.drawable.img_missing_background).into(mTrailerFrame);


        }

    }

}
