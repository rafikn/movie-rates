package nz.rafikn.movierates.services.impl;

import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressEventType;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import nz.rafikn.movierates.model.Movie;
import nz.rafikn.movierates.model.RawSearch;
import nz.rafikn.movierates.services.ConfigurationKeys;
import nz.rafikn.movierates.services.UploadService;
import nz.rafikn.movierates.util.IOUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.File;
import java.util.*;

/**
 * Created by rafik on 2/07/16.
 */
@Singleton
public class S3UploadService implements UploadService{

    private static final Log logger = LogFactory.getLog(S3UploadService.class);


    private final AmazonS3Client client;
    private final String bucket;

    private final Map<Movie, RawSearch> file = new HashMap<>();
    private final Collection<Movie> movies = new ArrayList<>();


    /**
     * Initialise S3 client
     *
     * @param s3bucket
     */
    @Inject
    public S3UploadService(@Named(ConfigurationKeys.AWS_S3_BUCKET) String s3bucket) {
        this.client = new AmazonS3Client();
        this.bucket = s3bucket;
    }

    /**
     * Initialise list of movies
     *
     * @param movies
     */
    @Override
    public void init(Collection<Movie> movies) {
        this.movies.addAll(movies);
    }


    /**
     * Process a search result for a movie.
     *
     * 1. Store result in memory
     * 2. If all results present, write memory to file and upload file to S3
     *
     * @param movie
     * @param raw
     */
    @Override
    public synchronized void process(Movie movie, RawSearch raw) {

        file.putIfAbsent(movie, raw);

        if (isComplete(movies, file)) {
            upload(file);
        }
    }

    /**
     * Simple test to check if all movies have a corresponding search result
     *
     * @param movies
     * @param file
     * @return
     */
    private synchronized boolean isComplete(Collection<Movie> movies, Map<Movie, RawSearch> file) {
        return !movies.stream().anyMatch(movie -> file.get(movie) == null);
    }



    /**
     * Multipart upload of the raw file to S3
     * see <url>http://docs.aws.amazon.com/AmazonS3/latest/dev/llJavaUploadFile.html</url>
     *
     * @param content
     *          Map<Movie, RawSearh> containing raw search results per movie
     *
     */
    private synchronized void upload(Map<Movie, RawSearch> content) {
        // Create file key
        String key = fileFormatter.format(Calendar.getInstance().getTime());
        String filename = key + ".json";

        File file = IOUtil.writeFile(content, filename);
        content.clear();

        String keyName = key  + "/" + filename;
        TransferManager tm = new TransferManager(client);

        // For more advanced uploads, you can create a request object
        // and supply additional request parameters (ex: progress listeners,
        // canned ACLs, etc.)
        PutObjectRequest request = new PutObjectRequest(
                bucket, keyName, file);

        // You can ask the upload for its progress, or you can
        // add a ProgressListener to your request to receive notifications
        // when bytes are transferred.
        request.setGeneralProgressListener(progressEvent -> handleProgress(progressEvent, keyName, content));

        // TransferManager processes all transfers asynchronously,
        // so this call will return immediately.
        Upload upload = tm.upload(request);


    }

    private void handleProgress(ProgressEvent event, String keyName, Map<Movie, RawSearch> content) {
        if (event.getEventType().equals(ProgressEventType.TRANSFER_COMPLETED_EVENT)) {
            logger.info("Finished uploading file to S3 " + keyName);
            content.clear();
        }
    }
}
