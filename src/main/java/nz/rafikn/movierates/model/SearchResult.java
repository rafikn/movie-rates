package nz.rafikn.movierates.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;

/**
 * Created by rafik on 26/06/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResult {

    private String uri;
    private String name;
    private Map<String, Object> stats;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getStats() {
        return stats;
    }

    public void setStats(Map<String, Object> stats) {
        this.stats = stats;
    }


    @JsonIgnore
    private transient int plays;

    public int getPlays() {
        if (stats.get("plays") != null) {
            return (int) stats.get("plays");
        } else {
            return 0;
        }
    }


    @Override
    public String toString() {
        return "SearchResult{" +
                "stats=" + stats +
                ", name='" + name + '\'' +
                ", uri='" + uri + '\'' +
                '}';
    }
}
