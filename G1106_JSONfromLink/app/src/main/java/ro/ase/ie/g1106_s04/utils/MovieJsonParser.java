package ro.ase.ie.g1106_s04.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ro.ase.ie.g1106_s04.model.GenreEnum;
import ro.ase.ie.g1106_s04.model.Movie;
import ro.ase.ie.g1106_s04.model.ParentalGuidanceEnum;

public class MovieJsonParser {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

    public static List<Movie> fromJson(String jsonString) {
        List<Movie> movies = null;

        if(jsonString!= null)
        {
            movies = new ArrayList<>();
            try {
                JSONArray array = new JSONArray(jsonString);
                for(int index = 0; index < array.length(); index++)
                {
                    JSONObject jsonObject = array.getJSONObject(index);
                    Movie movie = readMovie(jsonObject);
                    movies.add(movie);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }
        return movies;
    }

    private static Movie readMovie(JSONObject jsonObject) throws JSONException {
        String title = jsonObject.getString("title");
        Double budget = jsonObject.getDouble("budget");

        // Parse genre enum
        String genreString = jsonObject.getString("genre");
        GenreEnum genre = GenreEnum.valueOf(genreString);

        // Parse release date
        Date release = null;
        if (jsonObject.has("release")) {
            String releaseString = jsonObject.getString("release");
            try {
                release = sdf.parse(releaseString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // Parse parental guidance enum
        ParentalGuidanceEnum pGuidance = null;
        if (jsonObject.has("guidance")) {
            String guidanceString = jsonObject.getString("guidance");
            pGuidance = ParentalGuidanceEnum.valueOf(guidanceString);
        }

        // Parse rating
        Float rating = null;
        if (jsonObject.has("rating")) {
            rating = (float) jsonObject.getDouble("rating");
        }

        // Parse duration (comes as string in JSON)
        Integer duration = null;
        if (jsonObject.has("duration")) {
            String durationString = jsonObject.getString("duration");
            duration = Integer.parseInt(durationString);
        }

        // Parse watched
        Boolean watched = null;
        if (jsonObject.has("watched")) {
            watched = jsonObject.getBoolean("watched");
        }

        // Parse poster URL
        String posterUrl = null;
        if (jsonObject.has("posterUrl")) {
            posterUrl = jsonObject.getString("posterUrl");
        }

        return new Movie(title, budget, release, duration, genre, pGuidance, rating, watched, posterUrl);
    }


}
