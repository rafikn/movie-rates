package nz.rafikn.movierates.model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by rafik on 26/06/16.
 */
public class RawSearch {

    private Movie movie;
    private Collection<SearchResponse> pages = new ArrayList<>();

    public RawSearch() {
    }

    public RawSearch(Movie movie) {
        this.movie = movie;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public Collection<SearchResponse> getPages() {
        return pages;
    }

    public void setPages(Collection<SearchResponse> pages) {
        this.pages = pages;
    }

    @Override
    public String toString() {
        return "RawSearch{" +
                "movie=" + movie +
                ", pages=" + pages +
                '}';
    }
}
