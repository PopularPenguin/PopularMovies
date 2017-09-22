package com.popularpenguin.popularmovies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.popularpenguin.popularmovies.BuildConfig.MOVIE_API_KEY;

public class NetworkUtils {

    // TODO: Switch later to a Uri Builder?

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String POPULAR_URL =
            "http://api.themoviedb.org/3/movie/popular?api_key=" + MOVIE_API_KEY;
    private static final String TOP_RATED_URL =
            "http://api.themoviedb.org/3/movie/top_rated?api_key=" + MOVIE_API_KEY;

    private static final String BASE_IMG_URL = "https://image.tmdb.org/t/p/";
    private static final String DEFAULT_WIDTH = "w185/";
    private static final String IMG_URL = BASE_IMG_URL + DEFAULT_WIDTH;

    /** public method to fetch the movies as an ArrayList */
    public static ArrayList<Movie> getMovies(boolean isPopular) {
        ArrayList<Movie> movieList = null;
        String url = isPopular ? POPULAR_URL : TOP_RATED_URL;

        try {
            movieList = parseJsonIntoList(getJson(url));
        }
        catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return movieList;
    }

    /** Request the JSON from the server and return it as a String */
    private static String getJson(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();

        return response.body().string();
    }

    /** Parse the JSON String into an ArrayList of Movie objects */
    private static ArrayList<Movie> parseJsonIntoList(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);

        JSONArray results = jsonObject.getJSONArray("results");

        ArrayList<Movie> movieList = new ArrayList<>();

        for (int i = 0; i < results.length(); i++) {
            JSONObject movieData = results.getJSONObject(i);

            String title = movieData.getString("title");
            String posterPath = IMG_URL + movieData.getString("poster_path");
            String releaseDate = movieData.getString("release_date");
            String overview = movieData.getString("overview");
            double average = movieData.getDouble("vote_average");

            Movie movie = new Movie(title, posterPath, releaseDate, overview, average);
            movieList.add(movie);
        }

        return movieList;
    }
}
