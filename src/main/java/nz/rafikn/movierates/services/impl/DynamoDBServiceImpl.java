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
import java.util.Arrays;

/**
 * Created by rafik on 26/06/16.
 */
@Singleton
public class DynamoDBServiceImpl implements DynamoDBService {


    private static final Log logger = LogFactory.getLog(DynamoDBServiceImpl.class);

    private final AmazonDynamoDBClient client;
    private final DynamoDB dynamoDB;
    private final String tableName;
    private Table table;

    /**
     * Prepare DynamoDB Client.
     *
     * @param dynamoDBEndpoint
     * @param tableName
     */
    @Inject
    public DynamoDBServiceImpl(@Named(ConfigurationKeys.AWS_DYNAMODB_URL) String dynamoDBEndpoint,
                               @Named(ConfigurationKeys.AWS_DYNAMODB_TABLE) String tableName) {
        this.client = new AmazonDynamoDBClient();
        this.dynamoDB = new DynamoDB(client);
        this.tableName = tableName;
        this.table = dynamoDB.getTable(tableName);
    }

    /**
     * Create Movies table if it does not exist.
     */
    @Override
    public void init() {

        TableDescription description = table.getDescription();

        if (description != null) {
            logger.info("Connected to DynamoDB, found table " + tableName);
            logger.info("Table Status: " + table.getDescription().getTableStatus());
        } else {
            try {
                logger.info("Attempting to create table " + tableName + "; please wait...");
                table = dynamoDB.createTable(tableName,
                        Arrays.asList(
                                new KeySchemaElement(MovieRecord.PARTITION_KEY, KeyType.HASH),  //Partition key
                                new KeySchemaElement(MovieRecord.SORT_KEY, KeyType.RANGE)), //Sort key
                        Arrays.asList(
                                new AttributeDefinition(MovieRecord.PARTITION_KEY, ScalarAttributeType.S),
                                new AttributeDefinition(MovieRecord.SORT_KEY, ScalarAttributeType.S)),
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

        String partitionKey = record.getTitle();

        try {
            logger.info("Adding a new record to " + tableName + ": " + record);
            PutItemOutcome outcome = table.putItem(new Item()
                    .withPrimaryKey(MovieRecord.PARTITION_KEY, partitionKey, MovieRecord.SORT_KEY, record.getTimestamp())
                    .withString("source", record.getSource())
                    .withString("variable", record.getVariable())
                    .withString("query_term", record.getSearchTerm())
                    .withInt("hour", record.getHour())
                    .withInt("hour_total", record.getHourTotal())
                    .withInt("delta_last_hour", record.getDeltaLastHour())
                    .withLong("creation_timestamp", record.getCreationTimestamp())
            );

            logger.debug("PutItem succeeded:\n" + outcome.getPutItemResult());

        } catch (Exception e) {
            logger.error("Unable to add item: " + record, e);
            System.err.println(e.getMessage());
        }
    }

    /**
     * Get aggregated hour views for a movie.
     *
     * @param title
     * @param timestamp
     * @return
     */
    @Override
    public int getViewsForHourAndMovie(String title, String timestamp) {
        String partitionKey = title;


        GetItemSpec spec = new GetItemSpec()
                .withPrimaryKey(MovieRecord.PARTITION_KEY, partitionKey, MovieRecord.SORT_KEY, timestamp);

        try {
            Item outcome = table.getItem(spec);
            logger.debug("GetItem succeeded: " + outcome);

            if (outcome == null) {
                logger.debug("Found no previous record for movie: " + title + " and timestamp: " + timestamp);
                return 0;
            }
            return outcome.getInt(MovieRecord.HOUR_TOTAL);

        } catch (Exception e) {
            logger.error("Unable to read hour_total for title: " + partitionKey + " timestamp: " + timestamp, e);
        }

        return 0;
    }




}
