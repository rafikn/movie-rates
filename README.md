# movie-rates
Small Java app that queries Vimeo API for a movie trailer and aggregates the total number of views of the results.
Read the list of target movies from a mysql database, query the Vimeo API for each movie, then push the results (per hour)
to a remote DynamoDB database, and concatenate and store the raw json output (from the Vimeo API) in S3.

### Configuration

You need to provide a file with configuration properties for mysql, dynamodb and s3.

    database.url=
    database.user=
    database.password=

    vimeo.api.url=
    vimeo.api.endpoint=
    vimeo.api.access_token=

    aws.key.id=
    aws.key.secret=
    aws.s3.url=
    aws.dynamodb.url=
    aws.dynamodb.table=


Then pass the the path to the configuration file as a system property

```sh
$ java ... -Dconfig.file=/path/to/config.properties ...
```
### Work Flow (With Akka)

This app uses [Akka](http://akka.io) for concurrency.

First, a router actor is configured to speard the search load over xx SearchActor.
Every hour, a movie is sent to a SearchActor for querying. Every search is performed in a seperate actor instance.
The SearchActor then creates a reference to an AggregateActor and passes the search result to it.

The AggregateActor aggregates the views of the result hits, compares to the previous hour results and insert in a new record the DynamoDB table. Once the insert is done, the AggregateActor stops.

**TODO** The SearchActor also passes the result to a ConcatenteActor that has been previously created. The ConcatenateActor adds the current hour raw result to a local temp file and pushs it to S3 once it obtains the results of all SearchActors.

[image](/docs/diagram.html)