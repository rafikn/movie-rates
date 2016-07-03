package nz.rafikn.movierates.services;

import com.google.inject.ImplementedBy;
import nz.rafikn.movierates.model.Movie;
import nz.rafikn.movierates.model.RawSearch;
import nz.rafikn.movierates.services.impl.S3UploadService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;

/**
 * Created by rafik on 2/07/16.
 */
@ImplementedBy(S3UploadService.class)
public interface UploadService {

    void init(Collection<Movie> movies);

    void process(Movie movie, RawSearch raw);

    DateFormat fileFormatter = new SimpleDateFormat("HH.dd-MM-yyyy");

}
