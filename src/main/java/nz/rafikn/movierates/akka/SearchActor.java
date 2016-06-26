package nz.rafikn.movierates.akka;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import nz.rafikn.movierates.model.Movie;
import nz.rafikn.movierates.model.RawSearch;
import nz.rafikn.movierates.services.DynamoDBService;
import nz.rafikn.movierates.services.VimeoAPIService;


/**
 * Created by rafik on 26/06/16.
 */
public class SearchActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    public SearchActor(VimeoAPIService api, DynamoDBService dynamo, ActorSystem system) {
        receive(ReceiveBuilder.
                match(Movie.class, m -> {

                    // Perform search
                    RawSearch result = api.rawSearch(m);

                    // Do the aggregation
                    final ActorRef aggregateActor = system.actorOf(Props.create(AggregateActor.class, dynamo, system), "aggregate-" + m.getId());
                    aggregateActor.tell(result, this.self());

                    // TODO: obtain an ActorRef to the S3 concatenate actor
//                    final ActorRef concatenateActor = system.actorFor("s3-concatenateActor");
//                    concatenateActor.tell(result, this.self());

                }).
                matchAny(o -> log.info("received unknown message")).build()
        );
    }
}
