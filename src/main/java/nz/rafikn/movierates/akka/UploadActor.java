package nz.rafikn.movierates.akka;

import akka.actor.AbstractActor;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import nz.rafikn.movierates.model.Movie;
import nz.rafikn.movierates.model.RawSearch;
import nz.rafikn.movierates.services.UploadService;

import java.util.*;

/**
 * Created by rafik on 3/07/16.
 */
public class UploadActor extends  AbstractActor {

    private final LoggingAdapter logger = Logging.getLogger(context().system(), this);

    public UploadActor(UploadService uploader, ActorSystem system) {

        receive(ReceiveBuilder.
                match(Map.Entry.class, result -> {

                    if (result.getKey() instanceof Movie && result.getValue() instanceof RawSearch) {
                        uploader.process((Movie)result.getKey(), (RawSearch)result.getValue());
                    }

                    system.stop(this.self());
                }).
                matchAny(o -> logger.info("received unknown message")).build()
        );

    }

}
