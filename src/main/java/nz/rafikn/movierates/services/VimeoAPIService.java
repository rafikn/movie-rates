package nz.rafikn.movierates.services;

import com.google.inject.ImplementedBy;
import nz.rafikn.movierates.model.Movie;
import nz.rafikn.movierates.model.RawSearch;
import nz.rafikn.movierates.model.SearchResponse;
import nz.rafikn.movierates.model.SearchResult;
import nz.rafikn.movierates.services.impl.VimeoAPIServiceImpl;


/**
 * Created by rafik on 25/06/16.
 */
@ImplementedBy(VimeoAPIServiceImpl.class)
public interface VimeoAPIService {

    /**
     * Search api based on a query string.
     *
     * @param query
     * @return
     */
    SearchResponse search(String query);

    /**
     * Search api based on a query string and get a specific result page.
     *
     * @param query
     * @param page
     * @return
     */
    SearchResponse search(String query, int page);

    /**
     * Return all pages for a movie search.
     *
     * @param movie
     * @return
     */
    RawSearch rawSearch(Movie movie);

    /**
     * Sum all search results views.
     *
     * @param search
     * @return
     */
    static int aggregate(RawSearch search){
        return search.getPages().stream().flatMapToInt(page -> page.getData().stream().mapToInt(SearchResult::getPlays)).sum();
    }
}
