package nz.rafikn.movierates.services;

import com.google.inject.ImplementedBy;
import nz.rafikn.movierates.model.MovieRecord;
import nz.rafikn.movierates.services.impl.DynamoDBServiceImpl;

/**
 * Created by rafik on 26/06/16.
 */
@ImplementedBy(DynamoDBServiceImpl.class)
public interface DynamoDBService {

    /**
     * Create Movies table if it does not exist.
     *
     */
    void init();

    /**
     * Insert a new record/Override an existing record.
     *
     * @param record
     */
    void insertRecord(MovieRecord record);

    /**
     * Get aggregated hour views for a movie.
     *
     * @param hour
     * @param movieId
     * @return
     */
    int getViewsForHourAndMovie(int hour, int movieId);
}
