package srimalar.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Paging {
    @JsonProperty("limit")
    private int limit;
    @JsonProperty("offset")
    private long offset;
    @JsonProperty("total_records")
    private long totalRecords;
    @JsonProperty("size")
    private int size;

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public long getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(long totalRecords) {
        this.totalRecords = totalRecords;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
