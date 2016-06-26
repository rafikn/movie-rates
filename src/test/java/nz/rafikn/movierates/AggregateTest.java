package nz.rafikn.movierates;

import nz.rafikn.movierates.model.Movie;
import nz.rafikn.movierates.model.RawSearch;
import nz.rafikn.movierates.model.SearchResponse;
import nz.rafikn.movierates.model.SearchResult;
import nz.rafikn.movierates.services.VimeoAPIService;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;


/**
 * Created by rafik on 26/06/16.
 */
public class AggregateTest {

    @Test
    public void testAggregate() {
        // 150 = 10 per result, 5 results per page, 3 pages per response.
        assertEquals(VimeoAPIService.aggregate(mockSearch()), 150);
    }



    private RawSearch mockSearch() {

        RawSearch raw = new RawSearch();

        raw.setMovie(mockMovie());

        for (int page = 1; page < 4; page++) {
            SearchResponse response = new SearchResponse();
            response.setTotal(15);
            response.setPage(1);
            response.setPerPage(5);
            response.setData(mockSearchData());

            raw.getPages().add(response);
        }

        return raw;
    }

    private Movie mockMovie() {
        Movie movie = new Movie();
        movie.setId(1);
        movie.setTitle("Test Movie");
        movie.setSearchTerm("Search Term");
        return movie;
    }


    private Collection<SearchResult> mockSearchData() {
        List<SearchResult> results = new ArrayList<>();

        Map<String, Object> stats = new HashMap<>();

        for (int i =0; i<5; i++) {
            SearchResult result = new SearchResult();
            result.setName("Test Movie");
            result.setUri("/video/test");

            stats.put("plays", 10);
            result.setStats(stats);
            results.add(result);
        }
        return results;
    }

}
