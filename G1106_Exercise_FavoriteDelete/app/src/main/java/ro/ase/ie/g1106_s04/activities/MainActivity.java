package ro.ase.ie.g1106_s04.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import ro.ase.ie.g1106_s04.R;
import ro.ase.ie.g1106_s04.adapters.MovieAdapter;
import ro.ase.ie.g1106_s04.database.DatabaseManager;
import ro.ase.ie.g1106_s04.database.MovieDAO;
import ro.ase.ie.g1106_s04.model.Movie;

public class MainActivity extends AppCompatActivity implements IMovieEventListener {

    private static final int ADD_MOVIE = 100;
    private static final int UPDATE_MOVIE = 200;

    // SharedPreferences constants
    private static final String PREFS_NAME = "movie_prefs";
    private static final String KEY_FAVORITE = "favorite_movie";

    private ActivityResultLauncher<Intent> launcher;
    private final ArrayList<Movie> movieList = new ArrayList<>();
    private MovieAdapter movieAdapter;
    private RecyclerView recyclerView;
    private DatabaseManager databaseManager;
    private MovieDAO movieTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        movieAdapter = new MovieAdapter(this, movieList);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(movieAdapter);
        databaseManager = DatabaseManager.getInstance(getApplicationContext());
        movieTable = databaseManager.getMovieDao();
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if (o.getResultCode() == RESULT_OK) {
                            Intent data = o.getData();
                            Movie movie = data.getParcelableExtra("movie");
                            if (!movieList.contains(movie)) {
                                movieList.add(movie);
                            } else {
                                int position = movieList.indexOf(movie);
                                movieList.set(position, movie);
                            }
                            movieTable.insertMovie(movie);
                            Log.d("MainActivityTag", movie.toString());
                            movieAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_movie_menu_item) {
            Intent intent = new Intent(MainActivity.this, MovieActivity.class);
            intent.putExtra("action_code", ADD_MOVIE);
            launcher.launch(intent);
        } else if (item.getItemId() == R.id.about_menu_item) {
            Toast.makeText(MainActivity.this,
                    "DMA2025 - G1106!",
                    Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMovieClick(int position) {
        Movie currentMovie = movieList.get(position);
        Intent intent = new Intent(MainActivity.this, MovieActivity.class);
        intent.putExtra("action_code", UPDATE_MOVIE);
        intent.putExtra("movie", currentMovie);
        launcher.launch(intent);
    }

    @Override
    public void onMovieDelete(int position) {
        Movie movieToDelete = movieList.get(position);

        // 1. Read favorite from SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String favoriteTitle = prefs.getString(KEY_FAVORITE, null);

        // 2. Check if this movie is the favorite
        if (favoriteTitle != null && favoriteTitle.equals(movieToDelete.getTitle())) {
            // Cannot delete favorite movie
            Toast.makeText(this, "Cannot delete favorite movie!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // 3. Not favorite - proceed with delete
        movieTable.deleteMovie(movieToDelete);
        movieList.remove(position);
        movieAdapter.notifyDataSetChanged();
        Toast.makeText(this, "Movie deleted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMovieLongClick(int position) {
        Movie movie = movieList.get(position);

        // Save as favorite in SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_FAVORITE, movie.getTitle());
        editor.apply();

        Toast.makeText(this, "'" + movie.getTitle() + "' set as favorite!",
                Toast.LENGTH_SHORT).show();
    }
}
