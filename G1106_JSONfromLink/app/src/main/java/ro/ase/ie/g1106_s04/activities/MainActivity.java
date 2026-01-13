package ro.ase.ie.g1106_s04.activities;

import android.content.Intent;
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

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import ro.ase.ie.g1106_s04.R;
import ro.ase.ie.g1106_s04.adapters.MovieAdapter;
import ro.ase.ie.g1106_s04.database.MovieRepository;
import ro.ase.ie.g1106_s04.model.Movie;
import ro.ase.ie.g1106_s04.utils.HTTPConnectionService;
import ro.ase.ie.g1106_s04.utils.MovieJsonParser;

public class MainActivity extends AppCompatActivity implements IMovieEventListener{

    private static final int ADD_MOVIE = 100;
    private static final int UPDATE_MOVIE = 200;
    private ActivityResultLauncher<Intent> launcher;
    private final ArrayList<Movie> movieList = new ArrayList<>();
    private MovieAdapter movieAdapter;
    private RecyclerView recyclerView;
    private MovieRepository movieRepository;

    private static final String MOVIES_JSON_URL ="https://jsonkeeper.com/b/FLBCO";


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
        movieAdapter=new MovieAdapter(this,movieList);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(movieAdapter);

        // Initialize Room repository
        movieRepository = new MovieRepository(this);

        // Load movies from database on startup
        loadMoviesFromDatabase();

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if(o.getResultCode() == RESULT_OK)
                        {
                            Intent data = o.getData();
                            Movie movie = data.getParcelableExtra("movie");
                            if(!movieList.contains(movie)){
                                // New movie - add to list and insert to database
                                movieList.add(movie);
                                movieRepository.insert(movie, id -> {
                                    runOnUiThread(() -> {
                                        Log.d("MainActivityTag", "Movie inserted with ID: " + id);
                                    });
                                });
                            }
                            else{
                                // Existing movie - update in list and database
                                int position=movieList.indexOf(movie);
                                movieList.set(position, movie);
                                movieRepository.update(movie, result -> {
                                    runOnUiThread(() -> {
                                        Log.d("MainActivityTag", "Movie updated: " + movie.getTitle());
                                    });
                                });
                            }

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

        if(item.getItemId() == R.id.add_movie_menu_item)
        {
            //add a new movie instance
            Intent intent = new Intent(MainActivity.this, MovieActivity.class);
            intent.putExtra("action_code", ADD_MOVIE);
            launcher.launch(intent);
        } else if (item.getItemId() == R.id.load_json_menu_item) {
            {
                loadMoviesFromJson();
            }
            
        } else if(item.getItemId() == R.id.about_menu_item)
        {
            Toast.makeText(MainActivity.this,
                    "DMA2025 - G1106!",
                    Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadMoviesFromDatabase() {
        movieRepository.getAll(movies -> {
            runOnUiThread(() -> {
                if (movies != null && !movies.isEmpty()) {
                    movieList.clear();
                    movieList.addAll(movies);
                    movieAdapter.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this,
                            "Loaded " + movies.size() + " movies from database",
                            Toast.LENGTH_SHORT).show();
                    Log.d("MainActivityTag", "Loaded " + movies.size() + " movies from database");
                }
            });
        });
    }

    private void loadMoviesFromJson() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    HTTPConnectionService httpConnectionService = new HTTPConnectionService(MOVIES_JSON_URL);
                    String jsonString = httpConnectionService.getData();

                    if (jsonString != null && !jsonString.isEmpty()) {

                        List<Movie> movies = MovieJsonParser.fromJson(jsonString);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (movies != null && !movies.isEmpty()) {
                                    movieList.clear();
                                    movieList.addAll(movies);
                                    movieAdapter.notifyDataSetChanged();

                                    // Save loaded movies to database
                                    movieRepository.deleteAll(result -> {
                                        movieRepository.insertAll(movies, insertResult -> {
                                            runOnUiThread(() -> {
                                                Toast.makeText(MainActivity.this,
                                                        "Movies saved to database",
                                                        Toast.LENGTH_SHORT).show();
                                            });
                                        });
                                    });

                                } else {
                                    Toast.makeText(MainActivity.this,
                                            "No movies found",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this,
                                        "Failed to load Json",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this,
                                    "Error loading movies: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                            Log.e("MainActivity", "Error loading movIes", e);
                        }
                    });
                }
            }
        });
        thread.start();
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
        Movie movie = movieList.get(position);
        movieList.remove(position);
        movieAdapter.notifyDataSetChanged();

        // Delete from database
        movieRepository.delete(movie, result -> {
            runOnUiThread(() -> {
                Log.d("MainActivityTag", "Movie deleted: " + movie.getTitle());
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (movieRepository != null) {
            movieRepository.shutdown();
        }
    }

//    private void trustEveryone()
//    {
//        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
//            @Override
//            public boolean verify(String s, SSLSession sslSession) {
//                return true;
//            }
//        });
//        try {
//            SSLContext context = SSLContext.getInstance("TLS");
//            context.init(null, new X509TrustManager[]{new X509TrustManager()});
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException(e);
//        }
//    }




}