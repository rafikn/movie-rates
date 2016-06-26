package nz.rafikn.movierates.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rafik on 26/06/16.
 */
public class MovieRecord {

    public static final String PARTITION_KEY = "hour";
    public static final String SORT_KEY = "movie_id";

    public static final String SOURCE_KEY = "source";
    public static final String SOURCE = "vimeo";
    public static final String VARIABLE_KEY = "variable";
    public static final String VARIABLE = "views";

    public static final String HOUR_TOTAL= "hour_total";

    private int hour;
    private int movieId;

    public MovieRecord() {
        this.map.put(SOURCE_KEY, SOURCE);
        this.map.put(VARIABLE_KEY, VARIABLE);
    }

    private Map<String, Object> map = new HashMap<>();


    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public long getTimestamp() {
        return map.get("timestamp") != null ? (int) map.get("timestamp") : 0l;
    }

    public void setTimestamp(long timestamp) {
        map.put("hour", timestamp);
    }

    public String getSource() {
        return map.get("source") != null ? (String) map.get("source") : "Vimeo";
    }

    public void setSource(String source) {
        map.put("source", source);
    }

    public String getVariable() {
        return map.get("variable") != null ? (String) map.get("variable") : "Views";
    }

    public void setVariable(String variable) {
        map.put("variable", variable);
    }

    public int getHourTotal() {
        return map.get("hour_total") != null ? (int) map.get("hour_total") : 0;
    }

    public void setHourTotal(int hourTotal) {
        map.put("hour_total", hourTotal);
    }

    public int getDeltaLastHour() {
        return map.get("delta_last_hour") != null ? (int) map.get("delta_last_hour") : 0;
    }

    public void setDeltaLastHour(int deltaLastHour) {
        map.put("delta_last_hour", deltaLastHour);
    }


    public String getMovieTitle() {
        return map.get("movie_title") != null ? (String) map.get("movie_title") : "";
    }

    public void setMovieTitle(String movieTitle) {
        map.put("movie_title", movieTitle);
    }

    public String getSearchTerm() {
        return map.get("search_term") != null ? (String) map.get("search_term") : "";
    }

    public void setSearchTerm(String searchTerm) {
        map.put("search_term", searchTerm);
    }

    public Map<String, Object> getMap() {
        return map;
    }

    @Override
    public String toString() {
        return "MovieRecord{" +
                "hour=" + hour +
                ", movie_id=" + movieId +
                ", info={title=" + map.get("movie_title") + "},{hour_total=" + map.get("hour_total") + "},{delta_last_hour=" + map.get("delta_last_hour") + "}" +
                '}';
    }
}
