package com.popularpenguin.popularmovies.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.popularpenguin.popularmovies.data.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.popularpenguin.popularmovies.BuildConfig.MOVIE_API_KEY;

@SuppressWarnings("unused")
public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String BASE_URL = "http://api.themoviedb.org/3/movie/";

    private static final String POPULAR_URL = BASE_URL + "popular?api_key=" + MOVIE_API_KEY;
    private static final String TOP_RATED_URL = BASE_URL + "top_rated?api_key=" + MOVIE_API_KEY;
    // Movie videos is /movie/{id}/videos
    // Movie reviews is /movie/{id}/reviews

    private static final String BASE_IMG_URL = "https://image.tmdb.org/t/p/";
    private static final String DEFAULT_WIDTH = "w185/";
    private static final String IMG_URL = BASE_IMG_URL + DEFAULT_WIDTH;

    /** public method to fetch the movies as an ArrayList */
    public static ArrayList<Movie> getMovies(Context ctx, boolean isPopular) {
        // Check for a network connection
        if (!isConnected(ctx)) {
            return new ArrayList<>();
        }

        ArrayList<Movie> movieList = null;
        String url = isPopular ? POPULAR_URL : TOP_RATED_URL;

        try {
            movieList = parseMovieJSON(getJson(url));
        }
        catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return movieList;
    }

    /** Check for a network connection before downloading data */
    public static boolean isConnected(Context ctx) {
        // Check if there is an active network connection
        ConnectivityManager cm =
                (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        return info != null && info.isConnectedOrConnecting();
    }

    /** Get the trailers and add them to the movie object passed in to this method
     * This method should be called from the detail activity
     * @param ctx The context from the detail activity
     * @param id The id of the movie that the detail activity is displaying (from movie.getId())
     * */
    public static ArrayList<String[]> getTrailers(Context ctx, int id) {
        ArrayList<String[]> trailerList = new ArrayList<>();

        // if there is no connection, just return an empty list
        if (!isConnected(ctx)) return trailerList;

        // Movie videos endpoint is /movie/{id}/videos
        String urlString = BASE_URL + id + "/videos?api_key=" + MOVIE_API_KEY;

        try {
            trailerList = parseTrailerJSON(getJson(urlString));
        }
        catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return trailerList;
    }

    /** Get the reviews and add them to the movie object passed in to this method
     * this method should be called from the detail activity
     * @param ctx The context from the detail activity
     * @param id The id of the movie that the detail activity is displaying (from movie.getId())
     * */
    public static ArrayList<String[]> getReviews(Context ctx, int id) {
        ArrayList<String[]> reviewList = new ArrayList<>();

        // if there is no connection, just return an empty list
        if (!isConnected(ctx)) return reviewList;

        // Movie reviews endpoint is /movie/{id}/reviews
        String urlString = BASE_URL + id + "/reviews?api_key=" + MOVIE_API_KEY;

        try {
            reviewList = parseReviewJSON(getJson(urlString));
        }
        catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return reviewList;
    }


    /** Request the JSON from the server and return it as a String
     * This code was taken from the OKHTTP main page*/
    private static String getJson(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();

        return response.body().string();
    }

    /** Parse the JSON String into an ArrayList of Movie objects */
    private static ArrayList<Movie> parseMovieJSON(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);

        JSONArray results = jsonObject.getJSONArray("results");

        ArrayList<Movie> movieList = new ArrayList<>();

        for (int i = 0; i < results.length(); i++) {
            JSONObject movieData = results.getJSONObject(i);

            int id = movieData.getInt("id");
            String title = movieData.getString("title");
            String posterPath = IMG_URL + movieData.getString("poster_path");
            String releaseDate = movieData.getString("release_date");
            String overview = movieData.getString("overview");
            double average = movieData.getDouble("vote_average");

            Movie movie = new Movie(id, title, posterPath, releaseDate, overview, average);
            movieList.add(movie);
        }

        return movieList;
    }

    /** Parse the JSON String for trailers and return the data */
    private static ArrayList<String[]> parseTrailerJSON(String jsonString)
            throws JSONException {

        JSONObject jsonObject = new JSONObject(jsonString);

        JSONArray results = jsonObject.getJSONArray("results");
        int size = results.length();

        String[] keys = new String[size];
        String[] names = new String[size];

        for (int i = 0; i < results.length(); i++) {
            keys[i] = results.getJSONObject(i).getString("key");
            names[i] = results.getJSONObject(i).getString("name");
        }

        ArrayList<String[]> trailerList = new ArrayList<>();
        trailerList.add(keys);
        trailerList.add(names);

        return trailerList;
    }

    /** Parse the JSON String for reviews and add the data to the movie */
    private static ArrayList<String[]> parseReviewJSON(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);

        JSONArray results = jsonObject.getJSONArray("results");
        int size = results.length();

        String[] authors = new String[size];
        String[] content = new String[size];
        String[] urls = new String[size];

        for (int i = 0; i < results.length(); i++) {
            authors[i] = results.getJSONObject(i).getString("author");
            content[i] = results.getJSONObject(i).getString("content");
            urls[i] = results.getJSONObject(i).getString("url");
        }

        ArrayList<String[]> reviewList = new ArrayList<>();
        reviewList.add(authors);
        reviewList.add(content);
        reviewList.add(urls);

        return reviewList;
    }
}
