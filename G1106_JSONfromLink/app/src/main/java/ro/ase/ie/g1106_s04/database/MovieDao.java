package ro.ase.ie.g1106_s04.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ro.ase.ie.g1106_s04.model.Movie;

@Dao
public interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Movie movie);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Movie> movies);

    @Update
    void update(Movie movie);

    @Delete
    void delete(Movie movie);

    @Query("DELETE FROM movies")
    void deleteAll();

    @Query("SELECT * FROM movies")
    List<Movie> getAll();

    @Query("SELECT * FROM movies WHERE id = :movieId")
    Movie getById(long movieId);

    @Query("SELECT * FROM movies WHERE title = :title")
    List<Movie> getByTitle(String title);

    @Query("SELECT * FROM movies WHERE genre = :genre")
    List<Movie> getByGenre(String genre);

    @Query("SELECT * FROM movies WHERE watched = 1")
    List<Movie> getWatchedMovies();

    @Query("SELECT * FROM movies WHERE watched = 0 OR watched IS NULL")
    List<Movie> getUnwatchedMovies();

    @Query("SELECT COUNT(*) FROM movies")
    int getCount();
}
