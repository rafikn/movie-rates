package nz.rafikn.movierates.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

/**
 * Created by rafik on 26/06/16.
 */
public class SearchResponse {
    private int total;
    private int page;
    @JsonProperty("per_page")
    private int perPage;
    private Paging paging;
    private Collection<SearchResult> data;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPerPage() {
        return perPage;
    }

    public void setPerPage(int perPage) {
        this.perPage = perPage;
    }

    public Paging getPaging() {
        return paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
    }

    public Collection<SearchResult> getData() {
        return data;
    }

    public void setData(Collection<SearchResult> data) {
        this.data = data;
    }

    public static class Paging {
        private String next;
        private String previous;
        private String first;
        private String last;

        public String getNext() {
            return next;
        }

        public void setNext(String next) {
            this.next = next;
        }

        public String getPrevious() {
            return previous;
        }

        public void setPrevious(String previous) {
            this.previous = previous;
        }

        public String getFirst() {
            return first;
        }

        public void setFirst(String first) {
            this.first = first;
        }

        public String getLast() {
            return last;
        }

        public void setLast(String last) {
            this.last = last;
        }
    }

    @Override
    public String toString() {
        return "SearchResponse{" +
                "total=" + total +
                ", page=" + page +
                ", perPage=" + perPage +
                ", paging=" + paging +
                ", data=" + data +
                '}';
    }
}
