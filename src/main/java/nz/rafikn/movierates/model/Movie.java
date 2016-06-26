package nz.rafikn.movierates.model;

/**
 * Created by rafik on 26/06/16.
 */
public class Movie {

    private int id;
    private String title;
    private String searchTerm;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
