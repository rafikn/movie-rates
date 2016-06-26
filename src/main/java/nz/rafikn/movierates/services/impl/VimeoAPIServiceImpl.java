package nz.rafikn.movierates.services.impl;

import nz.rafikn.movierates.model.Movie;
import nz.rafikn.movierates.model.RawSearch;
import nz.rafikn.movierates.model.SearchResponse;
import nz.rafikn.movierates.services.ConfigurationKeys;
import nz.rafikn.movierates.services.VimeoAPIService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glassfish.jersey.client.ClientConfig;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

/**
 * Created by rafik on 25/06/16.
 */
@Singleton
public class VimeoAPIServiceImpl implements VimeoAPIService {

    private static final Log logger = LogFactory.getLog(VimeoAPIService.class);
    private static final Client client = ClientBuilder.newClient(new ClientConfig());
    private final String api;
    private final String endpoint;
    private final String token;

    @Inject
    public VimeoAPIServiceImpl(@Named(ConfigurationKeys.SEARCH_API) String api,
                            @Named(ConfigurationKeys.SEARCH_ENDPOINT) String endpoint,
                            @Named(ConfigurationKeys.SEARCH_ACCESS_TOKEN) String token) {
        this.api = api;
        this.endpoint = endpoint;
        this.token = token;
    }


    @Override
    public SearchResponse search(String query) {
        return search(query, 1);

    }

    @Override
    public RawSearch rawSearch(Movie movie) {

        SearchResponse response;
        RawSearch result = new RawSearch(movie);

        int page = 0;
        while (page < 3) {
            response = search(movie.getSearchTerm(), ++page);
            result.getPages().add(response);
            if (response.getPaging().getNext() == null) {
                logger.info("Found less than 3 pages (150 results) for movie: " + movie.getTitle());
                break;
            }
        }
        return result;
    }


    @Override
    public SearchResponse search(String query, int page) {
        logger.debug("Searching for query:" + query + " page: " + page);

        return client.target(api)
                .path(endpoint)
                .queryParam("query", query)
                .queryParam("per_page", 50)
                .queryParam("page", page)
                .queryParam("fields", "uri,name,stats")
                .request(MediaType.APPLICATION_FORM_URLENCODED)
                .header("Authorization", "Bearer " + token)
                .header("Accept", MediaType.APPLICATION_JSON)
                .get(SearchResponse.class);
    }


}
