package gianlucadp.com.popularmovies.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;


public class NetworkUtils {


    public static URL buildMovieUrl(String endPoint, String page) {
        String baseUrl = Constants.BASE_URL + endPoint;
        Uri builtUri = Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter(Constants.PARAM_KEY, Constants.PARAM_KEY_VALUE)
                .appendQueryParameter(Constants.PARAM_PAGE, page)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildReviewUrl(int movieId) {
        String baseUrl = Constants.BASE_URL + "/" + String.valueOf(movieId) + Constants.REVIEWS_ENDPOINT;
        Uri builtUri = Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter(Constants.PARAM_KEY, Constants.PARAM_KEY_VALUE)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildTrailersUrl(int movieId) {
        String baseUrl = Constants.BASE_URL + "/" + String.valueOf(movieId) + Constants.TRAILERS_ENDPOINT;
        Uri builtUri = Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter(Constants.PARAM_KEY, Constants.PARAM_KEY_VALUE)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }


    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static boolean checkConnection(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
}
