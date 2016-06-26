package nz.rafikn.movierates.services;

import com.google.inject.ImplementedBy;
import nz.rafikn.movierates.model.Movie;
import nz.rafikn.movierates.services.impl.MySQLServiceImpl;

import java.util.Collection;

/**
 * Created by rafik on 25/06/16.
 */
@ImplementedBy(MySQLServiceImpl.class)
public interface MySQLService {

    /**
     * Get all movies from database.
     *
     * @return Collection of all movies in the database.
     */
    Collection<Movie> getMovies();
}
