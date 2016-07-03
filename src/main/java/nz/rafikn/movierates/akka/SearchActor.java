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
import nz.rafikn.movierates.services.UploadService;
import nz.rafikn.movierates.services.VimeoAPIService;

import java.util.Map;


/**
 * Created by rafik on 26/06/16.
 */
public class SearchActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    public SearchActor(VimeoAPIService api, DynamoDBService dynamo, UploadService uploader, ActorSystem system) {
        receive(ReceiveBuilder.
                match(Movie.class, m -> {

                    // Perform search
                    RawSearch result = api.rawSearch(m);

                    // Do the aggregation
                    final ActorRef aggregateActor = system.actorOf(Props.create(AggregateActor.class, dynamo, system), "aggregate-" + m.getId());
                    aggregateActor.tell(result, this.self());

                    // Add to uploader
                    sendToUploader(m, result, uploader, system);

                    system.stop(this.self());
                }).
                matchAny(o -> log.info("received unknown message")).build()
        );
    }


    private void sendToUploader(Movie movie, RawSearch raw, UploadService uploader, ActorSystem system) {
        final ActorRef uploadActor = system.actorOf(Props.create(UploadActor.class, uploader, system));

        Map.Entry msg = new Map.Entry() {
            @Override
            public Object getKey() {
                return movie;
            }

            @Override
            public Object getValue() {
                return raw;
            }

            @Override
            public Object setValue(Object value) {
                return value;
            }
        };

        uploadActor.tell(msg, this.self());
    }
}
