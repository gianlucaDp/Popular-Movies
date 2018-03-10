package gianlucadp.com.popularmovies.data;


import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {
    public static final String AUTHORITY = "gianlucadp.com.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_MOVIES = MovieEntry.TABLE_NAME;

    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();
        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_MOVIEDB_ID ="movieId";
        public static final String COLUMN_TITLE="title";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_VOTE_AVERAGE="vote_average";
        public static final String COLUMN_OVERVIEW="overview";
        public static final String COLUMN_RELEASE_DATE="releaseDate";
        public static final String COLUMN_POSTER_IMG="poster_img";
        public static final String COLUMN_BACKGOUND_IMG = "background_img";


    }
}
