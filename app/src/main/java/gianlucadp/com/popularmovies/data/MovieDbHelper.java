package gianlucadp.com.popularmovies.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import gianlucadp.com.popularmovies.data.MovieContract.MovieEntry;
import gianlucadp.com.popularmovies.model.Movie;

public class MovieDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "popularMovieDB.db";
    private static final int VERSION = 3;

    MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY, " +
                MovieEntry.COLUMN_MOVIEDB_ID + " INTEGER NOT NULL," +
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL," +
                MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL," +
                MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL," +
                MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_POSTER_IMG + " BLOB," +
                MovieEntry.COLUMN_BACKGOUND_IMG + " BLOB);";

        sqLiteDatabase.execSQL(CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME + ";");
        onCreate(sqLiteDatabase);

    }


    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        return outputStream.toByteArray();
    }

    public static List<Movie> getMoviesFromCursor(Cursor cursor) {
        List<Movie> movies = new ArrayList<Movie>();
        int movieIdIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIEDB_ID);
        int titleIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE);
        int popularityIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POPULARITY);
        int averageIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE);
        int overviewIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW);
        int dateIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
        int posterImgIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_IMG);
        int backgroundImgIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKGOUND_IMG);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(movieIdIndex);
            String title = cursor.getString(titleIndex);
            double popularity = cursor.getDouble(popularityIndex);
            double vote_average = cursor.getDouble(averageIndex);
            String overview = cursor.getString(overviewIndex);
            String releaseDate = cursor.getString(dateIndex);
            byte[] posterImg = cursor.getBlob(posterImgIndex);
            byte[] backgroundImg = cursor.getBlob(backgroundImgIndex);
            movies.add(new Movie(title, overview, releaseDate, popularity, vote_average, id, posterImg, backgroundImg));
        }

        return movies;
    }

}
