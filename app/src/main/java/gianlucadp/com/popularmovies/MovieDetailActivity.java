package gianlucadp.com.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import gianlucadp.com.popularmovies.data.MovieContract;
import gianlucadp.com.popularmovies.data.MovieDbHelper;
import gianlucadp.com.popularmovies.model.Movie;
import gianlucadp.com.popularmovies.model.Review;
import gianlucadp.com.popularmovies.utils.Constants;
import gianlucadp.com.popularmovies.utils.JsonUtils;
import gianlucadp.com.popularmovies.utils.NetworkUtils;

public class MovieDetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<String> {
    private static final int REVIEWS_LIST_LOADER = 34;
    private static final int TRAILERS_LIST_LOADER = 35;
    private static final String SEARCH_QUERY_URL_REVIEWS = "query_reviews";
    private static final String SEARCH_QUERY_URL_TRAILERS = "query_trailers";
    private static final String REVIEW_LIST_STATE = "REVIEW_LIST";
    private static final String TRAILER_LIST_STATE = "TRAILER_LIST";
    private ImageView mBackGround;
    private Bitmap mBackGroundImage;
    private TextView mTitle;
    private Bitmap mPosterImage;
    private ImageView mPoster;
    private TextView mYear;
    private TextView mScore;
    private TextView mOverView;
    private List<Review> mReviewsList;
    private List<String> mTrailersList;
    private Button mFavorite;
    private int mMovieId;
    private RecyclerView mReviewsRecyclerView;
    private ReviewAdapter mReviewsAdapter;
    private RecyclerView.LayoutManager mLayoutManagerReviews;
    private TrailerAdapter mTrailersAdapter;
    private RecyclerView mTrailersRecyclerView;
    private RecyclerView.LayoutManager mLayoutManagerTrailers;
    private boolean isMovieFavorite;
    private Uri movieUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getSupportActionBar().hide();
        setContentView(R.layout.activity_movie_detail);
        Intent intent = getIntent();
        final Movie movie = intent.getParcelableExtra(Constants.MOVIE_INTENT);
        if (savedInstanceState != null) {
            mReviewsList = savedInstanceState.getParcelableArrayList(REVIEW_LIST_STATE);
            mTrailersList = savedInstanceState.getStringArrayList(TRAILER_LIST_STATE);
        }

        mBackGround = findViewById(R.id.im_movie_background);
        mTitle = findViewById(R.id.tv_movie_title);
        mPoster = findViewById(R.id.im_movie_poster);
        mYear = findViewById(R.id.tv_movie_year);
        mScore = findViewById(R.id.tv_movie_score);
        mOverView = findViewById(R.id.tv_movie_overview);
        mFavorite = findViewById(R.id.bt_favorite);
        mTitle.setText(movie.getOriginalTitle());
        mMovieId = movie.getMovieId();

        String stringId = Integer.toString(mMovieId);
        movieUri = MovieContract.MovieEntry.CONTENT_URI;
        movieUri = movieUri.buildUpon().appendPath(stringId).build();

        Cursor movieSearch = getContentResolver().query(movieUri, null, null, null, null);
        if (movieSearch.getCount() > 0) {
            isMovieFavorite = true;
            mFavorite.setText(R.string.remove_from_favorite);
        } else {
            isMovieFavorite = false;
            mFavorite.setText(R.string.add_favorite);
        }

