package nz.rafikn.movierates;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.RoundRobinPool;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import nz.rafikn.movierates.akka.SearchActor;
import nz.rafikn.movierates.guice.ApplicationModule;
import nz.rafikn.movierates.model.*;
import nz.rafikn.movierates.services.DynamoDBService;
import nz.rafikn.movierates.services.MySQLService;
import nz.rafikn.movierates.services.VimeoAPIService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;


/**
 * Created by rafik on 25/06/16.
 */
public class MovieRates {


    private static final Log logger = LogFactory.getLog(MovieRates.class);
    private static final ActorSystem actorSystem = ActorSystem.create("MovieRates");


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
        DynamoDBService dynamo = app.getInstance(DynamoDBService.class);
        MySQLService mysql = app.getInstance(MySQLService.class);
        VimeoAPIService api = app.getInstance(VimeoAPIService.class);

        // Init DynamoDB
        dynamo.init();

        ActorRef searchRouter =
                actorSystem.actorOf(new RoundRobinPool(100).props(Props.create(SearchActor.class, api, dynamo, actorSystem)),
                        "search-router");

        // Schedule search
        actorSystem.scheduler().schedule(
                Duration.create(0, TimeUnit.MILLISECONDS), // Delay
                Duration.create(1, TimeUnit.MINUTES), // Frequency
                () -> mysql.getMovies().forEach(movie -> searchRouter.tell(movie, ActorRef.noSender())), // Runnable
                actorSystem.dispatcher() // Akka System
        );

    }

}
