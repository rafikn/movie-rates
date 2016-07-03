package nz.rafikn.movierates.services;

import com.google.inject.ImplementedBy;
import nz.rafikn.movierates.model.MovieRecord;
import nz.rafikn.movierates.services.impl.DynamoDBServiceImpl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by rafik on 26/06/16.
 */
@ImplementedBy(DynamoDBServiceImpl.class)
public interface DynamoDBService {

    DateFormat timestampFormatter = new SimpleDateFormat("yyyy-MM-dd.HH");

    /**
     * Create Movies table if it does not exist.
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
     * @param title
     * @param timestamp
     * @return
     */
    int getViewsForHourAndMovie(String title, String timestamp);

}
