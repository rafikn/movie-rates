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

/**
 * Created by rafik on 26/06/16.
 */
public class AggregateActor extends  AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    public AggregateActor(DynamoDBService dynamoService, ActorSystem system) {

        receive(ReceiveBuilder.
                match(RawSearch.class, result -> {

                    // Aggregate views for search results
                    int hourTotal = VimeoAPIService.aggregate(result);
                    log.debug("Aggregate results for: " + result.getMovie().getTitle() + " = " + hourTotal);

                    // Get previous hour views and build record
                    int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                    int lastHourTotal = dynamoService.getViewsForHourAndMovie(hour, result.getMovie().getId());
                    MovieRecord record = buildRecord(hour, hourTotal, lastHourTotal, result);

                    // Insert record
                    dynamoService.insertRecord(record);

                    // Stop this actor
                    system.stop(this.self());
                }).
                matchAny(o -> log.info("received unknown message")).build()
        );
    }

    private MovieRecord buildRecord(int hour, int hourTotal, int lastHourTotal, RawSearch result) {


        MovieRecord record = new MovieRecord();

        record.setMovieId(result.getMovie().getId());
        record.setMovieTitle(result.getMovie().getTitle());
        record.setSearchTerm(result.getMovie().getSearchTerm());
        record.setTimestamp(Calendar.getInstance().getTimeInMillis());
        record.setHour(hour);
        record.setHourTotal(hourTotal);
        record.setDeltaLastHour(hourTotal - lastHourTotal);

        return record;
    }

}
