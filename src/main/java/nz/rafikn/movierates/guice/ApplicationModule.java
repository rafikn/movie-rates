package nz.rafikn.movierates.guice;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import nz.rafikn.movierates.services.ConfigurationKeys;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;

/**
 * Created by rafik on 26/06/16.
 */
public class ApplicationModule extends AbstractModule {

    private static final Log logger = LogFactory.getLog(ApplicationModule.class);
    private Configuration config;


    /**
     * Load application properties
     *
     * @param configFile
     */
    public ApplicationModule(String configFile) {
        Configurations configs = new Configurations();
        try {
            this.config = configs.properties(new File(configFile));

            logger.info("Read configuration values: " + config.size());
        } catch (ConfigurationException cex) {
            logger.error("Cannot load configuration file: " + configFile, cex);
        }
    }

    /**
     * Bind Configuration
     * Perform custom bindings (not done with @ImplementedBy)
     */
    protected void configure() {
        bindDBConfiguration();
        bindVimeoConfiguration();
        bindAWSConfiguration();

        // Add custom bindings here
        // ...
        // TODO: remove this
        //bind(VimeoAPIService.class).to(MockVimeoAPIServiceImpl.class).asEagerSingleton();
        //bind(UploadService.class).to(MockUploadServiceImpl.class).asEagerSingleton();
    }


    /**
     * Bind MySQL related configuration
     */
    private void bindDBConfiguration() {
        if (config != null) {
            bind(String.class)
                    .annotatedWith(Names.named(ConfigurationKeys.DB_URL))
                    .toInstance(config.getString(ConfigurationKeys.DB_URL));
            bind(String.class)
                    .annotatedWith(Names.named(ConfigurationKeys.DB_USER))
                    .toInstance(config.getString(ConfigurationKeys.DB_USER));
            bind(String.class)
                    .annotatedWith(Names.named(ConfigurationKeys.DB_PASSWORD))
                    .toInstance(config.getString(ConfigurationKeys.DB_PASSWORD));
        }

    }


    /**
     * Bind Vimeo related Configuration
     */
    private void bindVimeoConfiguration() {
        bind(String.class)
                .annotatedWith(Names.named(ConfigurationKeys.SEARCH_API))
                .toInstance(config.getString(ConfigurationKeys.SEARCH_API));
        bind(String.class)
                .annotatedWith(Names.named(ConfigurationKeys.SEARCH_ENDPOINT))
                .toInstance(config.getString(ConfigurationKeys.SEARCH_ENDPOINT));
        bind(String.class)
                .annotatedWith(Names.named(ConfigurationKeys.SEARCH_ACCESS_TOKEN))
                .toInstance(config.getString(ConfigurationKeys.SEARCH_ACCESS_TOKEN));
    }


    /**
     * Bind AWS related configuration
     */
    private void bindAWSConfiguration() {
        bind(String.class)
                .annotatedWith(Names.named(ConfigurationKeys.AWS_DYNAMODB_URL))
                .toInstance(config.getString(ConfigurationKeys.AWS_DYNAMODB_URL));
        bind(String.class)
                .annotatedWith(Names.named(ConfigurationKeys.AWS_DYNAMODB_TABLE))
                .toInstance(config.getString(ConfigurationKeys.AWS_DYNAMODB_TABLE));

        bind(String.class)
                .annotatedWith(Names.named(ConfigurationKeys.AWS_S3_BUCKET))
                .toInstance(config.getString(ConfigurationKeys.AWS_S3_BUCKET));

        // Set AWS Credentials as system properties for the SDK
        logger.info("Setting AWS Credentials...");
        System.setProperty("aws.accessKeyId", config.getString(ConfigurationKeys.AWS_KEY_ID));
        System.setProperty("aws.secretKey", config.getString(ConfigurationKeys.AWS_KEY_SECRET));
    }

}
