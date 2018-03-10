package gianlucadp.com.popularmovies.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gianlucadp.com.popularmovies.model.Movie;
import gianlucadp.com.popularmovies.model.Review;


public class JsonUtils {
    private static final String PARAM_RESULTS = "results";
    private static final String PARAM_TITLE = "original_title";
    private static final String PARAM_VOTE = "vote_average";
    private static final String PARAM_POPULARITY = "popularity";
    private static final String PARAM_POSTER = "poster_path";
    private static final String PARAM_RELEASE = "release_date";
    private static final String PARAM_OVERVIEW = "overview";
    private static final String PARAM_BACKGROUND = "backdrop_path";
    private static final String PARAM_ID = "id";
    private static final String PARAM_TRAILER_KEY = "key";
    private static final String PARAM_TYPE = "type";
    private static final String PARAM_AUTHOR = "author";
    private static final String PARAM_CONTENT = "content";

    public static List<Movie> parseMovieJson(String json) {
        List<Movie> parsedMovies = new ArrayList<Movie>();
        try {

            JSONObject jsonData = new JSONObject(json);
            JSONArray results = jsonData.getJSONArray(PARAM_RESULTS);

            for (int i = 0; i < results.length(); i++) {
                JSONObject currentResult = (JSONObject) results.get(i);
                String title = currentResult.optString(PARAM_TITLE);
                double vote_average = currentResult.getDouble(PARAM_VOTE);
                double popularity = currentResult.getDouble(PARAM_POPULARITY);
                String posterPath = currentResult.optString(PARAM_POSTER);
                String releaseDate = currentResult.optString(PARAM_RELEASE);
                String overview = currentResult.optString(PARAM_OVERVIEW);
                String background = currentResult.optString(PARAM_BACKGROUND);
                int id = currentResult.getInt(PARAM_ID);

                Movie currentMovie = new Movie(title, overview, releaseDate, posterPath, popularity, vote_average, background, id);
                parsedMovies.add(currentMovie);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parsedMovies;

    }

    public static List<String> parseTrailersJson(String json) {
        List<String> trailers = new ArrayList<String>();
        try {
            JSONObject jsonData = new JSONObject(json);
            JSONArray results = jsonData.getJSONArray(PARAM_RESULTS);
            for (int i = 0; i < results.length(); i++) {
                JSONObject currentResult = (JSONObject) results.get(i);
                String videoType = currentResult.optString(PARAM_TYPE);
                if (videoType.equals("Trailer")) {
                    String trailerKey = currentResult.optString(PARAM_TRAILER_KEY);
                    trailers.add(trailerKey);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return trailers;
    }


    public static List<Review> parseReviewsJson(String json) {
        List<Review> reviews = new ArrayList<Review>();
        try {
            JSONObject jsonData = new JSONObject(json);
            JSONArray results = jsonData.getJSONArray(PARAM_RESULTS);
            for (int i = 0; i < results.length(); i++) {
                JSONObject currentResult = (JSONObject) results.get(i);
                String author = currentResult.optString(PARAM_AUTHOR);
                String content = currentResult.optString(PARAM_CONTENT);
                Review review = new Review(author, content);
                reviews.add(review);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reviews;
    }
}
