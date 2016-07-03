package nz.rafikn.movierates.services.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import nz.rafikn.movierates.model.Movie;
import nz.rafikn.movierates.model.RawSearch;
import nz.rafikn.movierates.services.UploadService;
import nz.rafikn.movierates.util.IOUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.inject.Singleton;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by rafik on 2/07/16.
 */
@Singleton
public class MockUploadServiceImpl implements UploadService {

    private static final Log logger = LogFactory.getLog(MockUploadServiceImpl.class);

    private final Collection<Movie> movies = new ArrayList<>();
    private final Map<Movie, RawSearch> file = new HashMap<>();


    //private final Map<Integer, Map<Movie, RawSearch>> state = new HashMap<Integer, Map<Movie, RawSearch>>();
    //private int currentState;

    private final Gson gson = new GsonBuilder().create();

    @Override
    public void init(Collection<Movie> movies) {
        this.movies.addAll(movies);
        this.file.clear();

        //this.state.clear();
        //this.currentState = 0;

        // Initial state
        //this.state.put(currentState, this.file);
    }

    @Override
    public synchronized void process(Movie movie, RawSearch raw) {

        file.putIfAbsent(movie, raw);


        boolean isComplete = isComplete(movies, file);

        logger.debug("processing movie " + movie.getId() + " file complete: " + isComplete);
        if (isComplete) {
            upload(file);
        }
    }

    private synchronized boolean isComplete(Collection<Movie> movies, Map<Movie, RawSearch> file) {
        return !movies.stream().anyMatch(movie -> file.get(movie) == null);
    }



    private synchronized void upload(Map<Movie, RawSearch> file) {
        String key = fileFormatter.format(Calendar.getInstance().getTime());

        IOUtil.writeFile(file, key + ".json");

        file.clear();
    }

}
