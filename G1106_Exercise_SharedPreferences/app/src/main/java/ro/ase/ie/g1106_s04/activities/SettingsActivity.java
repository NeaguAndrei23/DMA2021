package ro.ase.ie.g1106_s04.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ro.ase.ie.g1106_s04.R;

public class SettingsActivity extends AppCompatActivity {

    private EditText etFavoriteMovie;
    private SharedPreferences sharedPreferences;

    // Constants for SharedPreferences
    private static final String PREFS_NAME = "movie_prefs";
    private static final String KEY_FAVORITE = "favorite_movie";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize views
        etFavoriteMovie = findViewById(R.id.etFavoriteMovie);

        // Get SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Load existing value (if any)
        loadSettings();
    }

    private void loadSettings() {
        String favoriteMovie = sharedPreferences.getString(KEY_FAVORITE, "");
        etFavoriteMovie.setText(favoriteMovie);
    }

    // Called when Save button is clicked (via android:onClick="saveSettings")
    public void saveSettings(View view) {
        String favoriteMovie = etFavoriteMovie.getText().toString().trim();

        if (favoriteMovie.isEmpty()) {
            Toast.makeText(this, "Please enter a movie title", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_FAVORITE, favoriteMovie);
        editor.apply();  // async, non-blocking

        Toast.makeText(this, "Favorite saved: " + favoriteMovie, Toast.LENGTH_SHORT).show();
        finish();  // Close activity and return to MainActivity
    }
}
