package nz.rafikn.movierates.model;

import java.util.Calendar;

/**
 * Created by rafik on 26/06/16.
 */
public class MovieRecord {

    public static final String PARTITION_KEY = "title";
    public static final String SORT_KEY = "timestamp";

    public static final String SOURCE = "Vimeo";
    public static final String VARIABLE = "views";

    public static final String HOUR_TOTAL= "hour_total";

    private String title;
    private int movieId;
    private String timestamp;
    private String source;
    private String variable;
    private String searchTerm;
    private int hour;
    private int hourTotal;
    private int deltaLastHour;
    private long creationTimestamp;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getHourTotal() {
        return hourTotal;
    }

    public void setHourTotal(int hourTotal) {
        this.hourTotal = hourTotal;
    }

    public int getDeltaLastHour() {
        return deltaLastHour;
    }

    public void setDeltaLastHour(int deltaLastHour) {
        this.deltaLastHour = deltaLastHour;
    }

    public long getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(long creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    @Override
    public String toString() {
        return "MovieRecord{" +
                "title=" + title +
                ", timestamp=" + timestamp +
                ", info={{id="+ movieId +"}, {hour_total=" + hourTotal + "}, {delta_last_hour=" + deltaLastHour + "}" +
                '}';
    }


    public synchronized static MovieRecord build(String timestamp, int hourTotal, int delta, Movie movie) {

        MovieRecord record = new MovieRecord();

        record.setTitle(movie.getTitle());
        record.setTimestamp(timestamp);
        record.setMovieId(movie.getId());
        record.setSearchTerm(movie.getSearchTerm());
        record.setHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        record.setHourTotal(hourTotal);
        record.setDeltaLastHour(delta);
        record.setCreationTimestamp(Calendar.getInstance().getTimeInMillis());
        record.setSource(SOURCE);
        record.setVariable(VARIABLE);

        return record;
    }
}
