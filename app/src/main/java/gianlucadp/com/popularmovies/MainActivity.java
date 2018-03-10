package gianlucadp.com.popularmovies;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import gianlucadp.com.popularmovies.data.MovieContract;
import gianlucadp.com.popularmovies.data.MovieDbHelper;
import gianlucadp.com.popularmovies.model.Movie;
import gianlucadp.com.popularmovies.utils.Constants;
import gianlucadp.com.popularmovies.utils.JsonUtils;
import gianlucadp.com.popularmovies.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks {
    //Variables used into Loader
    private static final int MOVIE_LIST_LOADER = 33;
    private static final int FAVORITE_LIST_LOADER = 36;
    private static final String SEARCH_QUERY_URL_EXTRA = "query";
    //Variables to restore state
    private static final String NUMBER_PAGE_STATE = "PAGE";
    private static final String MOVIE_LIST_STATE = "MOVIE_LIST";
    private static final String SORTING_STATE = "SORTING";
    private static final String VIEW_STATE = "VIEWED";

    private RecyclerView mMoviesRecyclerView;
    private MovieAdapter mMovieAdapter;
    private ProgressBar mLoading;
    private Button mLoadMore;
    private int mMoviePage;
    private List<Movie> mMovieList;
    private String mCurrentSorting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoading = findViewById(R.id.pb_loading);

        //If there is a saved instance reload the parameters
        if (savedInstanceState != null) {
            mMoviePage = savedInstanceState.getInt(NUMBER_PAGE_STATE);
            mMovieList = savedInstanceState.getParcelableArrayList(MOVIE_LIST_STATE);
            mCurrentSorting = savedInstanceState.getString(SORTING_STATE);


        } else {
            //Load default configuration
            mMoviePage = 1;
            mCurrentSorting = Constants.POPULAR_ENDPOINT;
        }

        //If no movie has been downloaded
        if (mMovieList == null) {
            //Create url for the search
            URL url = NetworkUtils.buildMovieUrl(mCurrentSorting, String.valueOf(mMoviePage));
            //Put it into the bundle
            Bundle queryBundle = new Bundle();
            queryBundle.putString(SEARCH_QUERY_URL_EXTRA, url.toString());
            //Start the loader
            //getSupportLoaderManager().initLoader(MOVIE_LIST_LOADER, null, this); enough to use the one in processLoader
            processLoader(queryBundle);

        }
        setContentView(R.layout.activity_main);

        mMoviesRecyclerView = findViewById(R.id.rv_movies);
        mMoviesRecyclerView.setHasFixedSize(true);
        //Set layout manager
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, numberOfColumns());
        mMoviesRecyclerView.setLayoutManager(layoutManager);
        // Set Adapter
        mMovieAdapter = new MovieAdapter(this, mMovieList);
        mMoviesRecyclerView.setAdapter(mMovieAdapter);
        //Set a listener
        mMoviesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                GridLayoutManager layoutManager = GridLayoutManager.class.cast(recyclerView.getLayoutManager());
                int totalItemCount = layoutManager.getItemCount();
                int lastVisible = layoutManager.findLastVisibleItemPosition();
                //If the last elements of the list are reached, show the load more button
                boolean endHasBeenReached = lastVisible + numberOfColumns() >= totalItemCount;
                if (mMovieList != null && totalItemCount > 0 && endHasBeenReached && mCurrentSorting != Constants.FAVORITE_ENDPOINT) {
                    mLoadMore.setVisibility(View.VISIBLE);
                } else {
                    mLoadMore.setVisibility(View.INVISIBLE);

                }
            }
        });
        if (savedInstanceState != null && mMoviesRecyclerView != null) {
            scrollToPosition(savedInstanceState.getInt(VIEW_STATE));

        }
        //Load more button
        mLoadMore = findViewById(R.id.btn_load_more);
        mLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMoreResults();
            }
        });


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MOVIE_LIST_STATE, (ArrayList<Movie>) mMovieList);
        outState.putInt(NUMBER_PAGE_STATE, mMoviePage);
        outState.putString(SORTING_STATE, mCurrentSorting);
        GridLayoutManager layoutManager = GridLayoutManager.class.cast(mMoviesRecyclerView.getLayoutManager());
        int lastVisible = layoutManager.findLastVisibleItemPosition();
        outState.putInt(VIEW_STATE, lastVisible);

    }

    @Override
    public Loader onCreateLoader(int id, final Bundle args) {
        if (id == MOVIE_LIST_LOADER) {
            return new AsyncTaskLoader<String>(this) {
                String mMovieListJson;

                @Override
                protected void onStartLoading() {

                    if (args == null) {
                        return;
                    }

                    if (mMovieListJson != null) {
                        deliverResult(mMovieListJson);
                    } else {
                        forceLoad();
                    }
                }

                @Override
                public String loadInBackground() {

                    String searchQueryUrlString = args.getString(SEARCH_QUERY_URL_EXTRA);

                    //Protection code, should never occurs
                    if (searchQueryUrlString == null || TextUtils.isEmpty(searchQueryUrlString)) {
                        return null;
                    }

                    try {
                        URL queryURL = new URL(searchQueryUrlString);

                        return NetworkUtils.getResponseFromHttpUrl(queryURL);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                public void deliverResult(String movieListJson) {
                    mMovieListJson = movieListJson;
                    super.deliverResult(movieListJson);
                }
            };
        } else if (id == FAVORITE_LIST_LOADER) {
            return new AsyncTaskLoader<Cursor>(this) {
                Cursor mMovieData;

                @Override
                protected void onStartLoading() {

                    if (mMovieData != null) {
                        deliverResult(mMovieData);
                    } else {
                        forceLoad();
                    }
                }

                @Override
                public Cursor loadInBackground() {

                    try {
                        return getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, MovieContract.MovieEntry.COLUMN_POPULARITY);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                public void deliverResult(Cursor data) {
                    mMovieData = data;
                    super.deliverResult(data);
                }
            };
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        int id = loader.getId();
        if (id == MOVIE_LIST_LOADER) {
            if (data == null) {
                //Handle error if needed
            } else {
                if (mMovieList == null) {
                    //If no element is present the list now is composed only by the search results
                    mMovieList = JsonUtils.parseMovieJson((String) data);
                } else {
                    //If the list already exists, append the results
                    mMovieList.addAll(JsonUtils.parseMovieJson((String) data));
                }
                mMovieAdapter.setMovies(mMovieList);
                mMovieAdapter.notifyDataSetChanged();
                mLoading = findViewById(R.id.pb_loading);
                mLoading.setVisibility(View.INVISIBLE);
                mLoadMore.setVisibility(View.INVISIBLE);
            }
        } else if (id == FAVORITE_LIST_LOADER) {
            if (data == null) {
                //Handle error if needed
            } else {
            }
            mMovieList = MovieDbHelper.getMoviesFromCursor((Cursor) data);
            mMovieAdapter.setMovies(mMovieList);
            mMovieAdapter.notifyDataSetChanged();
            mLoading = findViewById(R.id.pb_loading);
            mLoading.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void onLoaderReset(Loader loader) {
        //Do nothing
    }

    @Override
    protected void onResume() {
        super.onResume();
        //The only list that change outside the mainActivity is the favorites one
        if (mCurrentSorting.equals(Constants.FAVORITE_ENDPOINT)) {
            getSupportLoaderManager().restartLoader(FAVORITE_LIST_LOADER, null, this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.mi_sort_popularity:
                if (!mCurrentSorting.equals(Constants.POPULAR_ENDPOINT)) {
                    mMoviePage = 1;
                    mMovieList = null;
                    mCurrentSorting = Constants.POPULAR_ENDPOINT;
                    newSearch();
                    scrollToPosition(0);
                }
                return true;
            case R.id.mi_sort_rating:
                if (!mCurrentSorting.equals(Constants.TOP_RATED_ENDPOINT)) {
                    mMoviePage = 1;
                    mMovieList = null;
                    mCurrentSorting = Constants.TOP_RATED_ENDPOINT;
                    newSearch();
                    scrollToPosition(0);
                }
                return true;
            case R.id.mi_favorites:
                if (!mCurrentSorting.equals(Constants.FAVORITE_ENDPOINT)) {
                    mMoviePage = 1;
                    mMovieList = null;
                    mCurrentSorting = Constants.FAVORITE_ENDPOINT;
                    loadFavorites();
                    scrollToPosition(0);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void scrollToPosition(int position) {
        GridLayoutManager layoutManager = GridLayoutManager.class.cast(mMoviesRecyclerView.getLayoutManager());
        layoutManager.scrollToPositionWithOffset(position, 0);
    }

    private void loadMoreResults() {
        mMoviePage++;
        newSearch();
    }

    private void newSearch() {
        mLoading = findViewById(R.id.pb_loading);
        mLoading.setVisibility(View.VISIBLE);
        URL url = NetworkUtils.buildMovieUrl(mCurrentSorting, String.valueOf(mMoviePage));
        Bundle queryBundle = new Bundle();
        queryBundle.putString(SEARCH_QUERY_URL_EXTRA, url.toString());
        processLoader(queryBundle);
    }

    private void loadFavorites() {
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<Cursor> favLoader = loaderManager.getLoader(FAVORITE_LIST_LOADER);
        if (favLoader == null) {
            loaderManager.initLoader(FAVORITE_LIST_LOADER, null, this);
        } else {
            loaderManager.restartLoader(FAVORITE_LIST_LOADER, null, this);
        }

    }

    private void processLoader(Bundle queryBundle) {
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> pageLoader = loaderManager.getLoader(MOVIE_LIST_LOADER);
        boolean isConnected = true;
        try {
            //Check for connection before continue
            isConnected = NetworkUtils.checkConnection(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isConnected) {
            if (pageLoader == null) {
                loaderManager.initLoader(MOVIE_LIST_LOADER, queryBundle, this);
            } else {
                loaderManager.restartLoader(MOVIE_LIST_LOADER, queryBundle, this);
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_LONG).show();


        }

    }


    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int widthDivider = 400;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 2;
        return nColumns;
    }

}
