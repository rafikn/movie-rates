package nz.rafikn.movierates.services.impl;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.model.*;
import nz.rafikn.movierates.model.MovieRecord;
import nz.rafikn.movierates.services.ConfigurationKeys;
import nz.rafikn.movierates.services.DynamoDBService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

/**
 * Created by rafik on 26/06/16.
 */
@Singleton
public class DynamoDBServiceImpl implements DynamoDBService {


    private static final Log logger = LogFactory.getLog(DynamoDBServiceImpl.class);
    private static final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    private final AmazonDynamoDBClient client;
    private final DynamoDB dynamoDB;
    private final String baseName;

    /**
     * Prepare DynamoDB Client.
     *
     * @param dynamoDBEndpoint
     * @param baseName
     */
    @Inject
    public DynamoDBServiceImpl(@Named(ConfigurationKeys.AWS_DYNAMODB_URL) String dynamoDBEndpoint,
                               @Named(ConfigurationKeys.AWS_DYNAMODB_TABLE) String baseName) {
        this.client = new AmazonDynamoDBClient().withEndpoint(dynamoDBEndpoint);
        this.dynamoDB = new DynamoDB(client);
        this.baseName = baseName;
    }

    /**
     * Create Movies table if it does not exist.
     *
     */
    @Override
    public void init() {

        String tableName = getCurrentTable();
        Table table = dynamoDB.getTable(tableName);
        TableDescription description = table.getDescription();

        logger.info("desc " + table);

        if (description != null) {
            logger.info("Connected to DynamoDB, found table " + tableName);
            logger.info("Table Status: " + dynamoDB.getTable(tableName).getDescription().getTableStatus());
        } else {
            try {
                logger.info("Attempting to create table " + tableName + "; please wait...");
                table = dynamoDB.createTable(tableName,
                        Arrays.asList(
                                new KeySchemaElement(MovieRecord.PARTITION_KEY, KeyType.HASH),  //Partition key
                                new KeySchemaElement(MovieRecord.SORT_KEY, KeyType.RANGE)), //Sort key
                        Arrays.asList(
                                new AttributeDefinition(MovieRecord.PARTITION_KEY, ScalarAttributeType.N),
                                new AttributeDefinition(MovieRecord.SORT_KEY, ScalarAttributeType.N)),
                        new ProvisionedThroughput(10L, 10L));
                table.waitForActive();
                logger.info("Success.  Table status: " + table.getDescription().getTableStatus());

            } catch (ResourceInUseException ex) {
                logger.info("Found table " + tableName);
            } catch (Exception e) {
                logger.error("Unable to create table Movies", e);
            }
        }
    }

    /**
     * Insert a new record/Override an existing record (?).
     *
     * @param record
     */
    @Override
    public void insertRecord(MovieRecord record) {

        String tableName = getCurrentTable();
        Table table = dynamoDB.getTable(tableName);

        try {
            logger.info(Thread.currentThread() + " Adding a new record to " + tableName + ": " + record);
            PutItemOutcome outcome = table.putItem(new Item()
                    .withPrimaryKey(MovieRecord.PARTITION_KEY, record.getHour(), MovieRecord.SORT_KEY, record.getMovieId())
                    .withMap("info", record.getMap()));

            logger.debug("PutItem succeeded:\n" + outcome.getPutItemResult());

        } catch (Exception e) {
            logger.error("Unable to add item: " + record);
            System.err.println(e.getMessage());
        }
    }

    /**
     * Get aggregated hour views for a movie.
     *
     * @param hour
     * @param movieId
     * @return
     */
    @Override
    public int getViewsForHourAndMovie(int hour, int movieId) {
        Table table = dynamoDB.getTable(getCurrentTable());

        GetItemSpec spec = new GetItemSpec()
                .withPrimaryKey(MovieRecord.PARTITION_KEY, hour, MovieRecord.SORT_KEY, movieId);

        try {
            Item outcome = table.getItem(spec);
            logger.debug("GetItem succeeded: " + outcome);

            if (outcome == null) {
                logger.debug("Found no previous record for movie: " + movieId + " and hour: " + hour);
                return 0;
            }
            return ((BigDecimal) outcome.getMap("info").get(MovieRecord.HOUR_TOTAL)).intValue();

        } catch (Exception e) {
            logger.error("Unable to read hour_total for item: " + hour + " " + movieId, e);
        }

        return 0;
    }

    /**
     * Build tableName based on current date.
     *
     * @return
     */
    private String getCurrentTable() {
        return baseName + "-" + formatter.format(Calendar.getInstance().getTime());
    }


}
