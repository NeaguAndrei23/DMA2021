package ro.ase.ie.g1106_s04.database;

import android.content.Context;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import ro.ase.ie.g1106_s04.model.Movie;

public class MovieRepository {

    private final MovieDao movieDao;
    private final ExecutorService executorService;

    public MovieRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        movieDao = database.movieDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    // Insert operations
    public void insert(Movie movie, RepositoryCallback<Long> callback) {
        executorService.execute(() -> {
            long id = movieDao.insert(movie);
            movie.setId(id);
            if (callback != null) {
                callback.onComplete(id);
            }
        });
    }

    public void insertAll(List<Movie> movies, RepositoryCallback<Void> callback) {
        executorService.execute(() -> {
            movieDao.insertAll(movies);
            if (callback != null) {
                callback.onComplete(null);
            }
        });
    }

    // Update operations
    public void update(Movie movie, RepositoryCallback<Void> callback) {
        executorService.execute(() -> {
            movieDao.update(movie);
            if (callback != null) {
                callback.onComplete(null);
            }
        });
    }

    // Delete operations
    public void delete(Movie movie, RepositoryCallback<Void> callback) {
        executorService.execute(() -> {
            movieDao.delete(movie);
            if (callback != null) {
                callback.onComplete(null);
            }
        });
    }

    public void deleteAll(RepositoryCallback<Void> callback) {
        executorService.execute(() -> {
            movieDao.deleteAll();
            if (callback != null) {
                callback.onComplete(null);
            }
        });
    }

    // Query operations
    public void getAll(RepositoryCallback<List<Movie>> callback) {
        executorService.execute(() -> {
            List<Movie> movies = movieDao.getAll();
            if (callback != null) {
                callback.onComplete(movies);
            }
        });
    }

    public void getById(long movieId, RepositoryCallback<Movie> callback) {
        executorService.execute(() -> {
            Movie movie = movieDao.getById(movieId);
            if (callback != null) {
                callback.onComplete(movie);
            }
        });
    }

    public void getByTitle(String title, RepositoryCallback<List<Movie>> callback) {
        executorService.execute(() -> {
            List<Movie> movies = movieDao.getByTitle(title);
            if (callback != null) {
                callback.onComplete(movies);
            }
        });
    }

    public void getWatchedMovies(RepositoryCallback<List<Movie>> callback) {
        executorService.execute(() -> {
            List<Movie> movies = movieDao.getWatchedMovies();
            if (callback != null) {
                callback.onComplete(movies);
            }
        });
    }

    public void getUnwatchedMovies(RepositoryCallback<List<Movie>> callback) {
        executorService.execute(() -> {
            List<Movie> movies = movieDao.getUnwatchedMovies();
            if (callback != null) {
                callback.onComplete(movies);
            }
        });
    }

    public void getCount(RepositoryCallback<Integer> callback) {
        executorService.execute(() -> {
            int count = movieDao.getCount();
            if (callback != null) {
                callback.onComplete(count);
            }
        });
    }

    // Callback interface for async operations
    public interface RepositoryCallback<T> {
        void onComplete(T result);
    }

    // Shutdown executor when done
    public void shutdown() {
        executorService.shutdown();
    }
}
