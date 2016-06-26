package nz.rafikn.movierates.services.impl;

import nz.rafikn.movierates.model.Movie;
import nz.rafikn.movierates.services.ConfigurationKeys;
import nz.rafikn.movierates.services.MySQLService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by rafik on 25/06/16.
 */
@Singleton
public class MySQLServiceImpl implements MySQLService {

    private static final String SELECT_ALL_MOVIES = "SELECT * FROM movie_search";
    private static final Log logger = LogFactory.getLog(MySQLServiceImpl.class);


    private final String url;
    private final String username;
    private final String password;

    /**
     * Inject MySQL config
     *
     * @param url
     * @param username
     * @param password
     */
    @Inject
    MySQLServiceImpl(@Named(ConfigurationKeys.DB_URL) String url,
                      @Named(ConfigurationKeys.DB_USER) String username,
                      @Named(ConfigurationKeys.DB_PASSWORD) String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }


    /**
     * Get all movies from database.
     *
     * @return Collection of all movies in the database
     */
    public Collection<Movie> getMovies() {

        Collection<Movie> movies = new ArrayList<>();

        PreparedStatement stmt = null;
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, username, password);

            stmt = connection.prepareStatement(SELECT_ALL_MOVIES);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Movie result = new Movie();
                result.setId(rs.getInt("id"));
                result.setTitle(rs.getString("movie_title"));
                result.setSearchTerm(rs.getString("search_term"));
                movies.add(result);
            }

        } catch (SQLException ex) {
            logger.error("Cannot load movies from database", ex);
        } finally{

            try{
                if(stmt!=null)
                    stmt.close();
            }catch(SQLException e){
            }

            // No need for db connection
            try{
                if(connection!=null)
                    connection.close();
            }catch(SQLException ex){
                logger.error("Cannot close database connection", ex);
            }//end finally try
        }

        return movies;
    }
}
