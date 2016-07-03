package nz.rafikn.movierates.akka;

import akka.actor.AbstractActor;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import nz.rafikn.movierates.model.MovieRecord;
import nz.rafikn.movierates.model.RawSearch;
import nz.rafikn.movierates.services.DynamoDBService;
import nz.rafikn.movierates.services.VimeoAPIService;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by rafik on 26/06/16.
 */
public class AggregateActor extends  AbstractActor {

    private final LoggingAdapter logger = Logging.getLogger(context().system(), this);

    public AggregateActor(DynamoDBService dynamoService, ActorSystem system) {

        receive(ReceiveBuilder.
                match(RawSearch.class, result -> {

                    // Aggregate views for search results
                    int hourTotal = VimeoAPIService.aggregate(result);
                    logger.debug("Aggregate results for: " + result.getMovie().getTitle() + " = " + hourTotal);

                    // Get previous hour views and build record
                    String current = DynamoDBService.timestampFormatter.format(Calendar.getInstance().getTime());
                    String previous = DynamoDBService.timestampFormatter.format(new Date(Calendar.getInstance().getTimeInMillis() - 60*60*1000));


                    int delta = hourTotal - dynamoService.getViewsForHourAndMovie(previous, result.getMovie().getTitle());
                    MovieRecord record = MovieRecord.build(current, hourTotal, delta, result.getMovie());

                    // Insert record
                    dynamoService.insertRecord(record);

                    // Stop this actor
                    system.stop(this.self());
                }).
                matchAny(o -> logger.info("received unknown message")).build()
        );
    }



}
