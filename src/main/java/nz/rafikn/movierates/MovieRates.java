package nz.rafikn.movierates;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.RoundRobinPool;
import com.amazonaws.services.devicefarm.model.Upload;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import nz.rafikn.movierates.akka.SearchActor;
import nz.rafikn.movierates.akka.UploadActor;
import nz.rafikn.movierates.guice.ApplicationModule;
import nz.rafikn.movierates.model.*;
import nz.rafikn.movierates.services.DynamoDBService;
import nz.rafikn.movierates.services.MySQLService;
import nz.rafikn.movierates.services.UploadService;
import nz.rafikn.movierates.services.VimeoAPIService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import scala.concurrent.duration.Duration;

import java.util.Collection;
import java.util.concurrent.TimeUnit;


/**
 * Created by rafik on 25/06/16.
 */
public class MovieRates {


    private static final Log logger = LogFactory.getLog(MovieRates.class);
    private static final ActorSystem actorSystem;
    private static VimeoAPIService api;
    private static DynamoDBService dynamo;


    private static UploadService uploader;

    private static String config = "my-dispatcher {\n" +
            "  # Dispatcher is the name of the event-based dispatcher\n" +
            "  type = Dispatcher\n" +
            "  # What kind of ExecutionService to use\n" +
            "  executor = \"fork-join-executor\"\n" +
            "  # Configuration for the fork join pool\n" +
            "  fork-join-executor {\n" +
            "    # Min number of threads to cap factor-based parallelism number to\n" +
            "    parallelism-min = 100\n" +
            "    # Parallelism (threads) ... ceil(available processors * factor)\n" +
            "    parallelism-factor = 2.0\n" +
            "    # Max number of threads to cap factor-based parallelism number to\n" +
            "    parallelism-max = 150\n" +
            "  }\n" +
            "  # Throughput defines the maximum number of messages to be\n" +
            "  # processed per actor before the thread jumps to the next actor.\n" +
            "  # Set to 1 for as fair as possible.\n" +
            "  throughput = 100\n" +
            "}";

    static {
        actorSystem = ActorSystem.create("my-system", ConfigFactory.parseString(config));
    }

    /**
     * Init the DynamoDB.
     *
     * Init the search router.
     *
     * Create a search actor and start the rating process per movie
     * (pass the api and dynamo services to child actors).
     *
     * @param args
     */
    public static void main(String[] args) {
        logger.info("MoviesRates 1.0");


        String conf = System.getProperty("config.file");
        if (conf == null || conf.isEmpty()) {
            logger.warn("No configuration file provided, using default config.properties");
            conf = "config.properties";
        }

        // Init App
        Injector app = Guice.createInjector(new ApplicationModule(conf));
        MySQLService mysql = app.getInstance(MySQLService.class);

        dynamo = app.getInstance(DynamoDBService.class);
        api = app.getInstance(VimeoAPIService.class);

        uploader = app.getInstance(UploadService.class);

        Collection<Movie> movies = mysql.getMovies();

        // Init DynamoDB
        dynamo.init();
        uploader.init(movies);

        // Schedule search
        actorSystem.scheduler().schedule(
                Duration.create(0, TimeUnit.MILLISECONDS), // Delay
                Duration.create(60, TimeUnit.MINUTES), // Frequency
                () -> movies.forEach(movie -> handleMovie(movie)), // Runnable
                actorSystem.dispatcher() // Akka System
        );

    }

    private static void handleMovie(Movie movie) {
        ActorRef searchActor = actorSystem.actorOf(Props.create(SearchActor.class, api, dynamo, uploader, actorSystem).withDispatcher("my-dispatcher"));

        searchActor.tell(movie, ActorRef.noSender());
    }

}
