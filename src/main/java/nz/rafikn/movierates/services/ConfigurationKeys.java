package nz.rafikn.movierates.services;

/**
 * Created by rafik on 25/06/16.
 */
public interface ConfigurationKeys {

    /**
     * movies database configuration keys
     */
    String DB_URL = "database.url";
    String DB_USER = "database.user";
    String DB_PASSWORD = "database.password";

    String SEARCH_API = "vimeo.api.url";
    String SEARCH_ENDPOINT = "vimeo.api.endpoint";
    String SEARCH_ACCESS_TOKEN = "vimeo.api.access_token";
    /**
     * AWS Configuration keys
     */
    String AWS_KEY_ID = "aws.key.id";
    String AWS_KEY_SECRET = "aws.key.secret";
    String AWS_S3_BUCKET = "aws.s3.bucket";
    String AWS_DYNAMODB_URL = "aws.dynamodb.url";
    String AWS_DYNAMODB_TABLE = "aws.dynamodb.table";
}