        if (movie.getBackgroundImg() != null && movie.getBackgroundImg().length > 0) {
            Bitmap pic = BitmapFactory.decodeByteArray(movie.getBackgroundImg(), 0, movie.getBackgroundImg().length);
            mBackGround.setImageBitmap(pic);
        } else {
            Picasso.with(this).load(Constants.IMG_BASE_PATH_BACKGROUND + movie.getBackground()).placeholder(R.drawable.img_loading_background).error(R.drawable.img_missing_background).into(mBackGround);
        }
        if (movie.getPosterImg() != null && movie.getPosterImg().length > 0) {
            Bitmap pic = BitmapFactory.decodeByteArray(movie.getPosterImg(), 0, movie.getPosterImg().length);
            mPoster.setImageBitmap(pic);
        } else {
            Picasso.with(this).load(Constants.IMG_BASE_PATH + movie.getPosterPath()).placeholder(R.drawable.img_loading).error(R.drawable.img_missing).into(mPoster);
        }
        mFavorite.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                if (!isMovieFavorite) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIEDB_ID, movie.getMovieId());
                    contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getOriginalTitle());
                    contentValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, movie.getPopularity());
                    contentValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVote_average());
                    contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
                    contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
                    mPosterImage = ((BitmapDrawable) mPoster.getDrawable()).getBitmap();
                    contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_IMG, MovieDbHelper.getBitmapAsByteArray(mPosterImage));
                    mBackGroundImage = ((BitmapDrawable) mBackGround.getDrawable()).getBitmap();
                    contentValues.put(MovieContract.MovieEntry.COLUMN_BACKGOUND_IMG, MovieDbHelper.getBitmapAsByteArray(mBackGroundImage));
                    Uri addUri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);

                    if (addUri != null) {
                        Toast.makeText(getBaseContext(), R.string.movie_added, Toast.LENGTH_LONG).show();
                        mFavorite.setText(R.string.remove_from_favorite);
                        isMovieFavorite = true;

                    }

                } else {
                    int removed = getContentResolver().delete(movieUri, null, null);

                    if (removed != 0) {
                        Toast.makeText(getBaseContext(), R.string.movie_removed, Toast.LENGTH_LONG).show();
                        mFavorite.setText(R.string.add_favorite);
                        isMovieFavorite = false;

                    }


                }
            }
        });
        mYear.setText(movie.getReleaseDate());
        mScore.setText(String.valueOf(movie.getVote_average()) + getString(R.string.movie_rating_max));
        if (movie.getOverview().equals("null") || movie.getOverview().isEmpty()) {
            mOverView.setText(R.string.not_present);
        } else {
            mOverView.setText(movie.getOverview());

        }

        if (mReviewsList == null) {
            Bundle queryBundle = new Bundle();
            URL url = NetworkUtils.buildReviewUrl(mMovieId);
            queryBundle.putString(SEARCH_QUERY_URL_REVIEWS, url.toString());
            processReviewsLoader(queryBundle);
        }

        if (mTrailersList == null) {
            Bundle queryBundle = new Bundle();
            URL url = NetworkUtils.buildTrailersUrl(mMovieId);
            queryBundle.putString(SEARCH_QUERY_URL_TRAILERS, url.toString());
            processTrailersLoader(queryBundle);
        }


        mReviewsRecyclerView = findViewById(R.id.rv_reviews);
        mReviewsRecyclerView.setHasFixedSize(true);
        //Set layout manager
        mLayoutManagerReviews = new LinearLayoutManager(this);
        mReviewsRecyclerView.setLayoutManager(mLayoutManagerReviews);
        // Set Adapter
        mReviewsAdapter = new ReviewAdapter(mReviewsList);
        mReviewsRecyclerView.setAdapter(mReviewsAdapter);
        DividerItemDecoration verticalDivider = new DividerItemDecoration(
                mReviewsRecyclerView.getContext(), LinearLayoutManager.VERTICAL);
        mReviewsRecyclerView.addItemDecoration(verticalDivider);


        mTrailersRecyclerView = findViewById(R.id.rv_trailers);
        mTrailersRecyclerView.setHasFixedSize(true);
        //Set layout manager
        mLayoutManagerTrailers = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mTrailersRecyclerView.setLayoutManager(mLayoutManagerTrailers);
        // Set Adapter
        mTrailersAdapter = new TrailerAdapter(this, mTrailersList);
        mTrailersRecyclerView.setAdapter(mTrailersAdapter);
        DividerItemDecoration horizontalDivider = new DividerItemDecoration(
                mTrailersRecyclerView.getContext(), LinearLayoutManager.HORIZONTAL);
        mTrailersRecyclerView.addItemDecoration(horizontalDivider);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(REVIEW_LIST_STATE, (ArrayList<Review>) mReviewsList);
        outState.putStringArrayList(TRAILER_LIST_STATE, (ArrayList<String>) mTrailersList);
    }

    private void processReviewsLoader(Bundle queryBundle) {
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> reviewsLoader = loaderManager.getLoader(REVIEWS_LIST_LOADER);
        boolean isConnected = true;
        try {
            //Check for connection before continue
            isConnected = NetworkUtils.checkConnection(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isConnected) {
            if (reviewsLoader == null) {
                loaderManager.initLoader(REVIEWS_LIST_LOADER, queryBundle, this);
            } else {
                loaderManager.restartLoader(REVIEWS_LIST_LOADER, queryBundle, this);
            }

        } else {
            Toast.makeText(getApplicationContext(), R.string.no_connection_movie_detail, Toast.LENGTH_LONG).show();


        }

    }

    private void processTrailersLoader(Bundle queryBundle) {
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> trailersLoader = loaderManager.getLoader(TRAILERS_LIST_LOADER);
        boolean isConnected = true;
        try {
            //Check for connection before continue
            isConnected = NetworkUtils.checkConnection(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isConnected) {
            if (trailersLoader == null) {
                loaderManager.initLoader(TRAILERS_LIST_LOADER, queryBundle, this);
            } else {
                loaderManager.restartLoader(TRAILERS_LIST_LOADER, queryBundle, this);
            }
        }

    }

    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        if (id == REVIEWS_LIST_LOADER) {

            return new AsyncTaskLoader<String>(this) {
                String mReviewsListJson;

                @Override
                protected void onStartLoading() {

                    if (args == null) {
                        return;
                    }

                    if (mReviewsListJson != null) {
                        deliverResult(mReviewsListJson);
                    } else {
                        forceLoad();
                    }

                }

                @Override
                public String loadInBackground() {

                    //Get the search query from the bundle
                    String searchQueryUrlReviewsString = args.getString(SEARCH_QUERY_URL_REVIEWS);
                    //Protection code, should never occurs
                    if (searchQueryUrlReviewsString == null || TextUtils.isEmpty(searchQueryUrlReviewsString)) {
                        return null;
                    }

                    try {
                        //Parse the url and do the search
                        URL queryURL = new URL(searchQueryUrlReviewsString);

                        return NetworkUtils.getResponseFromHttpUrl(queryURL);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                public void deliverResult(String reviewsListJson) {
                    mReviewsListJson = reviewsListJson;
                    super.deliverResult(reviewsListJson);
                }
            };
        } else if (id == TRAILERS_LIST_LOADER) {

            return new AsyncTaskLoader<String>(this) {
                String mTrailersListJson;

                @Override
                protected void onStartLoading() {

                    if (args == null) {
                        return;
                    }

                    if (mTrailersListJson != null) {
                        deliverResult(mTrailersListJson);
                    } else {
                        forceLoad();
                    }

                }

                @Override
                public String loadInBackground() {

                    //Get the search query from the bundle
                    String searchQueryUrlTrailersString = args.getString(SEARCH_QUERY_URL_TRAILERS);
                    //Protection code, should never occurs
                    if (searchQueryUrlTrailersString == null || TextUtils.isEmpty(searchQueryUrlTrailersString)) {
                        return null;
                    }

                    try {
                        //Parse the url and do the search
                        URL queryURL = new URL(searchQueryUrlTrailersString);

                        return NetworkUtils.getResponseFromHttpUrl(queryURL);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                public void deliverResult(String trailersListJson) {
                    mTrailersListJson = trailersListJson;
                    super.deliverResult(trailersListJson);
                }
            };
        } else return null;
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        int id = loader.getId();
        if (id == REVIEWS_LIST_LOADER) {
            if (data == null) {
                //Handle error if needed
            } else {
                if (mReviewsList == null) {
                    //If no element is present the list now is composed only by the search results
                    mReviewsList = JsonUtils.parseReviewsJson(data);
                }
                mReviewsAdapter.setReviews(mReviewsList);
                mReviewsAdapter.notifyDataSetChanged();
            }
        }
        if (id == TRAILERS_LIST_LOADER) {
            if (data == null) {
                //Handle error if needed
            } else {
                if (mTrailersList == null) {
                    //If no element is present the list now is composed only by the search results
                    mTrailersList = JsonUtils.parseTrailersJson(data);
                }
                mTrailersAdapter.setTrailersIds(mTrailersList);
                mTrailersAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
        //Do nothing
    }
}
