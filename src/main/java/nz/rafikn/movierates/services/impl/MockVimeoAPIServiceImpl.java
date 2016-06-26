package nz.rafikn.movierates.services.impl;

import nz.rafikn.movierates.model.Movie;
import nz.rafikn.movierates.model.RawSearch;
import nz.rafikn.movierates.model.SearchResponse;
import nz.rafikn.movierates.model.SearchResult;
import nz.rafikn.movierates.services.VimeoAPIService;

import java.util.*;

/**
 * Created by rafik on 26/06/16.
 */
public class MockVimeoAPIServiceImpl implements VimeoAPIService {

    private final Random rand = new Random(Calendar.getInstance().getTimeInMillis());

    @Override
    public SearchResponse search(String query) {
        return null;
    }

    @Override
    public SearchResponse search(String query, int page) {
        return null;
    }

    @Override
    public RawSearch rawSearch(Movie movie) {
        return mockSearch(movie);
    }


    private RawSearch mockSearch(Movie movie) {

        RawSearch raw = new RawSearch(movie);

        for (int page = 1; page < 4; page++) {
            raw.getPages().add(mockSearchResponse(page));
        }

        return raw;
    }

    private SearchResponse mockSearchResponse(int page) {
        SearchResponse response = new SearchResponse();
        response.setTotal(150);
        response.setPage(page);
        response.setPerPage(50);
        response.setData(mockSearchData());
        return response;
    }

    private Collection<SearchResult> mockSearchData() {
        List<SearchResult> results = new ArrayList<>();

        Map<String, Object> stats = new HashMap<>();

        for (int i =0; i<50; i++) {
            SearchResult result = new SearchResult();
            result.setName("Test Movie" + i);
            result.setUri("/video/test/" + i);

            stats.put("plays", 100 + rand.nextInt(10000));
            result.setStats(stats);
            results.add(result);
        }
        return results;
    }
}
