package ro.ase.ie.g1106_s04.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ro.ase.ie.g1106_s04.model.Movie;

public class MovieJsonParser{
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YYYY", Locale.US);
    public static String toJson(List<Movie> movies)
    {
        JSONArray jsonArray = new JSONArray();
        for(Movie movie: movies)
        {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("title", movie.getTitle());
                jsonObject.put("budget", movie.getBudget());
                jsonObject.put("genre", movie.getGenre());
                if(movie.getRelease()!= null)
                {
                    jsonObject.put("release", sdf.format(movie.getRelease()));
                }
                if(movie.getpGuidance()!=null)
                {
                    jsonObject.put("pGuidance", movie.getpGuidance().name());
                }
                if(movie.getRating()!=null)
                {
                    jsonObject.put("Rating", movie.getRating());
                }
                if(movie.getDuration()!=null)
                {
                    jsonObject.put("Duration", String.valueOf(movie.getDuration()));
                }
                if(movie.getWatched()!=null)
                {
                    jsonObject.put("Watched", movie.getWatched());
                }
                if(movie.getPosterUrl()!=null)
                {
                    jsonObject.put("POSTER URL",movie.getPosterUrl());
                }
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }
        return jsonArray.toString();
    }

}
